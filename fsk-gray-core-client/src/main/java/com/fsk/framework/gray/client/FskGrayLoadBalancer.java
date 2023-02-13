package com.fsk.framework.gray.client;

import com.fsk.framework.gray.client.enums.GraySwitchEnum;
import com.fsk.framework.gray.client.enums.YesNoEnum;
import com.fsk.framework.gray.client.handler.GrayBizHandler;
import com.fsk.framework.gray.client.statistics.bean.GrayRequestBean;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.*;
import org.springframework.cloud.loadbalancer.core.NoopServiceInstanceListSupplier;
import org.springframework.cloud.loadbalancer.core.ReactorServiceInstanceLoadBalancer;
import org.springframework.cloud.loadbalancer.core.SelectedInstanceCallback;
import org.springframework.cloud.loadbalancer.core.ServiceInstanceListSupplier;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @ClassName: Fsk App
 * @Copyright: com.fsk
 * @Version: 1.0.0
 * @Author: Gary
 * @UpdateTime: 2022/8/30
 * @Describe: FskLoadBalancer.
 */
public class FskGrayLoadBalancer implements ReactorServiceInstanceLoadBalancer, EnvironmentAware {
    private static final Log log = LogFactory.getLog(FskGrayLoadBalancer.class);
    final AtomicInteger normalGrayPosition;
    final AtomicInteger normalPosition;
    final AtomicInteger grayPosition;
    final AtomicInteger npPosition;// 兜底
    private final String serviceId;
    private ObjectProvider<ServiceInstanceListSupplier> serviceInstanceListSupplierProvider;
    private static String currentServiceName;
    private final GrayBizHandler grayBizHandler;

    public FskGrayLoadBalancer(ObjectProvider<ServiceInstanceListSupplier> serviceInstanceListSupplierProvider, String serviceId, GrayBizHandler grayBizHandler) {
        this.serviceId = serviceId;
        this.serviceInstanceListSupplierProvider = serviceInstanceListSupplierProvider;
        this.normalPosition = new AtomicInteger((new Random()).nextInt(1000));
        this.normalGrayPosition = new AtomicInteger((new Random()).nextInt(1000));
        this.grayPosition = new AtomicInteger((new Random()).nextInt(1000));
        this.npPosition = new AtomicInteger((new Random()).nextInt(1000));
        this.grayBizHandler = grayBizHandler;
    }

    @Override
    public Mono<Response<ServiceInstance>> choose(Request request) {

        // 监控
        if (!"0".equals(grayBizHandler.getSwitch(this.getServiceName(request), GraySwitchEnum.ACTIVE_STEP))) {
            RequestDataContext context = (RequestDataContext) request.getContext();
            URI uri = context.getClientRequest().getUrl();
            String path = uri.getPath();
            GrayRequestBean grayRequestBean = new GrayRequestBean();
            grayRequestBean.setGatewayIp(uri.getHost());
            grayRequestBean.setCurrentServiceName(currentServiceName);
            grayRequestBean.setServiceName(path.substring(path.indexOf("/", 1) + 1, path.indexOf("/", 6)));
            grayRequestBean.setRequestMs(System.currentTimeMillis());
            BeforeGrayEndport.addUpdate(grayRequestBean);
        }

        ServiceInstanceListSupplier supplier = (ServiceInstanceListSupplier) this.serviceInstanceListSupplierProvider.getIfAvailable(NoopServiceInstanceListSupplier::new);
        return supplier.get(request).next().map((serviceInstances) -> {
            return this.processInstanceResponse(supplier, serviceInstances, request);
        });
    }

    private Response<ServiceInstance> processInstanceResponse(ServiceInstanceListSupplier supplier, List<ServiceInstance> serviceInstances, Request request) {
        Response<ServiceInstance> serviceInstanceResponse = this.getInstanceResponse(serviceInstances, request);
        if (supplier instanceof SelectedInstanceCallback && serviceInstanceResponse.hasServer()) {

            ((SelectedInstanceCallback) supplier).selectedServiceInstance((ServiceInstance) serviceInstanceResponse.getServer());
        }

        // 监控
        String serviceName = this.getServiceName(request);
        if (!"0".equals(grayBizHandler.getSwitch(this.getServiceName(request), GraySwitchEnum.ACTIVE_STEP))) {
            RequestDataContext context = (RequestDataContext) request.getContext();
            URI uri = context.getClientRequest().getUrl();
            GrayRequestBean grayRequestBean = new GrayRequestBean();
            grayRequestBean.setCurrentServiceName(currentServiceName);
            grayRequestBean.setGatewayIp(uri.getHost());
            grayRequestBean.setServiceName(serviceName);
            grayRequestBean.setTotalCounter(BeforeGrayEndport.get(serviceName));
            grayRequestBean.setServiceIp(serviceInstanceResponse.getServer().getHost());
            grayRequestBean.setServicePort(serviceInstanceResponse.getServer().getPort());
            grayRequestBean.setIsGray(serviceInstanceResponse.getServer().getPort() == 8889 ? "1" : "0");
            grayRequestBean.setRequestMs(System.currentTimeMillis());
            AfterGrayEndport.addUpdate(grayRequestBean);
        }

        return serviceInstanceResponse;
    }

    private Response<ServiceInstance> getInstanceResponse(List<ServiceInstance> instances, Request request) {
        if (instances.isEmpty()) {
            if (log.isWarnEnabled()) {
                log.warn("No servers available for service: " + this.serviceId);
            }
            return new EmptyResponse();
        } else {
            // 当前请求方的Ip
            String currentRemoteRequestIp = this.getCurrentRemoteRequestIp(request);
            String serviceName = this.getServiceName(request);
            int instanceSize = instances.size();
            // 灰度节点流量开关
            Boolean switchGrayTraffic = YesNoEnum.YES.getIndex().equals(grayBizHandler.getSwitch(serviceName, GraySwitchEnum.TRAFFIC_SWITCH));

            if (switchGrayTraffic) {
                // 灰度节点 和 非灰度节点
                Map<String, List<ServiceInstance>> availableNodes = this.getAvailableNodes(instances, serviceName);

                // A/B测试开关
                Boolean abTest = YesNoEnum.YES.getIndex().equals(grayBizHandler.getSwitch(serviceName, GraySwitchEnum.AB_SWITCH));
                if (abTest) {
                    // 测试人员测试
                    return this.abTest(availableNodes, request, serviceName, currentRemoteRequestIp);
                } else {
                    // AB内部人员测试结束后，打开 stopTest，
                    // 通过hash的方式做流量递增
                    // 首先获取用户请求的IP地址，并做对IP地址的hash值取模
                    if (StringUtils.isBlank(currentRemoteRequestIp)) {
                        // 如果请求头currentRemoteRequestIp未能透传，兜底一个可用节点
                        int pos = Math.abs(this.npPosition.incrementAndGet());
                        ServiceInstance npInstance = instances.get(pos % instances.size());
                        return new DefaultResponse(npInstance);
                    }
                    int mod = (currentRemoteRequestIp.hashCode() & Integer.MAX_VALUE) % grayBizHandler.size();
                    // 动态计算取值范围
                    Integer range = Integer.parseInt(grayBizHandler.getSwitch(serviceName, GraySwitchEnum.MOD_RANGE));
                    int rangeLength = range / instanceSize;
                    List<Integer> mods = new ArrayList<>();
                    for (int i = 0; i <= rangeLength; i++) {
                        mods.add(i);
                    }
                    grayBizHandler.setMods(serviceName, mods);

                    // 两种类型进灰度：1)取模范围内 2)包含请求头和授权ip地址的
                    if (grayBizHandler.containsMods(serviceName, mod) || this.checkRequestGray(request)) {
                        // 返回灰度节点
                        return new DefaultResponse(availableNodes.get("GRAY").get(0));
                    } else {
                        // 先从 instances 中剔除 灰度机器 ，然后对剩余的正式节点做轮询，并返回正式节点
                        List<ServiceInstance> non_gray = availableNodes.get("NON_GRAY");
                        int pos = Math.abs(this.normalGrayPosition.incrementAndGet());
                        return new DefaultResponse(non_gray.get(pos % non_gray.size()));
                    }
                }
            } else {
                // 把正式节点的全部流量 切到 灰度机器 的开关
                if (StringUtils.equals(grayBizHandler.getSwitch(serviceName, GraySwitchEnum.NORMAL_SWITCH), YesNoEnum.YES.getIndex())) {
                    // 全部进灰度机器
                    // switchGrayTraffic 关闭之前，先打开 flag
                    Map<String, List<ServiceInstance>> availableNodes = this.getAvailableNodes(instances, serviceName);
                    return new DefaultResponse(availableNodes.get("GRAY").get(0));
                } else {
                    // 未触发灰度发布的正常逻辑，正常节点轮询算法
                    int pos = Math.abs(this.normalPosition.incrementAndGet());
                    ServiceInstance instance = instances.get(pos % instances.size());
                    return new DefaultResponse(instance);
                }
            }
        }
    }

    /**
     * 获取当前可用节点
     *
     * @param instances
     * @param serviceName
     * @return key: GRAY
     * key: NON_GRAY
     */
    public Map<String, List<ServiceInstance>> getAvailableNodes(List<ServiceInstance> instances, String serviceName) {
        // 把指向灰度的流量请求切到正式机器
        String grayIp = grayBizHandler.getGrayHostPortByGatewayAndServiceName(serviceName);
        // 排除灰度服务器
        final List<ServiceInstance> normalInstances = new ArrayList<>();
        final List<ServiceInstance> grayInstances = new ArrayList<>();
        for (ServiceInstance instance : instances) {
            final String hostPort = instance.getHost() + ":" + instance.getPort();
            if (!hostPort.equals(grayIp)) {
                normalInstances.add(instance);
            } else {
                grayInstances.add(instance);
            }
        }
        Map<String, List<ServiceInstance>> map = new HashMap<>();
        map.put("GRAY", grayInstances);
        map.put("NON_GRAY", normalInstances);
        return map;
    }

    private Response<ServiceInstance> abTest(Map<String, List<ServiceInstance>> availableNodes, Request request, String serviceName, String currentRemoteRequestIp) {
        // 获取非灰度服务器节点
        final List<ServiceInstance> normalInstances = availableNodes.get("NON_GRAY");
        // 轮询切换的非灰度服务器
        int pos = Math.abs(this.grayPosition.incrementAndGet());
        ServiceInstance normalInstance = normalInstances.get(pos % normalInstances.size());
        // A/B test 开关
        Boolean abTest = YesNoEnum.YES.getIndex().equals(grayBizHandler.getSwitch(serviceName, GraySwitchEnum.AB_SWITCH));
        if (abTest) {
            // 开启了 ab 测试
            boolean requestGray = this.checkRequestGray(request);
            if (requestGray) {
                // 当前流量切至灰度服务器
                List<ServiceInstance> grayInstances = availableNodes.get("GRAY");
                boolean var = grayInstances == null || grayInstances.isEmpty();
                if (var && log.isWarnEnabled()) {
                    log.warn("No gray servers available for service: " + this.serviceId);
                }
                return var ? new EmptyResponse() : new DefaultResponse(grayInstances.get(0));
            }
        }
        // 返回正常机器节点
        return new DefaultResponse(normalInstance);
    }

    private String getServiceName(Request request) {
        RequestDataContext context = (RequestDataContext) request.getContext();
        URI uri = context.getClientRequest().getUrl();
        String path = uri.getPath();
        String serviceName = path.substring(path.indexOf("/", 1) + 1, path.indexOf("/", 6));
        return serviceName;
    }

    /**
     * 校验当前请求是否是请求灰度节点
     *
     * @param request
     * @return
     */
    private boolean checkRequestGray(Request request) {
        RequestDataContext context = (RequestDataContext) request.getContext();
        HttpHeaders headers = context.getClientRequest().getHeaders();
        if (StringUtils.isNotBlank(headers.getFirst(grayBizHandler.getGrayHeader()))) {
            log.info(">>>>> Enter Grayscale service - gray header");
            return true;
        } else if (grayBizHandler.accessUserIps(headers.getFirst("GRAY_REMOTE_REQUEST_IP"))) {
            log.info(">>>>> Enter Grayscale service - ip");
            return true;
        } else {
            log.info(">>>>> Non-grayscale service request");
            return false;
        }
    }

    @Override
    public void setEnvironment(Environment environment) {
        currentServiceName = environment.getProperty("spring.application.name");
    }

    private String getCurrentRemoteRequestIp(Request request) {
        RequestDataContext requestContext = (RequestDataContext) request.getContext();
        HttpHeaders headers = requestContext.getClientRequest().getHeaders();
        String grayRemoteRequestIp = headers.getFirst("GRAY_REMOTE_REQUEST_IP");
        return grayRemoteRequestIp;
    }
}

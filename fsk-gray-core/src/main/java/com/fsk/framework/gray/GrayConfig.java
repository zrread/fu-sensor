package com.fsk.framework.gray;

import com.fsk.framework.gray.bean.GrayInDto;
import com.fsk.framework.gray.enums.YesNoEnum;
import com.fsk.framework.gray.exception.GrayException;
import com.fsk.framework.gray.model.GrayNode;
import com.fsk.framework.pubconfig.entity.GrayAddressEntity;
import com.fsk.framework.pubconfig.entity.KeyEntities;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.ansi.AnsiColor;
import org.springframework.boot.ansi.AnsiOutput;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import javax.annotation.PostConstruct;
import java.io.PrintStream;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @ClassName: Fsk App
 * @Copyright: com.fsk
 * @Version: 1.0.0
 * @Author: Gary
 * @UpdateTime: 2022/8/30
 * @Describe: GrayConfig is obsolete. Since 1.0.2, it has
 * been replaced by {@link com.fsk.framework.gray.handler.GrayContextHolder}.
 */
@Slf4j
@Deprecated
//@Configuration
public class GrayConfig implements ApplicationContextAware, InitializingBean, DisposableBean {
    /**
     * 客户端请求 ip
     * Deprecated reason : instead of using headers
     */
    @Deprecated
    private static ThreadLocal<String> currentRequestInfo = new ThreadLocal<>();

    /**
     * 灰度测试请求头 key
     */
    protected final static String grayHeader = "A-gray-header";

    /**
     * AB Test 开关
     * key: serviceName, 只存当前网关下的服务 <br/>
     * val: 开关状态：1:开，0：关 <br/>
     */
    public final static ConcurrentHashMap<String, String> AB_SWITCH = new ConcurrentHashMap<>();

    /**
     * 灰度大屏监控开关
     */
    public final static ConcurrentHashMap<String, String> GRAY_PANEL_SWITCH = new ConcurrentHashMap<>();

    /**
     * 灰度节点流量开关<br/>
     * 把灰度节点的全部流量切到正式机器的开关
     * key: serviceName, 只存当前网关下的服务 <br/>
     * val: 开关状态：1:开，0：关 <br/>
     */
    public final static ConcurrentHashMap<String, String> GRAY_TRAFFIC_SWITCH = new ConcurrentHashMap<>();

    /**
     * 正式节点流量开关<br/>
     * 把正式节点的全部流量切到灰度机器的开关<br/>
     * key: service name
     * value: 开关状态：1:开，0：关 <br/>
     */
    public final static ConcurrentHashMap<String, String> NORMAL_TRAFFIC_SWITCH = new ConcurrentHashMap<>();

    /**
     * 灰度节点列表 <br/>
     * key: gateWayName#serviceName <br/>
     * val: ip:port <br/>
     */
    public final static ConcurrentHashMap<String, String> GRAY_NODE_IPS = new ConcurrentHashMap<>();
    /**
     * 灰度用户 ip 列表
     */
    public static List<String> GRAY_USER_IPS = new ArrayList<>();

    /**
     * 当前网关的名称 spring.application
     */
    protected final static AtomicReference<String> CURRENT_GATEWAY_NAME = new AtomicReference<>("0");

    /**
     * 当前网关 在 nacos 上注册成功后的 IP
     */
    protected final static AtomicReference<String> CURRENT_GATEWAY_HOST = new AtomicReference<>("0");

    /**
     * 当前网关 在 nacos 上注册成功后的 PORT
     */
    protected final static AtomicReference<String> CURRENT_GATEWAY_PORT = new AtomicReference<>("0");

    private ApplicationContext context;

    private static final String[] GRAY_MARKER = new String[]{" __    ___    __    _    ", "/ /`_ | |_)  / /\\  \\ \\_/ ", "\\_\\_/ |_| \\ /_/--\\  |_|  "};

    private final static String EXIT = ">>>> Gray exit success ";

    public final static ConcurrentHashMap<String, Integer> GRAY_MOD_RANGE = new ConcurrentHashMap<String, Integer>();

    public final static ConcurrentHashMap<String, List<Integer>> GRAY_MODS = new ConcurrentHashMap<String, List<Integer>>();

    public final static int SIZE = 100;

    public static String OFFLINE = "http://localhost:8080";
    public static String SERVER = "http://localhost:8080";
    public static String REGISTER = "http://localhost:8080";

    public GrayConfig() {
    }

    public static void setCurrentRequest(String requestIp) {
        currentRequestInfo.set(requestIp);
    }

    public static void removeCurrentRequest() {
        currentRequestInfo.remove();
    }

    public static String getCurrentRequest() {
        return currentRequestInfo.get();
    }

    public void setGrayNodeIps(ConcurrentHashMap<String, String> grayNodeIps) {
        GRAY_NODE_IPS.putAll(grayNodeIps);
    }

    public void setGrayUserIps(List<String> grayUserIps) {
        GRAY_USER_IPS.addAll(grayUserIps);
    }

    public void setCurrentGatewayName(String currentGatewayName) {
        CURRENT_GATEWAY_NAME.set(currentGatewayName);
    }

    public void setCurrentGatewayHost(String currentGatewayHost) {
        CURRENT_GATEWAY_HOST.set(currentGatewayHost);
    }

    public void setCurrentGatewayPort(String currentGatewayPort) {
        CURRENT_GATEWAY_PORT.set(currentGatewayPort);
    }

    public void setGrayTrafficSwitch(ConcurrentHashMap<String, String> trafficSwitch) {
        GRAY_TRAFFIC_SWITCH.putAll(trafficSwitch);
    }

    public void setAbSwitch(ConcurrentHashMap<String, String> abSwitch) {
        AB_SWITCH.putAll(abSwitch);
    }

    public void setNormalTrafficSwitch(ConcurrentHashMap<String, String> normalTrafficSwitch) {
        NORMAL_TRAFFIC_SWITCH.putAll(normalTrafficSwitch);
    }

    @Deprecated
    public void setGrayModRange(String serviceName, Integer modRange) {
        GRAY_MOD_RANGE.put(serviceName, modRange);
    }

    public static String getGrayPanelSwitch(String serviceName) {
        return GRAY_PANEL_SWITCH.get(serviceName);
    }

    public void setGrayPanelSwitch(String serviceName, String panelSwitch) {
        GRAY_PANEL_SWITCH.put(serviceName, panelSwitch);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.context = applicationContext;
    }

    @PostConstruct
    public void init() {
        PrintStream out = null;
        try {
            out = new PrintStream(System.out);
            out.println("=========================");
            out.println();
            for (String mark : GRAY_MARKER) {
                out.println(AnsiOutput.toString(AnsiColor.CYAN, mark, AnsiColor.DEFAULT));
            }
            out.println();
            out.println("=========================");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
            }
        } catch (Exception e) {
            if (out != null) {
                out.close();
            }
        }
    }


    /**
     * Refresh the memory value of TRAFFIC_SWITCH
     *
     * @param inDto
     * @return
     */
    public String changeTrafficSwitch(GrayInDto inDto) {
        String trafficSwitch = inDto.getTrafficSwitch();
        if (YesNoEnum.NO.getIndex().equals(trafficSwitch)) {
            ConcurrentHashMap<String, String> hashMap = new ConcurrentHashMap<>();
            hashMap.put(inDto.getService(), YesNoEnum.YES.getIndex());
            setNormalTrafficSwitch(hashMap);
        }

        GRAY_TRAFFIC_SWITCH.replace(inDto.getService(), inDto.getTrafficSwitch());
        return inDto.getService();
    }

    /**
     * Refresh the memory value of AB_SWITCH
     *
     * @param inDto
     * @return
     */
    public String changeAbSwitch(GrayInDto inDto) {
        AB_SWITCH.replace(inDto.getService(), inDto.getAbSwitch());
        return inDto.getAbSwitch();
    }

    /**
     * Refresh the memory value of GRAY_NODE_IPS
     *
     * @param inDto
     * @return
     */
    public int changeGrayIps(GrayInDto inDto) {
        String effective = inDto.getEffective();
        List<GrayNode> grayNodeList = inDto.getGrayNodeList();
        if (YesNoEnum.YES.getIndex().equals(effective)) {
            for (GrayNode oprGrayNode : grayNodeList) {
                String key = oprGrayNode.getGateway() + "#" + oprGrayNode.getService();
                GRAY_NODE_IPS.put(key, oprGrayNode.getIp());
            }
        } else if (YesNoEnum.NO.getIndex().equals(effective)) {
            List<String> delKeys = com.google.common.collect.Lists.newArrayList();
            for (GrayNode oprGrayNode : grayNodeList) {
                delKeys.add(oprGrayNode.getGateway() + "#" + oprGrayNode.getService());
            }

            Iterator<Map.Entry<String, String>> it = GRAY_NODE_IPS.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<String, String> e = it.next();
                if (delKeys.contains(e.getKey())) {
                    it.remove();
                }
            }
        } else {
            log.error("Failed to change the gray ips, parameter illegal: effective:{}", effective);
            throw new GrayException("Failed to change the gray ips", "999");
        }

        return grayNodeList.size();
    }

    @Override
    public void destroy() {

    }

    /**
     * Refresh the memory value of GRAY_USER_IPS
     *
     * @param ipList
     * @param effective
     * @return
     */
    public int changeGrayUserIps(List<String> ipList, String effective) {
        if (YesNoEnum.YES.getIndex().equals(effective)) {
            List<String> tmpList = Lists.newArrayList(GRAY_USER_IPS);
            tmpList.addAll(ipList);
            GRAY_USER_IPS = new ArrayList<>(new HashSet<>(tmpList));
        } else if (YesNoEnum.NO.getIndex().equals(effective)) {
            Iterator<String> it = GRAY_USER_IPS.iterator();
            while (it.hasNext()) {
                String currentIp = it.next();
                if (ipList.contains(currentIp)) {
                    it.remove();
                }
            }
        } else {
            log.error("Failed to change the gray user ips, parameter illegal: effective:{}", effective);
            throw new GrayException("Failed to change the gray user ips", "999");
        }

        return ipList.size();
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        KeyEntities keyEntities = this.context.getBean(KeyEntities.class);
        GrayAddressEntity grayAddress = keyEntities.getGrayAddress();
        OFFLINE = grayAddress.getOffline();
        SERVER = grayAddress.getServer();
        REGISTER = grayAddress.getRegister();
    }

}

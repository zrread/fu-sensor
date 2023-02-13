package com.fsk.framework.gray.listener;

import com.alibaba.cloud.nacos.NacosDiscoveryProperties;
import com.fsk.framework.gray.enums.YesNoEnum;
import com.fsk.framework.gray.event.RegisterApplicationEvent;
import com.fsk.framework.gray.event.RegisterEventPublisher;
import com.fsk.framework.gray.handler.GrayContextHolder;
import com.fsk.framework.gray.model.ServerNode;
import com.google.common.collect.Lists;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.cloud.client.discovery.event.InstanceRegisteredEvent;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationListener;
import reactor.core.publisher.Flux;

import java.util.Iterator;
import java.util.List;

/**
 * @ClassName: Fsk App
 * @Copyright: com.fsk
 * @Version: 1.0.0
 * @Author: Gary
 * @UpdateTime: 2022/9/4
 * @Describe: GrayRegisterListener.
 */
public class GrayRegisterListener implements ApplicationListener<InstanceRegisteredEvent>, ApplicationContextAware {

    private ApplicationContext context;

    @Override
    public void onApplicationEvent(InstanceRegisteredEvent instanceRegisteredEvent) {
        boolean flag = true;
        GrayRegisterProcessor grayRegisterProcessor = null;
        GrayContextHolder grayContextHolder = null;
        try {
            grayContextHolder = context.getBean(GrayContextHolder.class);
            grayRegisterProcessor = context.getBean(GrayRegisterProcessor.class);
        } catch (NoSuchBeanDefinitionException nbe) {
            flag = false;
        }
        if (flag) {
            // Information about the current gateway
            NacosDiscoveryProperties config = (NacosDiscoveryProperties) instanceRegisteredEvent.getConfig();
            String ip = config.getIp();
            String port = String.valueOf(config.getPort());
            String ipPort = ip + ":" + port; // 2022-11-22 13:55:00 废弃 default 8889 for gray server
            String gatewayName = config.getService();

            // Set the information about the current gateway
            grayContextHolder.setCurrentGatewayHost(ip);
            grayContextHolder.setCurrentGatewayName(gatewayName);
            grayContextHolder.setCurrentGatewayPort(port);

            // Server node information obtained from the boot route
            List<ServerNode> tmpServerNodes = Lists.newArrayList();
            Flux<RouteDefinition> routeDefinitions = grayRegisterProcessor.getPropertiesRouteDefinitionLocator().getRouteDefinitions();
            Iterable<RouteDefinition> toIterable = routeDefinitions.toIterable();
            Iterator<RouteDefinition> iterator = toIterable.iterator();
            while (iterator.hasNext()) {
                RouteDefinition routeDefinition = iterator.next();
                String serviceName = routeDefinition.getId();
                // 写入数据库
                ServerNode serverNode = ServerNode.builder()
                        .gateway(gatewayName)
                        .url(ipPort)
                        .service(serviceName)
                        .abSwitch(YesNoEnum.NO.getIndex())
                        .trafficSwitch(YesNoEnum.NO.getIndex())
                        .normalSwitch(YesNoEnum.NO.getIndex())
                        .activeStep(YesNoEnum.NO.getIndex())
                        .mods(YesNoEnum.NO.getIndex())
                        .build();
                tmpServerNodes.add(serverNode);
            }

            // register to remote
            RegisterEventPublisher.publish(new RegisterApplicationEvent("Register Event", tmpServerNodes));
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.context = applicationContext;
    }
}

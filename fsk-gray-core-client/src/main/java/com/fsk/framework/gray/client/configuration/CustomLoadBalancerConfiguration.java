package com.fsk.framework.gray.client.configuration;

import com.fsk.framework.gray.client.FskGrayLoadBalancer;
import com.fsk.framework.gray.client.handler.GrayBizHandler;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.loadbalancer.core.ReactorLoadBalancer;
import org.springframework.cloud.loadbalancer.core.ServiceInstanceListSupplier;
import org.springframework.cloud.loadbalancer.support.LoadBalancerClientFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

/**
 * @ClassName: Fsk App
 * @Copyright: com.fsk
 * @Version: 1.0.0
 * @Author: Gary
 * @UpdateTime: 2022/8/30
 * @Describe: 灰度发布SLB算法.
 */
@ConditionalOnProperty(prefix = "fsk.global.gray", name = "enabled", havingValue = "true", matchIfMissing = true)
@Configuration
public class CustomLoadBalancerConfiguration {
    @Bean
    ReactorLoadBalancer<ServiceInstance> randomLoadBalancer(Environment environment,
                                                            LoadBalancerClientFactory loadBalancerClientFactory,
                                                            GrayBizHandler grayBizHandler) {
        String name = environment.getProperty(LoadBalancerClientFactory.PROPERTY_NAME);
        return new FskGrayLoadBalancer(loadBalancerClientFactory.getLazyProvider(name, ServiceInstanceListSupplier.class), name, grayBizHandler);
    }
}

package com.fsk.framework.gray.configuration;

import com.fsk.framework.gray.actuator.health.GrayHealthIndicator;
import com.fsk.framework.gray.client.SensorClient;
import com.fsk.framework.gray.client.service.Client;
import com.fsk.framework.gray.condition.AppTypeCondition;
import com.fsk.framework.gray.event.RegisterEventPublisher;
import com.fsk.framework.gray.event.RegisterListener;
import com.fsk.framework.gray.filter.GrayFilter;
import com.fsk.framework.gray.handler.GrayBizHandler;
import com.fsk.framework.gray.handler.GrayContextHolder;
import com.fsk.framework.gray.handler.HeartBeatHandler;
import com.fsk.framework.gray.listener.NacosPubconfigListener;
import com.fsk.framework.pubconfig.entity.KeyEntities;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.autoconfigure.health.ConditionalOnEnabledHealthIndicator;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;

/**
 * @ClassName: Fsk App
 * @Copyright: com.fsk
 * @Version: 1.0.0
 * @Author: Gary
 * @UpdateTime: 2022/9/27
 * @Describe: GrayAutoConfiguration.
 */
@ConditionalOnProperty(prefix = "fsk.global.gray", name = "enabled", havingValue = "true", matchIfMissing = true)
@Configuration
public class GrayAutoConfiguration implements ApplicationContextAware {

    private ApplicationContext ctx;

    @Bean
    public RegisterEventPublisher registerEventPublisher() {
        return new RegisterEventPublisher();
    }

    @Bean
    public RegisterListener registerListener() {
        return new RegisterListener();
    }

    @Bean
    public Client sensorClient() {
        return new SensorClient();
    }

    @Bean
    public HeartBeatHandler heartBeatHandler(Client client,
                                             @Value("${fsk.global.gray.client.beat-interval:10}") long beatInterval) {
        return new HeartBeatHandler(client, beatInterval);
    }

    @Conditional(AppTypeCondition.class)
    @Bean
    public GrayFilter initGrayFilter() {
        return new GrayFilter();
    }

    @Bean
    public GrayBizHandler grayBizHandler(GrayContextHolder grayContextHolder) {
        return new GrayBizHandler(grayContextHolder);
    }

    @Bean
    public GrayContextHolder grayContextHolder(KeyEntities keyEntities) {
        return new GrayContextHolder(new GrayContextHolder.GrayStatusContext(), ctx, keyEntities);
    }

    @Bean
    public NacosPubconfigListener nacosPubconfigListener(GrayBizHandler grayBizHandler,
                                                         @Value("${fsk.${spring.profiles.active}.nacos.server-addr}") String SERVER_ADDR,
                                                         @Value("${fsk.${spring.profiles.active}.nacos.username}") String USERNAME,
                                                         @Value("${fsk.${spring.profiles.active}.nacos.password}") String PASSWORD) {
        return new NacosPubconfigListener(grayBizHandler, SERVER_ADDR, USERNAME, PASSWORD);
    }

    @Bean
    @ConditionalOnEnabledHealthIndicator("fu-sensor")
    public HealthIndicator GrayHealthIndicator() {
        return new GrayHealthIndicator();
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.ctx = applicationContext;
    }
}

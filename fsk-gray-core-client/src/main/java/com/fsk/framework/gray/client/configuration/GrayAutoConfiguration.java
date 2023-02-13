package com.fsk.framework.gray.client.configuration;

import com.fsk.framework.gray.client.handler.GrayContextHolder;
import com.fsk.framework.gray.client.filter.GrayFilter;
import com.fsk.framework.gray.client.handler.GrayBizHandler;
import com.fsk.framework.gray.client.nacos.NacosPubconfigListener;
import com.fsk.framework.pubconfig.entity.KeyEntities;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @ClassName: Fsk App
 * @Copyright: com.fsk
 * @Version: 1.0.0
 * @Author: Gary
 * @UpdateTime: 2022/11/15
 * @Describe: GrayAutoConfiguration.
 */
@ConditionalOnProperty(prefix = "fsk.global.gray", name = "enabled", havingValue = "true", matchIfMissing = true)
@Configuration
public class GrayAutoConfiguration implements ApplicationContextAware {

    private ApplicationContext ctx;

    @Bean
    public GrayFilter initGrayFilter() {
        return new GrayFilter();
    }

    @Bean
    public GrayBizHandler initGrayBizHandler(GrayContextHolder grayContextHolder) {
        return new GrayBizHandler(grayContextHolder);
    }

    @Bean
    public GrayContextHolder grayContextHolder(KeyEntities keyEntities) {
        return new GrayContextHolder(new GrayContextHolder.GrayStatusContext(), ctx, keyEntities);
    }

    @Bean
    public NacosPubconfigListener initFskEmbedServer(GrayBizHandler grayBizHandler,
                                                     @Value("${fsk.${spring.profiles.active}.nacos.server-addr}") String SERVER_ADDR,
                                                     @Value("${fsk.${spring.profiles.active}.nacos.username}") String USERNAME,
                                                     @Value("${fsk.${spring.profiles.active}.nacos.password}") String PASSWORD) {
        return new NacosPubconfigListener(grayBizHandler, SERVER_ADDR, USERNAME, PASSWORD);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.ctx = applicationContext;
    }
}

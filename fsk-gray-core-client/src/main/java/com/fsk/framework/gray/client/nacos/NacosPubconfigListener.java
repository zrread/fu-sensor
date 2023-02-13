package com.fsk.framework.gray.client.nacos;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.nacos.api.NacosFactory;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.config.listener.Listener;
import com.alibaba.nacos.api.exception.NacosException;
import com.fsk.framework.gray.client.bean.AllGrayServersPublishingExecStatusContext;
import com.fsk.framework.gray.client.exception.GrayClientException;
import com.fsk.framework.gray.client.handler.GrayBizHandler;
import com.fsk.framework.gray.client.threads.ThreadBean;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @ClassName: Fsk App
 * @Copyright: com.fsk
 * @Version: 1.0.0
 * @Author: Gary
 * @UpdateTime: 2022/11/15
 * @Describe: NacosPubconfigListener.
 */
@Slf4j
public class NacosPubconfigListener implements InitializingBean {

    private static final String DEFAULT_GROUP = "DEFAULT_GROUP";
    private static final String DATA_ID = "fsk-gray-publish-metadata.json";
    private static final String JSON = "json";
    private final List<Listener> listeners = new ArrayList<>();
    private final Properties properties = new Properties();
    private final ConfigService configService;
    private final ExecutorService executorService;
    private GrayBizHandler grayBizHandler;

    public NacosPubconfigListener(GrayBizHandler grayBizHandler, String SERVER_ADDR, String USERNAME, String PASSWORD) {
        properties.put("serverAddr", SERVER_ADDR);
        properties.put("username", USERNAME);
        properties.put("password", PASSWORD);
        properties.put("groupId", DEFAULT_GROUP);
        properties.put("dataId", DATA_ID);
        properties.put("dataType", JSON);

        executorService = Executors.newFixedThreadPool(8);
        this.grayBizHandler = grayBizHandler;

        try {
            configService = NacosFactory.createConfigService(properties);
        } catch (NacosException ne) {
            log.error("Failed to get gray status config from grayscale service center!", ne);
            throw new GrayClientException("777", "Failed to get gray status config from grayscale service center!");
        }

        if (!configService.getServerStatus().equals("UP")) {
            log.error("Gray publish metadata connect failed!");
            throw new GrayClientException("777", "Gray publish metadata connect failed! Reason is serverStatus of gray is DOWN !");
        }

        try {
            String config = configService.getConfig(properties.getProperty("dataId"), properties.getProperty("groupId"), 8000L);
            AllGrayServersPublishingExecStatusContext context = JSONObject.parseObject(config, AllGrayServersPublishingExecStatusContext.class);
            executorService.execute(new ThreadBean(grayBizHandler, context));
        } catch (NacosException ne) {
            log.error("Gray publish metadata init failed!", ne);
            throw new GrayClientException("777", "Gray publish metadata init failed!");
        }

    }

    @PostConstruct
    public void postConstruct() {
        Listener listener = new Listener() {
            @Override
            public void receiveConfigInfo(String configInfo) {
                final AllGrayServersPublishingExecStatusContext var = JSONObject.parseObject(configInfo, AllGrayServersPublishingExecStatusContext.class);
                // TODO 也可以换成事件监听器
                log.error(">>>>>>>>>>> Gray status config listener start <<<<<<<<<<<<");
                executorService.execute(new ThreadBean(grayBizHandler, var));
                log.error(">>>>>>>>>>> Gray status config is now:{}", configInfo);
            }

            @Override
            public Executor getExecutor() {
                return executorService;
            }
        };
        listeners.add(listener);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        // 注册配置监听器
        listeners.forEach(item -> {
            try {
                configService.addListener(DATA_ID, DEFAULT_GROUP, item);
            } catch (NacosException ne) {
                log.error("Failed to register listeners of pubconfig!", ne);
                throw new GrayClientException("777", "Failed to register listeners of pubconfig!");
            }
        });
    }
}

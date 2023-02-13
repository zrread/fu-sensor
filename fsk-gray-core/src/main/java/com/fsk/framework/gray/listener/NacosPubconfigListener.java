package com.fsk.framework.gray.listener;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.nacos.api.NacosFactory;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.config.listener.Listener;
import com.alibaba.nacos.api.exception.NacosException;
import com.fsk.framework.gray.bean.AllGrayServersPublishingExecStatusContext;
import com.fsk.framework.gray.exception.GrayException;
import com.fsk.framework.gray.handler.GrayBizHandler;
import com.fsk.framework.gray.threads.ThreadBean;
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

        try {
            configService = NacosFactory.createConfigService(properties);
        } catch (NacosException ne) {
            log.error("Failed to get gray status config from grayscale service center!", ne);
            throw new GrayException("777", "Failed to get gray status config from grayscale service center!");
        }

        executorService = Executors.newFixedThreadPool(8);
        this.grayBizHandler = grayBizHandler;

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
                throw new GrayException("777", "Failed to register listeners of pubconfig!");
            }
        });
    }
}

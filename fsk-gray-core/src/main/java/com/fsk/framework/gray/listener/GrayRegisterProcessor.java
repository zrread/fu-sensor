package com.fsk.framework.gray.listener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.config.PropertiesRouteDefinitionLocator;
import org.springframework.stereotype.Component;

/**
 * @ClassName: Fsk App
 * @Copyright: com.fsk
 * @Version: 1.0.0
 * @Author: Gary
 * @UpdateTime: 2022/9/4
 * @Describe: GrayRegisterProcessor.
 */
@Component
public class GrayRegisterProcessor {

    @Autowired
    private PropertiesRouteDefinitionLocator propertiesRouteDefinitionLocator;
    @Value("${spring.application.name}")

    protected PropertiesRouteDefinitionLocator getPropertiesRouteDefinitionLocator() {
        return this.propertiesRouteDefinitionLocator;
    }
}

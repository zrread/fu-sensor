package com.fsk.framework.gray.condition;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

import java.util.Properties;

/**
 * @ClassName: Fsk App
 * @Copyright: com.fsk
 * @Version: 1.0.0
 * @Author: Gary
 * @UpdateTime: 2022/11/14
 * @Describe: AppTypeCondition.
 */
public class AppTypeCondition implements Condition {
    @Override
    public boolean matches(ConditionContext conditionContext, AnnotatedTypeMetadata annotatedTypeMetadata) {
        String appType = (conditionContext.getEnvironment().getProperty("fsk.app.type"));
        return "FSK_GATEWAY".equalsIgnoreCase(appType);
    }

    public static void main(String[] args) {
        Properties properties = System.getProperties();
        System.out.println(properties);
    }
}

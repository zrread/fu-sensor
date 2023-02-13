package com.fsk.framework.gray.event;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;

/**
 * @ClassName: Fsk App
 * @Copyright: com.fsk
 * @Version: 1.0.0
 * @Author: Gary
 * @UpdateTime: 2022/12/10
 * @Describe: RegisterEventPublisher.
 */
public class RegisterEventPublisher implements ApplicationEventPublisherAware {

    private static ApplicationEventPublisher eventPublisher;

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        RegisterEventPublisher.eventPublisher = applicationEventPublisher;
    }

    public static void publish(RegisterApplicationEvent event) {
        eventPublisher.publishEvent(event);
    }
}

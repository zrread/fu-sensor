package com.fsk.framework.gray.event;

import com.fsk.framework.gray.handler.HeartBeatHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;

/**
 * @ClassName: Fsk App
 * @Copyright: com.fsk
 * @Version: 1.0.0
 * @Author: Gary
 * @UpdateTime: 2022/12/10
 * @Describe: RegisterListener.
 */
public class RegisterListener implements ApplicationListener<RegisterApplicationEvent> {

    public final static Logger log = LoggerFactory.getLogger(RegisterListener.class);

    @Override
    public void onApplicationEvent(RegisterApplicationEvent registerApplicationEvent) {
        Object source = registerApplicationEvent.getSource();
        log.info("Received grayscale registration message:" + source.toString());
        HeartBeatHandler.execute(registerApplicationEvent.getNodes());
    }
}

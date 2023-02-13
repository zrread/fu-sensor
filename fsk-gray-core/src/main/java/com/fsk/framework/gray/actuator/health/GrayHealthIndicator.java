package com.fsk.framework.gray.actuator.health;

import com.fsk.framework.gray.handler.HeartBeatHandler;
import org.springframework.boot.actuate.health.AbstractHealthIndicator;
import org.springframework.boot.actuate.health.Health;

/**
 * @ClassName: Fsk App
 * @Copyright: com.fsk
 * @Version: 1.0.0
 * @Author: Gary
 * @UpdateTime: 2022/12/10
 * @Describe: GrayHealthIndicator.
 */
public class GrayHealthIndicator extends AbstractHealthIndicator {

    public GrayHealthIndicator() {
    }

    @Override
    protected void doHealthCheck(Health.Builder builder) throws Exception {
        boolean isOnline = HeartBeatHandler.getIsOnline();
        boolean flag = HeartBeatHandler.getFlag();
        builder.status("Status:[isOnline:" + isOnline + ", flag:" + flag + "]");
        if (isOnline && flag) {
            builder.up();
        } else if (!isOnline) {
            builder.down();
        } else if (!flag) {
            builder.unknown();
        }
    }
}

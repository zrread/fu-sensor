package com.fsk.framework.gray.metrics.loadbalancer.statistics;

import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.cloud.loadbalancer.stats.MicrometerStatsLoadBalancerLifecycle;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @ClassName: Fsk App
 * @Copyright: com.fsk
 * @Version: 1.0.0
 * @Author: Gary
 * @UpdateTime: 2022/9/2
 * @Describe: CloudLoadBalancerStatistics.
 */
@Configuration
public class CloudLoadBalancerStatistics {
    public CloudLoadBalancerStatistics() {
    }

    @Bean
    public MicrometerStatsLoadBalancerLifecycle init(MeterRegistry meterRegistry) {
        MicrometerStatsLoadBalancerLifecycle micrometerStatsLoadBalancerLifecycle = new MicrometerStatsLoadBalancerLifecycle(meterRegistry);
        return micrometerStatsLoadBalancerLifecycle;
    }
}

package com.fsk.framework.gray.client.monitor;

import com.alibaba.fastjson.JSONObject;
import com.fsk.framework.gray.client.statistics.bean.GrayRequestBean;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.DistributionSummary;
import io.micrometer.core.instrument.distribution.Histogram;
import io.micrometer.prometheus.PrometheusMeterRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @ClassName: Fsk App
 * @Copyright: com.fsk
 * @Version: 1.0.0
 * @Author: Gary
 * @UpdateTime: 2022/11/20
 * @Describe: GrayPrometheusMonitor.
 */
//@Component
public class GrayPrometheusMonitor {

    private static final ConcurrentHashMap<String, GrayRequestBean> beforeGrayRequestHashMap = new ConcurrentHashMap<String, GrayRequestBean>();

    private static final ConcurrentHashMap<String, GrayRequestBean> afterGrayRequestHashMap = new ConcurrentHashMap<String, GrayRequestBean>();
    /**
     * 累加请求计数
     */
    private Counter counter;
    /**
     * 记录最大值，总数，平均值和累加计数。
     */
    private DistributionSummary summary;
    /**
     * 直方图、柱状图
     * 不同分端的数字
     * 暂不处理
     */
    private Histogram histogram;

    @Autowired
    PrometheusMeterRegistry registry;

    @PostConstruct
    private void initGrayPrometheusMonitor() {
        counter = registry.counter("test_counter", "order", "test-svc");
        summary = registry.summary("test_summary", "amount", "test-svc");
        registry.counter("gray.loadbalancer.requests", "beforeGrayRequestHashMap", JSONObject.toJSONString(beforeGrayRequestHashMap));
    }

    /**
     * 清除指标
     */
    public void clear() {
        registry.remove(counter.getId());
        registry.remove(summary.getId());
    }

    public Counter getCounter() {
        return counter;
    }

    public DistributionSummary getSummary() {
        return summary;
    }

    public static void addUpdateBefore(GrayRequestBean bean) {
        if (beforeGrayRequestHashMap.containsKey(bean.getServiceName())) {
            GrayRequestBean grayRequestBean = beforeGrayRequestHashMap.get(bean.getServiceName());
            grayRequestBean.getCounter().incrementAndGet();
        } else {
            beforeGrayRequestHashMap.put(bean.getServiceName(), bean);
        }
    }

    public static int getBefore(String serviceName) {
        if (beforeGrayRequestHashMap.containsKey(serviceName)) {
            GrayRequestBean grayRequestBean = beforeGrayRequestHashMap.get(serviceName);
            return grayRequestBean.getTotalCounter();
        } else {
            return 0;
        }
    }

    public static void addUpdateAfter(GrayRequestBean bean) {
        if (afterGrayRequestHashMap.containsKey(bean.getServiceIp())) {
            //todo 更改bean.getServiceIp() 为 bean.getServiceName()
            GrayRequestBean grayRequestBean = afterGrayRequestHashMap.get(bean.getServiceIp());
            grayRequestBean.getCounter().incrementAndGet();
        } else {
            afterGrayRequestHashMap.put(bean.getServiceIp(), bean);
        }
    }
}

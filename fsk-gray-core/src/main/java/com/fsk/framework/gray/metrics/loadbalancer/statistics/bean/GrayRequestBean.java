package com.fsk.framework.gray.metrics.loadbalancer.statistics.bean;

import lombok.Data;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @ClassName: Fsk App
 * @Copyright: com.fsk
 * @Version: 1.0.0
 * @Author: Gary
 * @UpdateTime: 2022/9/2
 * @Describe: GrayRequestBean.
 */
@Data
public class GrayRequestBean {
    private String gatewayName;
    private String gatewayIp;
    private String serviceName;//请求服务名
    private String serviceIp;//请求服务的Ip
    private int servicePort;//请求服务的端口号
    private String isGray;//是否是灰度机器 1是，0否
    private int totalCounter = 0;//网关请求量总数
    private AtomicInteger counter = new AtomicInteger(1);//请求量总数
    private String requestUrl;//请求的URL
    private long requestMs;//请求时间
    private String retCode;//请求返回状态码

    public GrayRequestBean() {
    }

    public GrayRequestBean(String gatewayName, String gatewayIp, String serviceName, String serviceIp, AtomicInteger counter, int totalCounter, int servicePort, String requestUrl, long requestMs, String retCode, String isGray) {
        this.gatewayName = gatewayName;
        this.gatewayIp = gatewayIp;
        this.serviceName = serviceName;
        this.serviceIp = serviceIp;
        this.counter = counter;
        this.servicePort = servicePort;
        this.requestUrl = requestUrl;
        this.requestMs = requestMs;
        this.retCode = retCode;
        this.isGray = isGray;
        this.totalCounter = totalCounter;
    }
}

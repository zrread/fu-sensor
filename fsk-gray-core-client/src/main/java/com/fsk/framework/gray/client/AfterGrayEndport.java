package com.fsk.framework.gray.client;

import com.fsk.framework.gray.client.statistics.bean.GrayRequestBean;
import org.springframework.boot.actuate.endpoint.annotation.DeleteOperation;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.boot.actuate.endpoint.annotation.WriteOperation;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ConcurrentHashMap;
import org.springframework.core.metrics.ApplicationStartup;
/**
 * @ClassName: Fsk App
 * @Copyright: com.fsk
 * @Version: 1.0.0
 * @Author: Gary
 * @UpdateTime: 2022/9/2
 * @Describe: AfterGrayEndport.
 */
@Configuration
@Endpoint(id = "afterGray")
public class AfterGrayEndport {

    private static final ConcurrentHashMap<String, GrayRequestBean> afterGrayRequestHashMap = new ConcurrentHashMap<String, GrayRequestBean>();

    @ReadOperation
    public ConcurrentHashMap<String, GrayRequestBean> getData() {
        return afterGrayRequestHashMap;
    }

    @WriteOperation
    public void upData(String name, int counter) {
    }

    @DeleteOperation
    public void delData(String name, int counter) {
    }

    public static void addUpdate(GrayRequestBean bean) {
        if (afterGrayRequestHashMap.containsKey(bean.getServiceIp())) {
            //todo 更改bean.getServiceIp() 为 bean.getServiceName()
            GrayRequestBean grayRequestBean = afterGrayRequestHashMap.get(bean.getServiceIp());
            grayRequestBean.getCounter().incrementAndGet();
        } else {
            afterGrayRequestHashMap.put(bean.getServiceIp(), bean);
        }
    }
}

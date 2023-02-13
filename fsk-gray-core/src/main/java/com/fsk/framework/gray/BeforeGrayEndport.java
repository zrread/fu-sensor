package com.fsk.framework.gray;

import com.fsk.framework.gray.bean.GrayRequestBean;
import org.springframework.boot.actuate.endpoint.annotation.DeleteOperation;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.boot.actuate.endpoint.annotation.WriteOperation;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @ClassName: Fsk App
 * @Copyright: com.fsk
 * @Version: 1.0.0
 * @Author: Gary
 * @UpdateTime: 2022/9/2
 * @Describe: BeforeGrayEndport.
 */
@Configuration
@Endpoint(id = "beforeGray")
public class BeforeGrayEndport {

    private static final ConcurrentHashMap<String, GrayRequestBean> beforeGrayRequestHashMap = new ConcurrentHashMap<String, GrayRequestBean>();

    @ReadOperation
    public ConcurrentHashMap<String, GrayRequestBean> getData() {
        return beforeGrayRequestHashMap;
    }

    @WriteOperation
    public void upData(String serviceIp, int counter) {
    }

    @DeleteOperation
    public void delData(String serviceIp, int counter) {
    }

    public static void addUpdate(GrayRequestBean bean) {
        if (beforeGrayRequestHashMap.containsKey(bean.getServiceName())) {
            GrayRequestBean grayRequestBean = beforeGrayRequestHashMap.get(bean.getServiceName());
            grayRequestBean.getCounter().incrementAndGet();
        } else {
            beforeGrayRequestHashMap.put(bean.getServiceName(), bean);
        }
    }

    public static int get(String serviceName) {
        if (beforeGrayRequestHashMap.containsKey(serviceName)) {
            GrayRequestBean grayRequestBean = beforeGrayRequestHashMap.get(serviceName);
            return grayRequestBean.getTotalCounter();
        } else {
            return 0;
        }
    }
}

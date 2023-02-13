package com.fsk.framework.gray.threads;

import com.fsk.framework.gray.bean.AllGrayServersPublishingExecStatusContext;
import com.fsk.framework.gray.handler.GrayBizHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * @ClassName: Fsk App
 * @Copyright: com.fsk
 * @Version: 1.0.0
 * @Author: Gary
 * @UpdateTime: 2022/11/16
 * @Describe: ThreadBean.
 */
@Slf4j
public class ThreadBean implements Runnable {

    private AllGrayServersPublishingExecStatusContext execStatusContext;
    private GrayBizHandler grayBizHandler;

    public ThreadBean(GrayBizHandler grayBizHandler, AllGrayServersPublishingExecStatusContext execStatusContext) {
        this.execStatusContext = execStatusContext;
        this.grayBizHandler = grayBizHandler;
    }

    @Override
    public void run() {
        grayBizHandler.refreshCache(execStatusContext);
    }
}

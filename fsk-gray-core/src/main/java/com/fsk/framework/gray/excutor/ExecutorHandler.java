package com.fsk.framework.gray.excutor;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @ClassName: Fsk App
 * @Copyright: com.fsk
 * @Version: 1.0.0
 * @Author: Gary
 * @UpdateTime: 2022/11/16
 * @Describe: ExecutorHandler.
 */
@Slf4j
public final class ExecutorHandler {

    private static final ExecutorService fixExecutorService;
    private static final ExecutorService scheduledExecutorService;

    static {
        fixExecutorService = Executors.newFixedThreadPool(5);
        scheduledExecutorService = Executors.newScheduledThreadPool(10);
    }

    public static void submit(Runnable runnable) {
        try {
            fixExecutorService.execute(runnable);
        } catch (Exception e) {
            log.error("提交普通任务线程出现异常=", e);
            throw e;
        }
    }

    public static void scheduled(Runnable runnable) {
        try {
            scheduledExecutorService.execute(runnable);
        } catch (Exception e) {
            log.error("提交定时任务线程出现异常=", e);
            throw e;
        }
    }
}

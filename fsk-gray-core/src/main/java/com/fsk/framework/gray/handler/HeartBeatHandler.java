package com.fsk.framework.gray.handler;

import com.fsk.framework.gray.client.service.Client;
import com.fsk.framework.gray.model.ServerNode;
import com.fsk.framework.gray.threads.RegisterThread;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;

import java.util.List;

/**
 * @ClassName: Fsk App
 * @Copyright: com.fsk
 * @Version: 1.0.0
 * @Author: Gary
 * @UpdateTime: 2022/12/9
 * @Describe: HeartBeatHandler.
 */
@Slf4j
public class HeartBeatHandler implements DisposableBean {

    public static Client client;
    // offline
    // Inform offline when exiting
    private static volatile boolean isOnline = true;
    private static volatile boolean flag = true;
    private static long BEAT_INTERVAL;

    private static Thread offlineThread;

    public static void execute(List<ServerNode> tmpServerNodes) {
        Thread thread = new Thread() {
            @Override
            public void run() {
                offlineThread = RegisterThread.getInstance().register(tmpServerNodes, client, BEAT_INTERVAL);
            }
        };
        thread.setDaemon(true);
        thread.start();
    }

    public HeartBeatHandler(Client client, long interval) {
        HeartBeatHandler.client = client;
        HeartBeatHandler.BEAT_INTERVAL = interval;
    }

    public static boolean getIsOnline() {
        return isOnline;
    }

    public static boolean getFlag() {
        return flag;
    }

    // TODO change accessible
    public static void setFlag(boolean boo) {
        flag = boo;
    }

    @Override
    public void destroy() throws Exception {
        // offline
        isOnline = false;
        if (offlineThread.isAlive()) {
            log.info(">>>>>> heartBeatThread is alive, Waiting for offline operation to complete");
            offlineThread.join();
        }
        if (offlineThread.isInterrupted()) {
            System.err.println(">>>>>> heartBeatThread is interrupted, gray node may fail to go offline");
            log.error(">>>>>> heartBeatThread is interrupted, gray node may fail to go offline");
        }
        log.info(">>>>>> Grayscale node has been offline successfully");
    }
}

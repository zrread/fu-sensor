package com.fsk.framework.gray.threads;

import com.fsk.framework.gray.bean.RegistryParam;
import com.fsk.framework.gray.client.service.Client;
import com.fsk.framework.gray.constants.GrayMsgConstant;
import com.fsk.framework.gray.handler.GrayContextHolder;
import com.fsk.framework.gray.handler.HeartBeatHandler;
import com.fsk.framework.gray.model.ServerNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @ClassName: Fsk App
 * @Copyright: com.fsk
 * @Version: 1.0.0
 * @Author: Gary
 * @UpdateTime: 2022/12/9
 * @Describe: RegisterThread.
 */
public class RegisterThread extends GrayMsgConstant {
    public final static Logger log = LoggerFactory.getLogger(RegisterThread.class);
    private static final RegisterThread registerThread = new RegisterThread();
    private HeartBeatThread heartBeatThread;
    private OfflineThread offlineThread;
    private static long BEAT_INTERVAL;

    public static RegisterThread getInstance() {
        return registerThread;
    }

    public Thread register(List<ServerNode> tmpServerNodes, Client client, long interval) {
        log.info("Start to register with Fu-sensor");
        RegisterThread.BEAT_INTERVAL = interval;
        heartBeatThread = new HeartBeatThread(tmpServerNodes, client);
        heartBeatThread.setDaemon(true);
        heartBeatThread.setName("Fu-sensor,HeartBeat Thread");
        heartBeatThread.start();
        offlineThread = new OfflineThread(tmpServerNodes, client);
        offlineThread.setDaemon(true);
        offlineThread.setName("Fu-sensor,Offline Thread");
        offlineThread.start();
        return offlineThread;
    }

    private static class HeartBeatThread extends Thread {

        private final List<ServerNode> tmpServerNodes;
        private Client client;

        public HeartBeatThread(List<ServerNode> tmpServerNodes, Client client) {
            this.tmpServerNodes = tmpServerNodes;
            this.client = client;
        }

        @Override
        public void run() {
            while (HeartBeatHandler.getIsOnline()) {

                // register
                try {
                    client.register(RegistryParam.builder().url(GrayContextHolder.REGISTER).body(tmpServerNodes).build());
                    log.info(">>>>> Fu-sensor, register executor ");
                } catch (Exception e) {
                    log.error(MSG_1);
                }

                try {
                    if (HeartBeatHandler.getIsOnline()) {
                        TimeUnit.SECONDS.sleep(RegisterThread.BEAT_INTERVAL);
                    }
                } catch (InterruptedException e) {
                    if (HeartBeatHandler.getIsOnline()) {
                        log.warn("Fu-sensor, registry interrupted, error msg:{}", e.getMessage());
                    }
                }
            }


        }
    }

    public static class OfflineThread extends Thread {

        private final List<ServerNode> tmpServerNodes;
        private Client client;

        public OfflineThread(List<ServerNode> tmpServerNodes, Client client) {
            this.tmpServerNodes = tmpServerNodes;
            this.client = client;
        }

        @Override
        public void run() {
            // offline
            while (HeartBeatHandler.getFlag()) {
                while (!HeartBeatHandler.getIsOnline()) {
                    try {
                        client.offline(RegistryParam.builder().url(GrayContextHolder.OFFLINE).body(tmpServerNodes).build());
                        HeartBeatHandler.setFlag(false);
                        break;
                    } catch (Exception e) {
                        log.error(MSG_2);
                    }
                }
            }
        }
    }

}

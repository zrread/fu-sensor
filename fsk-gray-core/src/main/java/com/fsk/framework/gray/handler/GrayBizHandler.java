package com.fsk.framework.gray.handler;

import com.fsk.framework.gray.bean.AllGrayServersPublishingExecStatusContext;
import com.fsk.framework.gray.enums.GraySwitchEnum;
import com.fsk.framework.gray.model.GrayNode;
import com.fsk.framework.gray.model.GrayUser;
import com.fsk.framework.gray.model.ServerNode;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
public class GrayBizHandler {

    private volatile static AtomicReference<AllGrayServersPublishingExecStatusContext> publishStatus = new AtomicReference<>();
    private GrayContextHolder grayContextHolder;

    public GrayBizHandler(GrayContextHolder grayContextHolder) {
        this.grayContextHolder = grayContextHolder;
    }

    public void refreshCache(AllGrayServersPublishingExecStatusContext properties) {
        this.process(properties);
    }

    public void process(AllGrayServersPublishingExecStatusContext properties) {
        grayContextHolder.getGrayStatusContext().setAllGrayServersPublishingExecStatusContext(properties);
    }

    public String getSwitch(String serviceName, GraySwitchEnum switchEnum) {
        String index = switchEnum.getIndex();
        String switchFlag = "1";
        GrayContextHolder.GrayStatusContext grayStatusContext = grayContextHolder.getGrayStatusContext();
        AtomicReference<AllGrayServersPublishingExecStatusContext> allGrayServersPublishingExecStatusContext =
                grayStatusContext.getAllGrayServersPublishingExecStatusContext();
        AllGrayServersPublishingExecStatusContext context = allGrayServersPublishingExecStatusContext.get();
        List<ServerNode> serverNodes = context.getServerNodes();
        for (ServerNode serverNode : serverNodes) {
            if (serverNode.getService().equals(serviceName)) {
                switch (index) {
                    case "1":
                        // 根据 serviceName 获取当前服务的 灰度流量 开关
                        switchFlag = serverNode.getTrafficSwitch();
                        break;
                    case "2":
                        // 根据 serviceName 获取当前服务的 常规灰度节点开关 开关
                        switchFlag = serverNode.getNormalSwitch();
                        break;
                    case "3":
                        // 根据 serviceName 获取当前服务的 A/B 开关
                        switchFlag = serverNode.getAbSwitch();
                        break;
                    case "4":
                        // 根据 serviceName 获取当前服务的 Mods 范围
                        switchFlag = serverNode.getMods();
                        break;
                    case "5":
                        // 根据 serviceName 获取当前服务的灰度发布进度
                        switchFlag = serverNode.getActiveStep();
                        break;
                }
                break;
            }
        }

        return switchFlag;
    }

    /**
     * 根据 网关名+服务名 获取灰度节点hostPort
     *
     * @param gatewayAndServiceName
     * @return
     */
    public String getGrayHostPortByGatewayAndServiceName(String gatewayAndServiceName) {
        String hostPort = "";
        GrayContextHolder.GrayStatusContext grayStatusContext = grayContextHolder.getGrayStatusContext();
        AtomicReference<AllGrayServersPublishingExecStatusContext> allGrayServersPublishingExecStatusContext =
                grayStatusContext.getAllGrayServersPublishingExecStatusContext();
        AllGrayServersPublishingExecStatusContext context = allGrayServersPublishingExecStatusContext.get();
        List<GrayNode> grayNodes = context.getGrayNodes();
        for (GrayNode grayNode : grayNodes) {
            if (grayNode.getService().equals(gatewayAndServiceName)) {
                hostPort = grayNode.getIp();
                break;
            }
        }
        return hostPort;
    }

    /**
     * 判断当前请求用户IP是否能进灰度机器
     *
     * @param ip
     * @return
     */
    public boolean accessUserIps(String ip) {
        boolean access = false;
        GrayContextHolder.GrayStatusContext grayStatusContext = grayContextHolder.getGrayStatusContext();
        AtomicReference<AllGrayServersPublishingExecStatusContext> allGrayServersPublishingExecStatusContext =
                grayStatusContext.getAllGrayServersPublishingExecStatusContext();
        AllGrayServersPublishingExecStatusContext context = allGrayServersPublishingExecStatusContext.get();
        List<GrayUser> grayUsers = context.getGrayUsers();
        for (GrayUser grayUser : grayUsers) {
            if (grayUser.getIp().equals(ip)) {
                access = true;
                break;
            }
        }
        return access;
    }

    public String getGrayHeader() {
        return grayContextHolder.getGrayHeader();
    }

    public boolean containsMods(String serviceName, Integer mod) {
        ConcurrentHashMap<String, List<Integer>> gray_mods = grayContextHolder.getGRAY_MODS();
        return gray_mods.get(serviceName).contains(mod);
    }

    public void setMods(String serviceName, List<Integer> mods) {
        grayContextHolder.setGRAY_MODS(serviceName, mods);
    }

    public Integer size() {
        return grayContextHolder.getSize();
    }

    public AllGrayServersPublishingExecStatusContext getGrayContext() {
        return grayContextHolder
                .getGrayStatusContext()
                .getAllGrayServersPublishingExecStatusContext()
                .get();
    }

    public void status() {
        statusAssemble();
    }

    private void statusAssemble() {

        List<GrayUser> grayUsers = new ArrayList<>();

        List<GrayNode> grayNodes = new ArrayList<>();

        List<ServerNode> serverNodes = new ArrayList<>();

        AllGrayServersPublishingExecStatusContext context =
                new AllGrayServersPublishingExecStatusContext(grayNodes, grayUsers, serverNodes);
        grayContextHolder.getGrayStatusContext().setAllGrayServersPublishingExecStatusContext(context);
    }

}

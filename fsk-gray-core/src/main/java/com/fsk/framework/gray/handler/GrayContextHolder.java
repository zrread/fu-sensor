package com.fsk.framework.gray.handler;

import com.fsk.framework.gray.bean.AllGrayServersPublishingExecStatusContext;
import com.fsk.framework.pubconfig.entity.GrayAddressEntity;
import com.fsk.framework.pubconfig.entity.KeyEntities;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.ansi.AnsiColor;
import org.springframework.boot.ansi.AnsiOutput;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import javax.annotation.PostConstruct;
import java.io.PrintStream;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @ClassName: Fsk App
 * @Copyright: com.fsk
 * @Version: 1.0.0
 * @Author: Gary
 * @UpdateTime: 2022/11/15
 * @Describe: GrayContextHolder.
 */
@Slf4j
public class GrayContextHolder implements ApplicationContextAware, InitializingBean, DisposableBean {

    /**
     * 灰度上下文
     */
    private final GrayStatusContext grayStatusContext;

    /**
     * 客户端请求 ip
     * Deprecated reason : instead of using headers
     */
    @Deprecated
    private final ThreadLocal<String> currentRequestInfo = new ThreadLocal<>();

    /**
     * 灰度测试请求头 key
     */
    private final static String GRAY_HEADER = "A-gray-header";

    /**
     * 总流量大小
     */
    private final static int SIZE = 100;

    /**
     * 灰度流量池
     */
    private final ConcurrentHashMap<String, List<Integer>> GRAY_MODS = new ConcurrentHashMap<String, List<Integer>>();

    /**
     * 公共不变配置
     */
    private KeyEntities keyEntities;
    private ApplicationContext context;
    private static final String[] GRAY_MARKER = new String[]{" ___          __   ___       __   __   __  ", "|__  |  | __ /__` |__  |\\ | /__` /  \\ |__) ", "|    \\__/    .__/ |___ | \\| .__/ \\__/ |  \\ ", "                                           "};
    public static String OFFLINE = "http://localhost:8080";
    public static String SERVER = "http://localhost:8080";
    public static String REGISTER = "http://localhost:8080";
    /**
     * 当前网关的名称 spring.application
     */
    public final static AtomicReference<String> CURRENT_GATEWAY_NAME = new AtomicReference<>("0");

    /**
     * 当前网关 在 nacos 上注册成功后的 IP
     */
    public final static AtomicReference<String> CURRENT_GATEWAY_HOST = new AtomicReference<>("0");

    /**
     * 当前网关 在 nacos 上注册成功后的 PORT
     */
    public final static AtomicReference<String> CURRENT_GATEWAY_PORT = new AtomicReference<>("0");

    public GrayContextHolder(GrayStatusContext grayStatusContext, ApplicationContext ctx, KeyEntities keyEntities) {
        this.grayStatusContext = grayStatusContext;
        this.context = ctx;
        this.keyEntities = keyEntities;
        GrayAddressEntity grayAddress = keyEntities.getGrayAddress();
        OFFLINE = grayAddress.getOffline();
        SERVER = grayAddress.getServer();
        REGISTER = grayAddress.getRegister();
    }

    public GrayStatusContext getGrayStatusContext() {
        return grayStatusContext;
    }

    protected void setCurrentRequestInfo(String requestInfo) {
        currentRequestInfo.set(requestInfo);
    }

    protected ThreadLocal<String> getCurrentRequestInfo() {
        return currentRequestInfo;
    }

    protected void setGRAY_MODS(String serviceName, List<Integer> mods) {
        GRAY_MODS.put(serviceName, mods);
    }

    protected ConcurrentHashMap<String, List<Integer>> getGRAY_MODS() {
        return GRAY_MODS;
    }

    protected int getSize() {
        return SIZE;
    }

    protected String getGrayHeader() {
        return GRAY_HEADER;
    }

    public static class GrayStatusContext {

        private final AtomicReference<AllGrayServersPublishingExecStatusContext> allGrayServersPublishingExecStatusContext = new AtomicReference<>();

        public GrayStatusContext() {
        }

        protected void setAllGrayServersPublishingExecStatusContext(AllGrayServersPublishingExecStatusContext context) {
            allGrayServersPublishingExecStatusContext.set(context);
        }

        protected AtomicReference<AllGrayServersPublishingExecStatusContext> getAllGrayServersPublishingExecStatusContext() {
            return allGrayServersPublishingExecStatusContext;
        }

    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.context = applicationContext;
    }

    @Override
    public void afterPropertiesSet() {
        GrayAddressEntity grayAddress = keyEntities.getGrayAddress();
        OFFLINE = grayAddress.getOffline();
        SERVER = grayAddress.getServer();
        REGISTER = grayAddress.getRegister();
    }

    @PostConstruct
    public void grayStart() {
        PrintStream out = null;
        try {
            out = new PrintStream(System.out);
            out.println("==================================================");
            out.println();
            for (String mark : GRAY_MARKER) {
                out.println(AnsiOutput.toString(AnsiColor.CYAN, mark, AnsiColor.DEFAULT));
            }
            out.println();
            out.println("==================================================");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
            }
        } catch (Exception e) {
            if (out != null) {
                out.close();
            }
        }
    }

    @Override
    public void destroy() {

    }

    public void setCurrentGatewayName(String currentGatewayName) {
        CURRENT_GATEWAY_NAME.set(currentGatewayName);
    }

    public void setCurrentGatewayHost(String currentGatewayHost) {
        CURRENT_GATEWAY_HOST.set(currentGatewayHost);
    }

    public void setCurrentGatewayPort(String currentGatewayPort) {
        CURRENT_GATEWAY_PORT.set(currentGatewayPort);
    }


}

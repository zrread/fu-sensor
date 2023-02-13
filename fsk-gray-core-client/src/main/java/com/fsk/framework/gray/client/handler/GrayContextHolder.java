package com.fsk.framework.gray.client.handler;

import com.fsk.framework.gray.client.bean.AllGrayServersPublishingExecStatusContext;
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
import javax.annotation.PreDestroy;
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
    private static final String[] GRAY_MARKER = new String[]{" __    ___    __    _    ", "/ /`_ | |_)  / /\\  \\ \\_/ ", "\\_\\_/ |_| \\ /_/--\\  |_|  "};
    private static String OFFLINE = "http://localhost:8080";
    private static String SERVER = "http://localhost:8080";
    private static String REGISTER = "http://localhost:8080";

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
    public void afterPropertiesSet() throws Exception {

    }

    @PostConstruct
    public void grayStartBanner() {
        PrintStream out = null;
        try {
            out = new PrintStream(System.out);
            out.println("=========================");
            out.println();
            for (String mark : GRAY_MARKER) {
                out.println(AnsiOutput.toString(AnsiColor.CYAN, mark, AnsiColor.DEFAULT));
            }
            out.println();
            out.println("=========================");
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

    @PreDestroy
    public void grayOfflineBanner() {
        PrintStream out = new PrintStream(System.out);
        out.println("=========================================================");
        out.println();
        out.println(AnsiOutput.toString(AnsiColor.CYAN, "Gray server node offline success", AnsiColor.DEFAULT));
        out.println();
        out.println("=========================================================");
    }

    @Override
    public void destroy() {

    }

}

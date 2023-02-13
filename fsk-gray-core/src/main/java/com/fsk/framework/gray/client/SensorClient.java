package com.fsk.framework.gray.client;

import com.fsk.framework.gray.bean.GrayInDto;
import com.fsk.framework.gray.bean.RegistryParam;
import com.fsk.framework.gray.bean.ReturnBody;
import com.fsk.framework.gray.client.service.Client;
import com.fsk.framework.gray.exception.GrayException;
import com.fsk.framework.gray.handler.GrayContextHolder;
import com.fsk.framework.gray.utils.RemoteRequestHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ansi.AnsiColor;
import org.springframework.boot.ansi.AnsiOutput;

import java.io.PrintStream;

/**
 * @ClassName: Fsk App
 * @Copyright: com.fsk
 * @Version: 1.0.0
 * @Author: Gary
 * @UpdateTime: 2022/12/9
 * @Describe: SensorClient.
 */
@Slf4j
public class SensorClient implements Client {

    @Override
    public void register(RegistryParam param) throws Exception {
        ReturnBody returnBody = RemoteRequestHelper.postBody(param.getUrl(),
                param.getAccessToken(),
                6,
                param.getBody(),
                String.class);
        if (returnBody == null || !returnBody.getStatusCode().equals("200")) {
            throw new GrayException("999", "register exception");
        }
    }

    @Override
    public void offline(RegistryParam param) throws Exception {

        String url = param.getUrl();

        // offline gateway node from remote
        String gatewayPort = GrayContextHolder.CURRENT_GATEWAY_PORT.get();
        String gatewayHost = GrayContextHolder.CURRENT_GATEWAY_HOST.get();
        String gatewayName = GrayContextHolder.CURRENT_GATEWAY_NAME.get();
        log.info("offline:{},{},{}", gatewayName, gatewayHost, gatewayPort);
        boolean isSuccess = true;
        PrintStream out = new PrintStream(System.out);
        out.println("==================================================================================================================");
        out.println();

        try {
            GrayInDto reqBody = GrayInDto.builder()
                    .gateway(gatewayName)
                    .url(gatewayHost + ":" + gatewayPort) // default 8080 for gray server
                    .build();
            ReturnBody returnBody = RemoteRequestHelper.postBody(url,
                    null,
                    6,
                    reqBody,
                    String.class);
            if (returnBody == null || !returnBody.getStatusCode().equals("200")) {
                throw new GrayException("999", "offline exception");
            }
        } catch (Exception e) {
            isSuccess = false;
            out.println("=============================");
            out.println(AnsiOutput.toString(AnsiColor.RED, "GRAY SERVER OFFLINE FAILED", AnsiColor.DEFAULT));
            out.println("=============================");
            out.println(AnsiOutput.toString(AnsiColor.WHITE, "* Gray Node *", AnsiColor.DEFAULT));
            out.println(AnsiOutput.toString(AnsiColor.WHITE, gatewayName + "@" + gatewayHost + ":" + gatewayPort, AnsiColor.DEFAULT));
            out.println(AnsiOutput.toString(AnsiColor.WHITE, "* Description *", AnsiColor.DEFAULT));
            out.println(AnsiOutput.toString(AnsiColor.WHITE, e.getMessage(), AnsiColor.DEFAULT));
        }
        if (isSuccess) {
            out.println("=============================");
            out.println(AnsiOutput.toString(AnsiColor.CYAN, "GRAY SERVER OFFLINE SUCCESS", AnsiColor.DEFAULT));
            out.println("=============================");
        }
        out.println();
        out.println("==================================================================================================================");
    }
}

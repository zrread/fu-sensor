package com.fsk.framework.gray.client.bean;

import com.fsk.framework.gray.client.model.GrayNode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class GrayInDto {
    /**
     * 网关名称
     */
    private String gateway;
    /**
     * 网关 ip:port
     */
    private String url;
    /**
     * service name
     */
    private String service;
    /**
     * AB test 开关  1: 打开，0：关闭
     */
    private String abSwitch;
    /**
     * 流量开关 1: 打开，0：关闭
     */
    private String trafficSwitch;

    /**
     * 正常节点开关 1: 打开，0：关闭
     */
    private String normalSwitch;

    /**
     * 流程步骤 1，2，3，4，5
     */
    private String activeStep;

    /**
     * 流量逐步开放取模值 :步长为20，总共100
     */
    private Integer mods;

    /**
     * ip:port
     */
    private String ip;

    /**
     * ip list
     */
    private List<String> ipList;

    /**
     * 是否有效 1: 有效，0：无效
     */
    private String effective;

    /**
     * 用户名称
     */
    private String userName;

    /**
     * 灰度节点 list
     */
    List<GrayNode> grayNodeList;

}

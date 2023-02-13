package com.fsk.framework.gray.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class ServerNode {
    private String id;
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
     * AB test 开关
     */
    private String abSwitch;
    /**
     * 流量开关
     */
    private String trafficSwitch;

    /**
     * 正式节点开关
     */
    private String normalSwitch;

    /**
     * 流程步骤 0，1，2，3，4，5
     */
    private String activeStep;

    /**
     * 流量比例
     */
    private String mods;

    /**
     * 是否删除(0:未删除；1:已删除)
     */
    private String isDelete;

    /**
     * 创建人code
     */
    private String createCode;

    /**
     * 创建人名称
     */
    private String createName;

    /**
     * 创建时间
     */
    private LocalDateTime createDate;

    /**
     * 修改人code
     */
    private String updateCode;

    /**
     * 修改人名称
     */
    private String updateName;

    /**
     * 修改时间
     */
    private LocalDateTime updateDate;

}

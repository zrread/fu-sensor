package com.fsk.framework.gray.client.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class GrayNode {

    private String id;

    /**
     * 网关名称
     */
    private String gateway;
    /**
     * 节点 ip:port
     */
    private String ip;
    /**
     * service name
     */
    private String service;

    /**
     * 是否有效 1: 有效，0：无效
     */
    private String effective;


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

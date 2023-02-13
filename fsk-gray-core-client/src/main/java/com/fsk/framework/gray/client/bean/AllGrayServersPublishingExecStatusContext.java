package com.fsk.framework.gray.client.bean;

import com.fsk.framework.gray.client.model.GrayNode;
import com.fsk.framework.gray.client.model.GrayUser;
import com.fsk.framework.gray.client.model.ServerNode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * @ClassName: Fsk App
 * @Copyright: com.fsk
 * @Version: 1.0.0
 * @Author: Gary
 * @UpdateTime: 2022/11/15
 * @Describe: 灰度发布状态上下文.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AllGrayServersPublishingExecStatusContext {
    private List<GrayNode> grayNodes;
    private List<GrayUser> grayUsers;
    private List<ServerNode> serverNodes;
}

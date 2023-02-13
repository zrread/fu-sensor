package com.fsk.framework.gray.event;

import com.fsk.framework.gray.model.ServerNode;
import org.springframework.context.ApplicationEvent;

import java.util.List;

/**
 * @ClassName: Fsk App
 * @Copyright: com.fsk
 * @Version: 1.0.0
 * @Author: Gary
 * @UpdateTime: 2022/12/10
 * @Describe: RegisterApplicationEvent.
 */
public class RegisterApplicationEvent extends ApplicationEvent {

    private List<ServerNode> nodes;

    public RegisterApplicationEvent(Object source,List<ServerNode> serverNodes) {
        super(source);
        this.nodes = serverNodes;
    }

    public List<ServerNode> getNodes() {
        return nodes;
    }
}

package com.fsk.framework.gray.bean;

import com.fsk.framework.gray.model.ServerNode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @ClassName: Fsk App
 * @Copyright: com.fsk
 * @Version: 1.0.0
 * @Author: Gary
 * @UpdateTime: 2022/12/9
 * @Describe: RegistryParam.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegistryParam {
    private String url;
    private String accessToken;
    private List<ServerNode> body;
}

package com.fsk.framework.gray.bean;

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
 * @UpdateTime: 2022/9/4
 * @Describe: GatewayRoutes.
 */
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class GatewayRoutes {
    private String id;
    private String uri;
    private List<String> predicates;
    private List<String> filters;
}

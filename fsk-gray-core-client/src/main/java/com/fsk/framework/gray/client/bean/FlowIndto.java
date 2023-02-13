package com.fsk.framework.gray.client.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ClassName: Fsk App
 * @Copyright: com.fsk
 * @Version: 1.0.0
 * @Author: Gary
 * @UpdateTime: 2022/9/14
 * @Describe: NextIndto.
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
public class FlowIndto {
    private String activeStep;
    private String rollbackStep;
    private GrayInDto grayInfo;
}

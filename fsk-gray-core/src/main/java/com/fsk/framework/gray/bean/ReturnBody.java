package com.fsk.framework.gray.bean;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @ClassName: Fsk App
 * @Copyright: com.fsk
 * @Version: 1.0.0
 * @Author: Gary
 * @UpdateTime: 2022/9/27
 * @Describe: ReturnT.
 */
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ReturnBody<T> implements Serializable {
    public static final long serialVersionUID = 42L;
    private String statusCode;
    private String statusMsg;
    private T responseData;
}

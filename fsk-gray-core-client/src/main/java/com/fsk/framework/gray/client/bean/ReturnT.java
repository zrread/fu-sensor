package com.fsk.framework.gray.client.bean;

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
public class ReturnT<T> implements Serializable {
    public static final long serialVersionUID = 42L;
    private int code;
    private String msg;
    private T content;
}

package com.fsk.framework.gray.client.exception;

/**
 * @ClassName: Fsk App
 * @Copyright: com.fsk
 * @Version: 1.0.0
 * @Author: Gary
 * @UpdateTime: 2022/8/30
 * @Describe: GrayException.
 */
public class GrayClientException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    protected String errorCode;

    protected String errorMsg;

    public GrayClientException() {
        super();
    }

    public GrayClientException(String errorCode, String errorMsg) {
        super(errorCode);
        this.errorCode = errorCode;
        this.errorMsg = errorMsg;
    }
}

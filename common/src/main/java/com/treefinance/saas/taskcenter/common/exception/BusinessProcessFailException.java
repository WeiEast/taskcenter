package com.treefinance.saas.taskcenter.common.exception;

/**
 * 运行时异常
 * Created by haojiahong on 2017/2/12.
 */

public class BusinessProcessFailException extends RuntimeException {
    private String errorCode;

    public BusinessProcessFailException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public BusinessProcessFailException(String errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }
}

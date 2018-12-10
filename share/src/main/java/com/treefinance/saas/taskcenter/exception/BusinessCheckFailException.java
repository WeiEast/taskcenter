package com.treefinance.saas.taskcenter.exception;

/**
 * 业务校验异常
 * Created by haojiahong on 2017/2/12.
 */
public class BusinessCheckFailException extends RuntimeException {
    private String errorCode;

    public BusinessCheckFailException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public BusinessCheckFailException(String errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }
}

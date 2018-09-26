package com.treefinance.saas.taskcenter.common.exception;

/**
 * Created by luoyihua on 2017/5/10.
 */
public class CallbackEncryptException extends CryptoException{

    public CallbackEncryptException(String message) {
        super(message);
    }

    public CallbackEncryptException(String message, Throwable cause) {
        super(message, cause);
    }
}

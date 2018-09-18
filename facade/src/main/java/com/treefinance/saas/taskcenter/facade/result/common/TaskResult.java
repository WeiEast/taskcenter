package com.treefinance.saas.taskcenter.facade.result.common;

import java.io.Serializable;

/**
 * Created by haojiahong on 2017/2/12.
 */
public class TaskResult<D> implements Serializable {

    private static final long serialVersionUID = -5642910436763381104L;

    private static final String SUCCESS_CODE = "00000000";
    private D data;
    private boolean success = true;
    private String code;
    private String message;

    public static <D> TaskResult<D> wrapSuccessfulResult(D data) {
        TaskResult<D> result = new TaskResult<>();
        result.data = data;
        result.success = true;
        result.code = SUCCESS_CODE;
        return result;
    }

    public static <T> TaskResult<T> wrapSuccessfulResult(String message, T data) {
        TaskResult<T> result = new TaskResult<>();
        result.data = data;
        result.success = true;
        result.code = SUCCESS_CODE;
        result.message = message;
        return result;
    }

    public static <D> TaskResult<D> wrapErrorResult(String code, String message) {
        TaskResult<D> result = new TaskResult<>();
        result.success = false;
        result.code = code;
        result.message = message;
        return result;
    }

    public D getData() {
        return data;
    }

    public TaskResult<D> setData(D data) {
        this.data = data;
        return this;
    }

    public boolean isSuccess() {
        return success;
    }

    public TaskResult<D> setSuccess(boolean success) {
        this.success = success;
        return this;

    }

    public String getCode() {
        return code;
    }

    public TaskResult<D> setCode(String code) {
        this.code = code;
        return this;
    }

    public String getMessage() {
        return message;
    }

    public TaskResult<D> setMessage(String message) {
        this.message = message;
        return this;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append("{");

        sb.append("success=");
        sb.append(success);
        sb.append(",");

        sb.append("code=");
        sb.append(code);
        sb.append(",");

        sb.append("message=");
        sb.append(message);
        sb.append(",");

        sb.append("data=");
        sb.append(data);

        sb.append("}");

        return sb.toString();
    }
}

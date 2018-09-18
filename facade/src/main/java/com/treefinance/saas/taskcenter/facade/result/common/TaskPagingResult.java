package com.treefinance.saas.taskcenter.facade.result.common;

import java.io.Serializable;
import java.util.List;

/**
 * Created by haojiahong on 2017/2/12.
 */
public class TaskPagingResult<T> implements Serializable {
    private static final long serialVersionUID = 8964313097005577485L;
    private List<T> list;
    private int total;

    private boolean success;
    private String code;
    private String message;

    public static <T> TaskPagingResult<T> wrapSuccessfulResult(List<T> data, int total) {
        TaskPagingResult<T> result = new TaskPagingResult<>();
        result.list = data;
        result.total = total;
        result.success = true;
        return result;
    }

    public static <T> TaskPagingResult<T> wrapErrorResult(String code, String message) {
        TaskPagingResult<T> result = new TaskPagingResult<>();
        result.success = false;
        result.code = code;
        result.message = message;
        return result;
    }

    public List<T> getList() {
        return list;
    }

    public TaskPagingResult<T> setList(List<T> list) {
        this.list = list;
        return this;
    }

    public int getTotal() {
        return total;
    }

    public TaskPagingResult<T> setTotal(int total) {
        this.total = total;
        return this;
    }

    public boolean isSuccess() {
        return success;
    }

    public TaskPagingResult<T> setSuccess(boolean success) {
        this.success = success;
        return this;
    }

    public String getCode() {
        return code;
    }

    public TaskPagingResult<T> setCode(String code) {
        this.code = code;
        return this;
    }

    public String getMessage() {
        return message;
    }

    public TaskPagingResult<T> setMessage(String message) {
        this.message = message;
        return this;
    }
}

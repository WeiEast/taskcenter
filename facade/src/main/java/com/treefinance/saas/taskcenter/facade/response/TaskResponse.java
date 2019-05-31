/*
 * Copyright © 2015 - 2017 杭州大树网络技术有限公司. All Rights Reserved
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package com.treefinance.saas.taskcenter.facade.response;

import java.io.Serializable;

/**
 * @author Jerry
 * @date 2018/12/13 19:44
 */
public class TaskResponse<T> implements Serializable {

    /**
     * 是否调用成功
     */
    private boolean success;
    /**
     * 错误编码
     */
    private String code;
    /**
     * 错误信息
     */
    private String message;
    /**
     * 数据实体
     */
    private T entity;
    /**
     * 时间戳
     */
    private long timestamp;

    public TaskResponse() {
    }

    private TaskResponse(boolean success, String code, String message, T entity) {
        this.success = success;
        this.code = code;
        this.message = message;
        this.entity = entity;
        this.timestamp = System.currentTimeMillis();
    }

    public static <T> TaskResponse<T> success(T entity) {
        return new TaskResponse<>(true, null, null, entity);
    }

    public static <T> TaskResponse<T> failure(String code, String message) {
        return new TaskResponse<>(false, code, message, null);
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getEntity() {
        return entity;
    }

    public void setEntity(T entity) {
        this.entity = entity;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}

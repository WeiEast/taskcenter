package com.treefinance.saas.taskcenter.biz.schedule.handler;

import com.treefinance.saas.taskcenter.dao.entity.Task;

import java.util.Date;

/**
 * 超时任务处理
 * @author yh-treefinance
 * @date 2017/12/25.
 */
public interface TaskTimeoutHandler {

    /**
     * 超时任务处理
     *
     * @param task 任务对象
     * @param timeout 超时时长，单位：秒
     * @param loginTime 登录时间
     */
    void handle(Task task, Integer timeout, Date loginTime);
}

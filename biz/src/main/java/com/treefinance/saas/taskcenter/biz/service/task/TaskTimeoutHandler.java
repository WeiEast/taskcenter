package com.treefinance.saas.taskcenter.biz.service.task;


import com.treefinance.saas.taskcenter.dto.TaskDTO;

import java.util.Date;

/**
 * 超时任务处理
 * Created by yh-treefinance on 2017/12/25.
 */
public interface TaskTimeoutHandler {

    /**
     * 超时任务处理
     *
     * @param task
     * @param timeout
     * @param loginTime
     * @return
     */
    void handleTaskTimeout(TaskDTO task, Integer timeout, Date loginTime);
}

package com.treefinance.saas.taskcenter.facade.service;

import com.treefinance.saas.taskcenter.facade.result.common.TaskResult;

import java.util.Date;

/**
 * @author haojiahong
 * @date 2018/9/28
 */
public interface MoxieTimeoutFacade {

    TaskResult<Date> getLoginTime(Long taskId);

    TaskResult<Void> logLoginTime(Long taskId);

    TaskResult<Void> logLoginTime(Long taskId, Date date);

    TaskResult<Void> handleTaskTimeout(Long taskId);

    TaskResult<Void> handleLoginTimeout(Long taskId, String moxieTaskId);

}

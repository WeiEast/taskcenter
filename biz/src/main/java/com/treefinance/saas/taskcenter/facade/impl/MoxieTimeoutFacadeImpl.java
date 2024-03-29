package com.treefinance.saas.taskcenter.facade.impl;

import com.treefinance.saas.taskcenter.biz.service.MoxieTaskService;
import com.treefinance.saas.taskcenter.facade.result.common.TaskResult;
import com.treefinance.saas.taskcenter.facade.service.MoxieTimeoutFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * @author haojiahong
 * @date 2018/9/28
 */
@Component("moxieTimeoutFacade")
public class MoxieTimeoutFacadeImpl implements MoxieTimeoutFacade {

    @Autowired
    private MoxieTaskService moxieTimeoutService;

    @Override
    public TaskResult<Date> getLoginTime(Long taskId) {
        Date date = moxieTimeoutService.queryLoginTime(taskId);
        return TaskResult.wrapSuccessfulResult(date);
    }

    @Override
    public TaskResult<Void> logLoginTime(Long taskId) {
        moxieTimeoutService.logLoginTime(taskId);
        return TaskResult.wrapSuccessfulResult(null);
    }

    @Override
    public TaskResult<Void> logLoginTime(Long taskId, Date date) {
        moxieTimeoutService.logLoginTime(taskId, date);
        return TaskResult.wrapSuccessfulResult(null);
    }

    @Override
    public TaskResult<Void> handleTaskTimeout(Long taskId) {
        moxieTimeoutService.handleIfTaskTimeout(taskId);
        return TaskResult.wrapSuccessfulResult(null);
    }

    @Override
    public TaskResult<Void> handleLoginTimeout(Long taskId, String moxieTaskId) {
        moxieTimeoutService.handleAfterLoginTimeout(taskId, moxieTaskId);
        return TaskResult.wrapSuccessfulResult(null);
    }
}

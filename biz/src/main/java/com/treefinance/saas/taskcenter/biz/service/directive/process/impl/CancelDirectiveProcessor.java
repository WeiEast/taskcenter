package com.treefinance.saas.taskcenter.biz.service.directive.process.impl;

import com.treefinance.saas.taskcenter.biz.service.directive.process.AbstractCallbackDirectiveProcessor;
import com.treefinance.saas.taskcenter.biz.service.directive.process.DirectiveContext;
import com.treefinance.saas.taskcenter.common.enums.EDirective;
import com.treefinance.saas.taskcenter.common.enums.ETaskStatus;
import com.treefinance.saas.taskcenter.interation.manager.SpiderTaskManager;
import com.treefinance.saas.taskcenter.share.AsyncExecutor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * 取消任务执行
 *
 * @author yh-treefinance
 * @date 2017/7/10.
 */
@Component
public class CancelDirectiveProcessor extends AbstractCallbackDirectiveProcessor {
    @Autowired
    private AsyncExecutor asyncExecutor;
    @Autowired
    private SpiderTaskManager spiderTaskManager;

    @Override
    public EDirective getSpecifiedDirective() {
        return EDirective.TASK_CANCEL;
    }

    @Override
    protected void doProcess(DirectiveContext context) {
        // 更新任务状态
        context.updateTaskStatus(ETaskStatus.CANCEL);

        // 取消任务
        Long taskId = context.getTaskId();
        taskService.updateStatusIfDone(taskId, ETaskStatus.CANCEL.getStatus());

        Map<String, String> extMap = new HashMap<>(1);
        extMap.put("reason", "user");
        spiderTaskManager.cancelQuietly(taskId, extMap);

        // 异步触发触发回调
        asyncExecutor.runAsync(context, this::callback);
    }

}

package com.treefinance.saas.taskcenter.biz.service.directive.process.impl;

import com.treefinance.saas.taskcenter.biz.service.directive.process.AbstractCallbackDirectiveProcessor;
import com.treefinance.saas.taskcenter.biz.service.directive.process.CallbackEntity;
import com.treefinance.saas.taskcenter.biz.service.directive.process.DirectiveContext;
import com.treefinance.saas.taskcenter.common.enums.EDirective;
import com.treefinance.saas.taskcenter.common.enums.ETaskStatus;
import org.springframework.stereotype.Component;

/**
 * 成功指令处理
 * 
 * @author yh-treefinance
 * @date 2017/7/6.
 */
@Component
public class SuccessDirectiveProcessor extends AbstractCallbackDirectiveProcessor {

    @Override
    public EDirective getSpecifiedDirective() {
        return EDirective.TASK_SUCCESS;
    }

    @Override
    protected void doProcess(DirectiveContext context) {
        Long taskId = context.getTaskId();

        // 记录任务日志
        this.saveTaskLog(taskId, "爬数任务执行完成", null);

        // 生成数据map
        CallbackEntity callbackEntity = buildCallbackEntity(context);
        // 回调之前预处理
        precallback(callbackEntity, context);

        // 触发回调: 0-无需回调，1-回调成功，-1-回调失败
        int result = callback(callbackEntity, context);
        if (result == 0) {
            // 任务成功但是不需要回调(前端回调),仍需记录回调日志,获取dataUrl提供数据下载以及回调统计
            this.saveCallbackLog((byte)2, null,0, 0,callbackEntity, null, context);
            this.saveTaskLog(taskId, "回调通知成功", null);

            context.updateTaskStatus(ETaskStatus.SUCCESS);
        } else if (result == 1) {
            context.updateTaskStatus(ETaskStatus.SUCCESS);
        } else {
            /* 指令发生变更 ： task_success -> temporary_success(过渡) -> callback_fail
               有风险，尝试用临时成功转移的状态
               taskNextDirectiveService.insert(taskId, context.getDirectiveString());*/
            taskNextDirectiveService.insert(taskId, EDirective.TEMPORARY_SUCCESS);

            context.updateTaskStatus(ETaskStatus.FAIL);

            context.updateDirective(EDirective.CALLBACK_FAIL);
        }
        // 更新任务状态
        String stepCode = taskService.updateStatusIfDone(taskId, context.getTaskStatus());
        context.updateStepCode(stepCode);

    }

}

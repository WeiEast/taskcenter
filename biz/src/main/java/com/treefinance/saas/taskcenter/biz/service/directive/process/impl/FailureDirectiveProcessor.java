package com.treefinance.saas.taskcenter.biz.service.directive.process.impl;

import com.treefinance.saas.taskcenter.biz.service.directive.process.AbstractCallbackDirectiveProcessor;
import com.treefinance.saas.taskcenter.biz.service.directive.process.CallbackEntity;
import com.treefinance.saas.taskcenter.biz.service.directive.process.DirectiveContext;
import com.treefinance.saas.taskcenter.common.enums.EBizType;
import com.treefinance.saas.taskcenter.common.enums.EDirective;
import com.treefinance.saas.taskcenter.common.enums.ETaskStatus;
import com.treefinance.saas.taskcenter.context.Constants;
import com.treefinance.saas.taskcenter.share.AsyncExecutor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 任务失败回调
 * 
 * @author yh-treefinance
 * @date 2017/7/10.
 */
@Component
public class FailureDirectiveProcessor extends AbstractCallbackDirectiveProcessor {
    @Autowired
    private AsyncExecutor asyncExecutor;

    @Override
    public EDirective getSpecifiedDirective() {
        return EDirective.TASK_FAIL;
    }

    @Override
    protected void doProcess(DirectiveContext context) {
        // 补充错误信息
        appendFailureMessage(context);

        // 任务置为失败
        context.updateTaskStatus(ETaskStatus.FAIL);

        // 更新任务状态
        String errorCode = taskService.updateStatusIfDone(context.getTaskId(), ETaskStatus.FAIL.getStatus());
        context.updateStepCode(errorCode);

        // 成数据map
        CallbackEntity callbackEntity = buildCallbackEntity(context);

        // 异步触发触发回调
        asyncExecutor.runAsync(context, ctx -> callback(callbackEntity, ctx));

        context.backupCallbackEntity(callbackEntity);
    }

    private void appendFailureMessage(DirectiveContext context) {
        // 处理返回到前端的消息
        try {
            Byte bizType = context.getBizType();
            if (EBizType.OPERATOR.getCode().equals(bizType)) {
                // 如果是运营商维护导致任务失败,爬数发来的任务指令中,directiveDTO的remark字段为{"errorMsg","当前运营商正在维护中，请稍后重试"}.
                // 如果是其他原因导致的任务失败,则返回下面的默认值.
                context.putAttributeIfAbsent(Constants.ERROR_MSG_NAME, Constants.OPERATOR_TASK_FAIL_MSG);
                logger.info("handle task-fail-msg, directiveContext={}", context);
            } else if (EBizType.DIPLOMA.getCode().equals(bizType)) {
                context.putAttribute(Constants.ERROR_MSG_NAME, Constants.DIPLOMA_TASK_FAIL_MSG);
                logger.info("handle task-fail-msg, directiveContext={}", context);
            }
        } catch (Exception e) {
            logger.error("handle result failed : directiveContext={}", context, e);
        }
    }
}

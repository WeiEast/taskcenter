package com.treefinance.saas.taskcenter.biz.service.directive.process.impl;

import com.alibaba.fastjson.JSON;
import com.treefinance.saas.taskcenter.biz.service.MonitorService;
import com.treefinance.saas.taskcenter.biz.service.directive.process.AbstractCallbackDirectiveProcessor;
import com.treefinance.saas.taskcenter.biz.service.directive.process.DirectiveContext;
import com.treefinance.saas.taskcenter.common.enums.EDirective;
import com.treefinance.saas.taskcenter.common.enums.ETaskStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Map;

/**
 * 成功指令处理 Created by yh-treefinance on 2017/7/6.
 */
@Component
public class SuccessDirectiveProcessor extends AbstractCallbackDirectiveProcessor {
    @Autowired
    protected MonitorService monitorService;

    @Override
    public EDirective getSpecifiedDirective() {
        return EDirective.TASK_SUCCESS;
    }

    @Override
    protected void doProcess(DirectiveContext context) {
        Long taskId = context.getTaskId();

        // 记录任务日志
        taskLogService.insertTaskLog(taskId, "爬数任务执行完成", new Date(), null);

        // 生成数据map
        Map<String, Object> dataMap = generateDataMap(context);
        // 回调之前预处理
        precallback(dataMap, context);

        // 触发回调: 0-无需回调，1-回调成功，-1-回调失败
        int result = callback(dataMap, context);
        if (result == 0) {
            // 任务成功但是不需要回调(前端回调),仍需记录回调日志,获取dataUrl提供数据下载以及回调统计
            taskCallbackLogService.insert(null, taskId, (byte)2, JSON.toJSONString(dataMap), null, 0, 0);
            taskLogService.insertTaskLog(taskId, "回调通知成功", new Date(), null);

            context.updateTaskStatus(ETaskStatus.SUCCESS);
        } else if (result == 1) {
            context.updateTaskStatus(ETaskStatus.SUCCESS);
        } else {
            // 指令发生变更 ： task_success -> callback_fail
            taskNextDirectiveService.insert(taskId, context.getDirectiveString());

            context.updateTaskStatus(ETaskStatus.FAIL);

            context.updateDirective(EDirective.CALLBACK_FAIL);
        }
        // 更新任务状态
        String stepCode = taskService.updateStatusIfDone(taskId, context.getTaskStatus());
        context.updateStepCode(stepCode);

        // 发送监控消息
        monitorService.sendMonitorMessage(taskId);
    }

}

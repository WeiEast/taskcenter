package com.treefinance.saas.taskcenter.biz.service.monitor;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.treefinance.saas.assistant.model.TaskEmailMonitorMessage;
import com.treefinance.saas.assistant.model.TaskStep;
import com.treefinance.saas.assistant.plugin.TaskEmailMonitorPlugin;
import com.treefinance.saas.taskcenter.common.enums.EBizType;
import com.treefinance.saas.taskcenter.common.enums.ETaskStep;
import com.treefinance.saas.taskcenter.context.enums.EProcessStep;
import com.treefinance.saas.taskcenter.dao.repository.TaskAttributeRepository;
import com.treefinance.saas.taskcenter.dao.repository.TaskLogRepository;
import com.treefinance.saas.taskcenter.service.domain.TaskInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

import javax.annotation.Nonnull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by yh-treefinance on 2018/1/31.
 */
@Service
@Order(Ordered.HIGHEST_PRECEDENCE + 1)
public class EmailMonitor extends AbstractBusinessMonitor<TaskEmailMonitorMessage> {

    @Autowired
    private TaskAttributeRepository taskAttributeRepository;
    @Autowired
    private TaskLogRepository taskLogRepository;
    @Autowired
    private TaskEmailMonitorPlugin taskEmailMonitorPlugin;

    @Override
    public boolean support(EBizType bizType) {
        return EBizType.EMAIL.equals(bizType) || EBizType.EMAIL_H5.equals(bizType);
    }

    @Override
    protected TaskEmailMonitorMessage buildMonitorMessage(@Nonnull TaskInfo task) {
        TaskEmailMonitorMessage message = convertStrict(task, TaskEmailMonitorMessage.class);
        message.setSaasEnv(String.valueOf(task.getSaasEnv()));

        // 1.获取任务属性
        Long taskId = task.getId();
        final Map<String, String> attrMap = taskAttributeRepository.getAttributeMapByTaskId(taskId, false);
        Map<String, String> attributeMap = new HashMap<>(attrMap);
        attributeMap.putIfAbsent("email", task.getWebSite());
        message.setTaskAttributes(attributeMap);

        // 2.获取任务步骤
        Map<Integer, TaskStep> taskStepMap = Maps.newHashMap();

        List<String> taskLogMsgs = taskLogRepository.queryTaskLogMsgListByTaskIdAndInMsgs(taskId, ETaskStep.TASK_CREATE.getText(), ETaskStep.LOGIN_SUCCESS.getText(),
            ETaskStep.LOGIN_FAIL.getText(), ETaskStep.CRAWL_SUCCESS.getText(), ETaskStep.DATA_SAVE_SUCCESS.getText(), ETaskStep.CALLBACK_SUCCESS.getText());

        // 任务创建
        if (taskLogMsgs.contains(ETaskStep.TASK_CREATE.getText())) {
            taskStepMap.put(1, new TaskStep(1, EProcessStep.CREATE.getCode(), EProcessStep.CREATE.getName()));
        }
        // 确认登录
        if (taskLogMsgs.contains(ETaskStep.LOGIN_SUCCESS.getText()) || taskLogMsgs.contains(ETaskStep.LOGIN_FAIL.getText())) {
            taskStepMap.put(2, new TaskStep(2, EProcessStep.CONFIRM_LOGIN.getCode(), EProcessStep.CONFIRM_LOGIN.getName()));
        }
        // 登录成功
        if (taskLogMsgs.contains(ETaskStep.LOGIN_SUCCESS.getText())) {
            taskStepMap.put(3, new TaskStep(3, EProcessStep.LOGIN.getCode(), EProcessStep.LOGIN.getName()));
        }
        // 爬取成功
        if (taskLogMsgs.contains(ETaskStep.CRAWL_SUCCESS.getText())) {
            taskStepMap.put(4, new TaskStep(4, EProcessStep.CRAWL.getCode(), EProcessStep.CRAWL.getName()));
        }
        // 数据保存成功
        if (taskLogMsgs.contains(ETaskStep.DATA_SAVE_SUCCESS.getText())) {
            taskStepMap.put(5, new TaskStep(5, EProcessStep.PROCESS.getCode(), EProcessStep.PROCESS.getName()));
        }
        // 回调成功
        if (taskLogMsgs.contains(ETaskStep.CALLBACK_SUCCESS.getText())) {
            taskStepMap.put(6, new TaskStep(6, EProcessStep.CALLBACK.getCode(), EProcessStep.CALLBACK.getName()));
        }

        // 判断任务步骤是否正确或有遗漏
        List<TaskStep> taskSteps = Lists.newArrayList();
        for (int i = 1; i <= 6; i++) {
            if (!taskStepMap.keySet().contains(i)) {
                break;
            }
            taskSteps.add(taskStepMap.get(i));
        }
        message.setTaskSteps(taskSteps);

        return message;
    }

    @Override
    protected void doSending(@Nonnull TaskEmailMonitorMessage message) {
        taskEmailMonitorPlugin.sendMessage(message);
    }
}

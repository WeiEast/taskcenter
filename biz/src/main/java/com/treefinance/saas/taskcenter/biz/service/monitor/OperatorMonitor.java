package com.treefinance.saas.taskcenter.biz.service.monitor;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.treefinance.saas.assistant.model.TaskOperatorMonitorMessage;
import com.treefinance.saas.assistant.model.TaskStep;
import com.treefinance.saas.assistant.plugin.TaskOperatorMonitorPlugin;
import com.treefinance.saas.taskcenter.common.enums.EBizType;
import com.treefinance.saas.taskcenter.common.enums.ETaskStep;
import com.treefinance.saas.taskcenter.context.enums.EProcessStep;
import com.treefinance.saas.taskcenter.dao.repository.TaskAttributeRepository;
import com.treefinance.saas.taskcenter.dao.repository.TaskBuryPointRepository;
import com.treefinance.saas.taskcenter.dao.repository.TaskLogRepository;
import com.treefinance.saas.taskcenter.service.domain.TaskInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

import javax.annotation.Nonnull;

import java.util.List;
import java.util.Map;

/**
 * Buddha Bless , No Bug !
 *
 * @author haojiahong
 * @date 2018/4/2
 */
@Service
@Order(Ordered.HIGHEST_PRECEDENCE + 1)
public class OperatorMonitor extends AbstractBusinessMonitor<TaskOperatorMonitorMessage> {

    @Autowired
    private TaskAttributeRepository taskAttributeRepository;
    @Autowired
    private TaskLogRepository taskLogRepository;
    @Autowired
    private TaskOperatorMonitorPlugin taskOperatorMonitorPlugin;
    @Autowired
    private TaskBuryPointRepository taskBuryPointRepository;

    @Override
    public boolean support(EBizType bizType) {
        return EBizType.OPERATOR.equals(bizType);
    }

    @Override
    protected TaskOperatorMonitorMessage buildMonitorMessage(@Nonnull TaskInfo task) {
        Long taskId = task.getId();
        TaskOperatorMonitorMessage message = convertStrict(task, TaskOperatorMonitorMessage.class);
        message.setTaskId(taskId);
        message.setSaasEnv(String.valueOf(task.getSaasEnv()));
        // 1.获取任务属性
        Map<String, String> attributeMap = taskAttributeRepository.getAttributeMapByTaskId(taskId, false);
        message.setTaskAttributes(attributeMap);

        // 2.获取任务步骤
        Map<Integer, TaskStep> taskStepMap = Maps.newHashMap();

        List<String> taskLogMsgs = taskLogRepository.queryTaskLogMsgListByTaskIdAndInMsgs(taskId, ETaskStep.TASK_CREATE.getText(), ETaskStep.LOGIN_SUCCESS.getText(),
            ETaskStep.CRAWL_SUCCESS.getText(), ETaskStep.DATA_SAVE_SUCCESS.getText(), ETaskStep.CALLBACK_SUCCESS.getText());

        // 任务创建
        if (taskLogMsgs.contains(ETaskStep.TASK_CREATE.getText())) {
            taskStepMap.put(1, new TaskStep(1, EProcessStep.CREATE.getCode(), EProcessStep.CREATE.getName()));
        }
        // 确认手机号
        if (taskBuryPointRepository.doesAnyExist(taskId, "300502")) {
            taskStepMap.put(2, new TaskStep(2, EProcessStep.CONFIRM_MOBILE.getCode(), EProcessStep.CONFIRM_MOBILE.getName()));
        }
        // 开始登陆
        if (taskBuryPointRepository.doesAnyExist(taskId, "300701")) {
            taskStepMap.put(3, new TaskStep(3, EProcessStep.CONFIRM_LOGIN.getCode(), EProcessStep.CONFIRM_LOGIN.getName()));
        }
        // 登录成功
        if (taskLogMsgs.contains(ETaskStep.LOGIN_SUCCESS.getText())) {
            taskStepMap.put(4, new TaskStep(4, EProcessStep.LOGIN.getCode(), EProcessStep.LOGIN.getName()));
        }
        // 爬取成功
        if (taskLogMsgs.contains(ETaskStep.CRAWL_SUCCESS.getText())) {
            taskStepMap.put(5, new TaskStep(5, EProcessStep.CRAWL.getCode(), EProcessStep.CRAWL.getName()));
        }
        // 数据保存成功
        if (taskLogMsgs.contains(ETaskStep.DATA_SAVE_SUCCESS.getText())) {
            taskStepMap.put(6, new TaskStep(6, EProcessStep.PROCESS.getCode(), EProcessStep.PROCESS.getName()));
        }
        // 回调成功
        if (taskLogMsgs.contains(ETaskStep.CALLBACK_SUCCESS.getText())) {
            taskStepMap.put(7, new TaskStep(7, EProcessStep.CALLBACK.getCode(), EProcessStep.CALLBACK.getName()));
        }
        // 3.判断任务步骤是否正确或有遗漏
        List<TaskStep> taskSteps = Lists.newArrayList();
        for (int i = 1; i <= 7; i++) {
            if (!taskStepMap.keySet().contains(i)) {
                break;
            }
            taskSteps.add(taskStepMap.get(i));
        }
        message.setTaskSteps(taskSteps);

        return message;
    }

    @Override
    protected void doSending(@Nonnull TaskOperatorMonitorMessage message) {
        taskOperatorMonitorPlugin.sendMessage(message);
    }

}

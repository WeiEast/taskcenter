package com.treefinance.saas.taskcenter.biz.service.monitor;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.treefinance.saas.assistant.model.TaskEcommeceMonitorMessage;
import com.treefinance.saas.assistant.model.TaskStep;
import com.treefinance.saas.assistant.plugin.TaskEcommerceMonitorPlugin;
import com.treefinance.saas.taskcenter.biz.service.TaskLogService;
import com.treefinance.saas.taskcenter.context.enums.EProcessStep;
import com.treefinance.saas.taskcenter.context.enums.ETaskStep;
import com.treefinance.saas.taskcenter.dao.entity.TaskAttribute;
import com.treefinance.saas.taskcenter.dao.entity.TaskBuryPointLog;
import com.treefinance.saas.taskcenter.dao.entity.TaskLog;
import com.treefinance.saas.taskcenter.facade.enums.EBizType;
import com.treefinance.saas.taskcenter.service.TaskAttributeService;
import com.treefinance.saas.taskcenter.service.TaskBuryPointLogService;
import com.treefinance.saas.taskcenter.service.domain.TaskInfo;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by yh-treefinance on 2018/1/31.
 */
@Service
@Order(Ordered.HIGHEST_PRECEDENCE + 1)
public class EcommerceMonitor extends AbstractBusinessMonitor<TaskEcommeceMonitorMessage> {

    @Autowired
    private TaskEcommerceMonitorPlugin taskEcommerceMonitorPlugin;
    @Autowired
    private TaskAttributeService taskAttributeService;
    @Autowired
    private TaskBuryPointLogService taskBuryPointLogService;
    @Autowired
    private TaskLogService taskLogService;

    @Override
    public boolean support(EBizType bizType) {
        return EBizType.ECOMMERCE.equals(bizType);
    }

    @Override
    protected TaskEcommeceMonitorMessage buildMonitorMessage(TaskInfo task) {
        Long taskId = task.getId();
        TaskEcommeceMonitorMessage message = convert(task, TaskEcommeceMonitorMessage.class);
        message.setSaasEnv(String.valueOf(task.getSaasEnv()));
        // 1.获取任务属性
        List<TaskAttribute> attributeList = taskAttributeService.listAttributesByTaskId(taskId);
        Map<String, String> attributeMap = Maps.newHashMap();
        if (CollectionUtils.isNotEmpty(attributeList)) {
            attributeList.forEach(taskAttribute -> attributeMap.put(taskAttribute.getName(), taskAttribute.getValue()));
        }
        message.setTaskAttributes(attributeMap);

        // 2.获取任务步骤
        List<TaskStep> taskSteps = Lists.newArrayList();
        Map<Integer, TaskStep> taskStepMap = Maps.newHashMap();
        List<TaskLog> taskLogs = taskLogService.queryTaskLogsByTaskIdAndInSteps(taskId, ETaskStep.TASK_CREATE, ETaskStep.LOGIN_SUCCESS, ETaskStep.LOGIN_FAIL,
            ETaskStep.CRAWL_SUCCESS, ETaskStep.DATA_SAVE_SUCCESS, ETaskStep.CALLBACK_SUCCESS);

        List<String> taskLogMsgs = taskLogs.stream().map(TaskLog::getMsg).collect(Collectors.toList());
        // 任务创建
        if (taskLogMsgs.contains(ETaskStep.TASK_CREATE.getText())) {
            taskStepMap.put(1, new TaskStep(1, EProcessStep.CREATE.getCode(), EProcessStep.CREATE.getName()));
        }
        // 一键登录
        String sourceType = attributeMap.get("sourceType");

        if (StringUtils.equalsIgnoreCase("1", sourceType)) {
            // 来源sdk
            // sdk没有一键登录按钮,设置一键登录按钮数量与创建任务数量一致
            if (taskLogMsgs.contains(ETaskStep.TASK_CREATE.getText())) {
                taskStepMap.put(2, new TaskStep(2, EProcessStep.ONE_CLICK_LOGIN.getCode(), EProcessStep.ONE_CLICK_LOGIN.getName()));
            }
        } else if (StringUtils.equalsIgnoreCase("2", sourceType)) {
            // 来源h5
            // H5版本基于埋点数据，判断是否一键登录
            List<TaskBuryPointLog> taskBuryPointLogs = taskBuryPointLogService.queryTaskBuryPointLogByCode(taskId, "100803");
            if (CollectionUtils.isNotEmpty(taskBuryPointLogs)) {
                taskStepMap.put(2, new TaskStep(2, EProcessStep.ONE_CLICK_LOGIN.getCode(), EProcessStep.ONE_CLICK_LOGIN.getName()));
            }
        } else {
            // 其他来源
            logger.info("send task ecommerce message to saas-monitor,存在未知任务来源sourceType={},task={}", sourceType, JSON.toJSONString(task));
        }

        // 确认登录
        if (taskLogMsgs.contains(ETaskStep.LOGIN_SUCCESS.getText()) || taskLogMsgs.contains(ETaskStep.LOGIN_FAIL.getText())) {
            if (StringUtils.equalsIgnoreCase("2", sourceType) && taskStepMap.get(2) == null) {// h5可能不点击一键登录按钮,直接截图通过app扫描二维码
                taskStepMap.put(2, new TaskStep(2, EProcessStep.ONE_CLICK_LOGIN.getCode(), EProcessStep.ONE_CLICK_LOGIN.getName()));
            }
            taskStepMap.put(3, new TaskStep(3, EProcessStep.CONFIRM_LOGIN.getCode(), EProcessStep.CONFIRM_LOGIN.getName()));
        }
        // 登录成功
        if (taskLogMsgs.contains(ETaskStep.LOGIN_SUCCESS.getText())) {
            if (StringUtils.equalsIgnoreCase("2", sourceType) && taskStepMap.get(2) == null) {// h5可能不点击一键登录按钮,直接截图通过app扫描二维码
                taskStepMap.put(2, new TaskStep(2, EProcessStep.ONE_CLICK_LOGIN.getCode(), EProcessStep.ONE_CLICK_LOGIN.getName()));
            }
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

        // 判断任务步骤是否正确或有遗漏
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
    protected void doSending(TaskEcommeceMonitorMessage message) {
        taskEcommerceMonitorPlugin.sendMessage(message);
    }

}

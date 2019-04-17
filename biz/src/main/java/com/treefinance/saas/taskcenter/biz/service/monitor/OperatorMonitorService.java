package com.treefinance.saas.taskcenter.biz.service.monitor;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.treefinance.saas.assistant.model.TaskOperatorMonitorMessage;
import com.treefinance.saas.assistant.model.TaskStep;
import com.treefinance.saas.assistant.plugin.TaskOperatorMonitorPlugin;
import com.treefinance.saas.taskcenter.service.TaskAttributeService;
import com.treefinance.saas.taskcenter.service.TaskBuryPointLogService;
import com.treefinance.saas.taskcenter.biz.service.TaskLogService;
import com.treefinance.saas.taskcenter.service.impl.AbstractService;
import com.treefinance.saas.taskcenter.context.enums.EProcessStep;
import com.treefinance.saas.taskcenter.context.enums.ETaskStep;
import com.treefinance.saas.taskcenter.dao.entity.TaskAttribute;
import com.treefinance.saas.taskcenter.dao.entity.TaskBuryPointLog;
import com.treefinance.saas.taskcenter.dao.entity.TaskLog;
import com.treefinance.saas.taskcenter.dto.TaskDTO;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Buddha Bless , No Bug !
 *
 * @author haojiahong
 * @date 2018/4/2
 */
@Service
public class OperatorMonitorService extends AbstractService {
    private static final Logger logger = LoggerFactory.getLogger(OperatorMonitorService.class);

    @Autowired
    private TaskOperatorMonitorPlugin taskOperatorMonitorPlugin;
    @Autowired
    private TaskAttributeService taskAttributeService;
    @Autowired
    private TaskBuryPointLogService taskBuryPointLogService;
    @Autowired
    private TaskLogService taskLogService;

    /**
     * 发送消息
     *
     * @param taskDTO
     */
    public void sendMessage(TaskDTO taskDTO) {
        long start = System.currentTimeMillis();
        Long taskId = taskDTO.getId();
        TaskOperatorMonitorMessage message = convert(taskDTO, TaskOperatorMonitorMessage.class);
        message.setTaskId(taskDTO.getId());
        message.setSaasEnv(String.valueOf(taskDTO.getSaasEnv()));
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
        List<TaskLog> taskLogs = taskLogService.queryTaskLogsByTaskIdAndInSteps(taskId, ETaskStep.TASK_CREATE, ETaskStep.LOGIN_SUCCESS, ETaskStep.CRAWL_SUCCESS,
            ETaskStep.DATA_SAVE_SUCCESS, ETaskStep.CALLBACK_SUCCESS);
        List<String> taskLogMsgs = taskLogs.stream().map(TaskLog::getMsg).collect(Collectors.toList());
        // 任务创建
        if (taskLogMsgs.contains(ETaskStep.TASK_CREATE.getText())) {
            taskStepMap.put(1, new TaskStep(1, EProcessStep.CREATE.getCode(), EProcessStep.CREATE.getName()));
        }
        // 确认手机号
        List<TaskBuryPointLog> confirmMobileList = taskBuryPointLogService.queryTaskBuryPointLogByCode(taskId, "300502");
        if (CollectionUtils.isNotEmpty(confirmMobileList)) {
            taskStepMap.put(2, new TaskStep(2, EProcessStep.CONFIRM_MOBILE.getCode(), EProcessStep.CONFIRM_MOBILE.getName()));
        }
        // 开始登陆
        List<TaskBuryPointLog> startLoginList = taskBuryPointLogService.queryTaskBuryPointLogByCode(taskId, "300701");
        if (CollectionUtils.isNotEmpty(startLoginList)) {
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
        for (int i = 1; i <= 7; i++) {
            if (!taskStepMap.keySet().contains(i)) {
                break;
            }
            taskSteps.add(taskStepMap.get(i));
        }
        message.setTaskSteps(taskSteps);

        // 4.发送消息
        taskOperatorMonitorPlugin.sendMessage(message);
        logger.info("send task operator message to saas-monitor cost{}ms , message={}", System.currentTimeMillis() - start, JSON.toJSONString(message));
    }

}

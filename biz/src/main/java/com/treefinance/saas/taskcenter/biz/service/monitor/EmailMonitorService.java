package com.treefinance.saas.taskcenter.biz.service.monitor;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.treefinance.saas.assistant.model.TaskEmailMonitorMessage;
import com.treefinance.saas.assistant.model.TaskStep;
import com.treefinance.saas.assistant.plugin.TaskEmailMonitorPlugin;
import com.treefinance.saas.taskcenter.biz.service.TaskAttributeService;
import com.treefinance.saas.taskcenter.biz.service.TaskBuryPointLogService;
import com.treefinance.saas.taskcenter.biz.service.TaskLogService;
import com.treefinance.saas.taskcenter.biz.utils.DataConverterUtils;
import com.treefinance.saas.taskcenter.common.enums.EProcessStep;
import com.treefinance.saas.taskcenter.common.enums.ETaskStep;
import com.treefinance.saas.taskcenter.common.model.dto.TaskDTO;
import com.treefinance.saas.taskcenter.dao.entity.TaskAttribute;
import com.treefinance.saas.taskcenter.dao.entity.TaskLog;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by yh-treefinance on 2018/1/31.
 */
@Service
public class EmailMonitorService {

    private static final Logger logger = LoggerFactory.getLogger(EmailMonitorService.class);

    @Autowired
    private TaskEmailMonitorPlugin taskEmailMonitorPlugin;
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
        TaskEmailMonitorMessage message = DataConverterUtils.convert(taskDTO, TaskEmailMonitorMessage.class);
        message.setSaasEnv(String.valueOf(taskDTO.getSaasEnv()));
        // 1.获取任务属性
        List<TaskAttribute> attributeList = taskAttributeService.findByTaskId(taskId);
        Map<String, String> attributeMap = Maps.newHashMap();
        attributeMap.put("email", taskDTO.getWebSite());
        if (CollectionUtils.isNotEmpty(attributeList)) {
            attributeList.forEach(taskAttribute -> attributeMap.put(taskAttribute.getName(), taskAttribute.getValue()));
        }
        message.setTaskAttributes(attributeMap);

        // 2.获取任务步骤
        List<TaskStep> taskSteps = Lists.newArrayList();
        Map<Integer, TaskStep> taskStepMap = Maps.newHashMap();

        List<TaskLog> taskLogs = taskLogService.queryTaskLog(taskId, ETaskStep.TASK_CREATE.getText(),
                ETaskStep.LOGIN_SUCCESS.getText(), ETaskStep.LOGIN_FAIL.getText(), ETaskStep.CRAWL_SUCCESS.getText(),
                ETaskStep.DATA_SAVE_SUCCESS.getText(), ETaskStep.CALLBACK_SUCCESS.getText());
        List<String> taskLogMsgs = taskLogs.stream().map(TaskLog::getMsg).collect(Collectors.toList());
        // 任务创建
        if (taskLogMsgs.contains(ETaskStep.TASK_CREATE.getText())) {
            taskStepMap.put(1, new TaskStep(1, EProcessStep.CREATE.getCode(), EProcessStep.CREATE.getName()));
        }
        // 确认登录
        if (taskLogMsgs.contains(ETaskStep.LOGIN_SUCCESS.getText()) || taskLogMsgs.contains(ETaskStep.LOGIN_FAIL.getText())) {
            taskStepMap.put(2, new TaskStep(2, EProcessStep.CONFIRM_LOGIN.getCode(), EProcessStep.CONFIRM_LOGIN.getName()));
        }
        //登录成功
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

        //判断任务步骤是否正确或有遗漏
        for (int i = 1; i <= 6; i++) {
            if (!taskStepMap.keySet().contains(i)) {
                break;
            }
            taskSteps.add(taskStepMap.get(i));
        }
        message.setTaskSteps(taskSteps);

        // 4.发送消息
        taskEmailMonitorPlugin.sendMessage(message);
        logger.info("send task email message to saas-monitor cost{}ms , message={}", System.currentTimeMillis() - start, JSON.toJSONString(message));
    }
}
package com.treefinance.saas.taskcenter.biz.service.monitor;

import com.alibaba.fastjson.JSON;
import com.treefinance.saas.assistant.model.TaskRealTimeMonitorMessage;
import com.treefinance.saas.assistant.plugin.rocketmq.producer.MonitorMessageProducer;
import com.treefinance.saas.taskcenter.context.enums.ETaskStatLink;
import com.treefinance.saas.taskcenter.dao.entity.Task;
import com.treefinance.saas.taskcenter.dao.repository.TaskAttributeRepository;
import com.treefinance.saas.taskcenter.dao.repository.TaskRepository;
import com.treefinance.toolkit.util.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Good Luck Bro , No Bug ! 任务实时监控
 *
 * @author haojiahong
 * @date 2018/6/19
 */
@Service
public class TaskRealTimeStatMonitorImpl implements TaskRealTimeStatMonitor{
    private static final Logger logger = LoggerFactory.getLogger(TaskRealTimeStatMonitorImpl.class);
    /**
     * 需要监控的日志环节
     */
    private static List<String> logLinkList = ETaskStatLink.getStepCodeListBySource("task_log");
    @Autowired
    private TaskRepository taskRepository;
    @Autowired
    private TaskAttributeRepository taskAttributeRepository;
    @Autowired
    private MonitorMessageProducer monitorMessageProducer;

    @Override
    public void sendMessage(Long taskId, String code, Date dataTime) {
        logger.info("任务实时监控日志环节处理,taskId={},code={},dataTime={}", taskId, code, DateUtils.format(dataTime));
        if (!logLinkList.contains(code)) {
            return;
        }
        ETaskStatLink taskStatLink = ETaskStatLink.getItemByStepCode(code);
        if (taskStatLink == null) {
            logger.error("任务实时监控日志环节处理,需统计的任务环节未在枚举定义中找到,taskId={},code={}", taskId, code);
            return;
        }
        Task task = taskRepository.getTaskById(taskId);
        if (task == null) {
            return;
        }
        String taskLinkStatCode = taskStatLink.getStatCode();
        String taskLinkStatName = taskStatLink.getDesc();

        TaskRealTimeMonitorMessage message = new TaskRealTimeMonitorMessage();
        message.setTaskId(task.getId());
        message.setSaasEnv(String.valueOf(task.getSaasEnv()));
        message.setAccountNo(task.getAccountNo());
        message.setAppId(task.getAppId());
        message.setBizType(task.getBizType());
        message.setDataTime(dataTime);
        message.setStatus(task.getStatus());
        message.setUniqueId(task.getUniqueId());
        message.setWebSite(task.getWebSite());
        message.setStatCode(taskLinkStatCode);
        message.setStatName(taskLinkStatName);

        // 获取任务属性
        Map<String, String> attributeMap = taskAttributeRepository.getAttributeMapByTaskId(taskId, false);
        message.setTaskAttributes(attributeMap);
        monitorMessageProducer.send(message);
        logger.info("任务实时监控日志环节处理,发送消息,taskId={},code={},message={}", taskId, code, JSON.toJSONString(message));

    }
}

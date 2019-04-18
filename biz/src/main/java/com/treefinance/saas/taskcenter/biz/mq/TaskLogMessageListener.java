package com.treefinance.saas.taskcenter.biz.mq;

import com.alibaba.fastjson.JSON;
import com.treefinance.saas.taskcenter.biz.mq.model.TaskLogMessage;
import com.treefinance.saas.taskcenter.biz.service.TaskLogService;
import com.treefinance.saas.taskcenter.biz.service.TaskService;
import com.treefinance.saas.taskcenter.context.config.MqConfig;
import com.treefinance.saas.taskcenter.share.mq.ConsumeSetting;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * Created by luoyihua on 2017/4/26.
 */
@Service
public class TaskLogMessageListener extends AbstractRocketMqMessageListener {

    @Autowired
    private MqConfig mqConfig;
    @Autowired
    private TaskLogService taskLogService;
    @Autowired
    private TaskService taskService;

    @Override
    public ConsumeSetting getConsumeSetting() {
        ConsumeSetting consumeSetting = new ConsumeSetting();
        consumeSetting.setGroup(mqConfig.getTaskLogGroupName());
        consumeSetting.setTopic(mqConfig.getConsumeTaskLogTopic());
        consumeSetting.setTags(mqConfig.getConsumeTaskLogTag());

        return consumeSetting;
    }

    @Override
    protected void handleMessage(String msgBody) {
        logger.info("消费TaskLog消息数据==>{}", msgBody);
        TaskLogMessage taskLogMessage = JSON.parseObject(msgBody, TaskLogMessage.class);
        if (taskLogMessage != null) {
            Long taskId = taskLogMessage.getTaskId();
            if (taskService.isTaskCompleted(taskId)) {
                logger.info("任务已完成，不再更新日志：message={}", taskLogMessage);
                return;
            }
            Date processTime = new Date(taskLogMessage.getTimestamp());
            taskLogService.insertTaskLog(taskId, taskLogMessage.getMsg(), processTime, taskLogMessage.getErrorDetail());
            logger.info("日志保存成功：message={}", taskLogMessage);
        }
    }
}

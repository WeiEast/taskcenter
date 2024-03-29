package com.treefinance.saas.taskcenter.biz.mq;

import com.alibaba.fastjson.JSON;
import com.treefinance.saas.assistant.model.TaskCallbackFailureReasonMessage;
import com.treefinance.saas.assistant.plugin.rocketmq.producer.MonitorMessageProducer;
import com.treefinance.saas.taskcenter.biz.mq.model.CallbackFailureReasonMessage;
import com.treefinance.saas.taskcenter.biz.service.TaskService;
import com.treefinance.saas.taskcenter.common.enums.EBizType;
import com.treefinance.saas.taskcenter.dao.repository.TaskCallbackLogRepository;
import com.treefinance.saas.taskcenter.service.TaskAttributeService;
import com.treefinance.saas.taskcenter.service.domain.TaskInfo;
import com.treefinance.saas.taskcenter.share.mq.ConsumeSetting;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;

import java.util.Map;

/**
 * Good Luck Bro , No Bug !
 * <p>
 * 接受爬数指明的回调失败具体原因, 方便统计出回调是个人原因还是系统或网站的原因.
 *
 * @author haojiahong
 * @date 2018/6/11
 */
@Component
public class CallbackFailureReasonMessageListener extends AbstractJsonMessageListener<CallbackFailureReasonMessage> {

    @Autowired
    private TaskCallbackLogRepository taskCallbackLogRepository;
    @Autowired
    private TaskService taskService;
    @Autowired
    private TaskAttributeService taskAttributeService;
    @Autowired
    private MonitorMessageProducer monitorMessageProducer;

    @Override
    public ConsumeSetting getConsumeSetting() {
        ConsumeSetting consumeSetting = new ConsumeSetting();
        consumeSetting.setGroup("callback_failure_reason");
        consumeSetting.setTopic("task-callback");
        consumeSetting.setTags("callback");

        return consumeSetting;
    }

    @Override
    protected void processMessage(@Nonnull CallbackFailureReasonMessage message) {
        Long taskId = message.getTaskId();
        Long callbackConfigId = message.getCallbackConfigId();
        Byte failureReason = message.getFailureReason();
        // 更新回调日志表
        taskCallbackLogRepository.insertOrUpdateLog(taskId, callbackConfigId, failureReason);

        // 发送监控消息
        TaskInfo task = taskService.getTaskInfoById(taskId);
        if (!EBizType.OPERATOR.getCode().equals(task.getBizType())) {
            logger.warn("接收爬数发送的回调失败具体原因,任务类型非运营商,暂不处理.message={},task={}", message, task);
            return;
        }

        TaskCallbackFailureReasonMessage taskCallbackFailureReasonMessage = convertStrict(task, TaskCallbackFailureReasonMessage.class);
        taskCallbackFailureReasonMessage.setTaskId(task.getId());
        taskCallbackFailureReasonMessage.setDataTime(task.getCreateTime());
        taskCallbackFailureReasonMessage.setFailureReason(failureReason);
        Map<String, String> attributeMap = taskAttributeService.getAttributeMapByTaskId(taskId, false);
        taskCallbackFailureReasonMessage.setTaskAttributes(attributeMap);
        monitorMessageProducer.send(taskCallbackFailureReasonMessage);
        logger.info("send task callback failure reason to saas-monitor,message={}", JSON.toJSONString(taskCallbackFailureReasonMessage));
    }
}

package com.treefinance.saas.taskcenter.biz.mq;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Maps;
import com.treefinance.saas.assistant.model.TaskCallbackFailureReasonMessage;
import com.treefinance.saas.assistant.plugin.rocketmq.producer.MonitorMessageProducer;
import com.treefinance.saas.taskcenter.biz.service.TaskAttributeService;
import com.treefinance.saas.taskcenter.biz.service.TaskService;
import com.treefinance.saas.taskcenter.facade.enums.EBizType;
import com.treefinance.saas.taskcenter.dao.entity.TaskAttribute;
import com.treefinance.saas.taskcenter.dao.repository.TaskCallbackLogRepository;
import com.treefinance.saas.taskcenter.dto.CallbackFailureReasonDTO;
import com.treefinance.saas.taskcenter.dto.TaskDTO;
import com.treefinance.saas.taskcenter.share.mq.ConsumeSetting;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * Good Luck Bro , No Bug !
 * <p>
 * 接受爬数指明的回调失败具体原因,
 * 方便统计出回调是个人原因还是系统或网站的原因.
 *
 * @author haojiahong
 * @date 2018/6/11
 */
@Component
public class CallbackFailureReasonMessageListener extends AbstractRocketMqMessageListener {

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
    protected void handleMessage(String message) {
        if (StringUtils.isBlank(message)) {
            logger.error("接收爬数发送的回调失败具体原因为空,message={}", message);
            throw new IllegalArgumentException("接收爬数发送的回调失败具体原因为空,message=" + message);
        }
        CallbackFailureReasonDTO callbackFailureReasonDTO = JSON.parseObject(message, CallbackFailureReasonDTO.class);
        //更新回调日志表
        Long taskId = callbackFailureReasonDTO.getTaskId();
        Long callbackConfigId = callbackFailureReasonDTO.getCallbackConfigId();
        Byte failureReason = callbackFailureReasonDTO.getFailureReason();
        taskCallbackLogRepository.insertOrUpdateLog(taskId, callbackConfigId, failureReason);

        //发送监控消息
        TaskDTO taskDTO = taskService.getById(taskId);
        if (!EBizType.OPERATOR.getCode().equals(taskDTO.getBizType())) {
            logger.info("接收爬数发送的回调失败具体原因,任务类型非运营商,暂不处理.message={},task={}", message, JSON.toJSONString(taskDTO));
            return;
        }
        TaskCallbackFailureReasonMessage taskCallbackFailureReasonMessage = convertStrict(taskDTO, TaskCallbackFailureReasonMessage.class);
        taskCallbackFailureReasonMessage.setTaskId(taskDTO.getId());
        taskCallbackFailureReasonMessage.setDataTime(taskDTO.getCreateTime());
        taskCallbackFailureReasonMessage.setFailureReason(failureReason);
        List<TaskAttribute> attributeList = taskAttributeService.findByTaskId(taskId);
        Map<String, String> attributeMap = Maps.newHashMap();
        if (CollectionUtils.isNotEmpty(attributeList)) {
            attributeList.forEach(taskAttribute -> attributeMap.put(taskAttribute.getName(), taskAttribute.getValue()));
        }
        taskCallbackFailureReasonMessage.setTaskAttributes(attributeMap);
        monitorMessageProducer.send(taskCallbackFailureReasonMessage);
        logger.info("send task callback failure reason to saas-monitor,message={}", JSON.toJSONString(taskCallbackFailureReasonMessage));
    }
}

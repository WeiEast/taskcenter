package com.treefinance.saas.taskcenter.biz.mq;

import com.alibaba.fastjson.JSON;
import com.treefinance.saas.taskcenter.biz.service.GrapDataCallbackService;
import com.treefinance.saas.taskcenter.biz.service.TaskLogService;
import com.treefinance.saas.taskcenter.context.enums.EDataType;
import com.treefinance.saas.taskcenter.dto.AsycGrapDTO;
import com.treefinance.saas.taskcenter.share.mq.ConsumeSetting;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.Date;

/**
 * 异步数据回调处理 Created by yh-treefinance on 2017/12/19.
 */
@Component
public class AsyncGrapDataMessageListener extends AbstractRocketMqMessageListener {
    @Autowired
    protected GrapDataCallbackService grapDataCallbackService;
    @Autowired
    private TaskLogService taskLogService;

    @Override
    public ConsumeSetting getConsumeSetting() {
        ConsumeSetting consumeSetting = new ConsumeSetting();
        consumeSetting.setGroup("async_grap_data_group");
        consumeSetting.setTopic("async-grap-data");
        consumeSetting.setTags("*");

        return consumeSetting;
    }

    @Override
    protected void handleMessage(String json) {
        Assert.notNull(json, "message body can't be null");
        // json --> 消息
        AsycGrapDTO asycGrapDTO = JSON.parseObject(json, AsycGrapDTO.class);
        if (asycGrapDTO == null) {
            logger.info("异步数据回调处理,接收到的消息数据为空,message={}", json);
            return;
        }
        Long taskId = asycGrapDTO.getTaskId();
        EDataType dataType = EDataType.typeOf(asycGrapDTO.getDataType().byteValue());
        if (dataType == null || taskId == null) {
            logger.info("异步数据回调处理,接收到的消息数据有误,message={}", json);
            return;
        }
        taskLogService.insertTaskLog(taskId, dataType.getName() + "爬取完成", new Date(), "");
        // 处理数据
        grapDataCallbackService.handleAyscData(asycGrapDTO);
    }
}

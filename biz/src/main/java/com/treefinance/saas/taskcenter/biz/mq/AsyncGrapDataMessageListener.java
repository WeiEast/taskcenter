package com.treefinance.saas.taskcenter.biz.mq;

import com.alibaba.fastjson.JSON;
import com.treefinance.saas.taskcenter.biz.service.GrapDataCallbackService;
import com.treefinance.saas.taskcenter.biz.service.impl.TaskLogServiceImpl;
import com.treefinance.saas.taskcenter.common.enums.EDataType;
import com.treefinance.saas.taskcenter.common.model.dto.AsycGrapDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.Date;

/**
 * 异步数据回调处理
 * Created by yh-treefinance on 2017/12/19.
 */
@Component
public class AsyncGrapDataMessageListener extends AbstractRocketMqMessageListener {
    @Autowired
    protected GrapDataCallbackService grapDataCallbackService;
    @Autowired
    private TaskLogServiceImpl taskLogService;


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

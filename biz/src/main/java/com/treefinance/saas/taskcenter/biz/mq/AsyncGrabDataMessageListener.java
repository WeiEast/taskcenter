package com.treefinance.saas.taskcenter.biz.mq;

import com.alibaba.fastjson.JSON;
import com.treefinance.saas.taskcenter.biz.callback.AsyncGrabDataHandler;
import com.treefinance.saas.taskcenter.biz.callback.AsyncGrabMessage;
import com.treefinance.saas.taskcenter.biz.service.TaskLogService;
import com.treefinance.saas.taskcenter.context.enums.EDataType;
import com.treefinance.saas.taskcenter.share.mq.ConsumeSetting;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * 异步数据回调处理
 * 
 * @author yh-treefinance
 * @date 2017/12/19.
 */
@Component
public class AsyncGrabDataMessageListener extends AbstractRocketMqMessageListener {
    @Autowired
    protected AsyncGrabDataHandler asyncGrabDataHandler;
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
    protected void handleMessage(String msgBody) {
        AsyncGrabMessage asyncGrabMessage = JSON.parseObject(msgBody, AsyncGrabMessage.class);
        if (asyncGrabMessage == null) {
            logger.info("异步数据回调处理,接收到的消息数据为空,message={}", msgBody);
            return;
        }
        Long taskId = asyncGrabMessage.getTaskId();
        EDataType dataType = EDataType.typeOf(asyncGrabMessage.getDataType().byteValue());
        if (dataType == null || taskId == null) {
            logger.info("异步数据回调处理,接收到的消息数据有误,message={}", msgBody);
            return;
        }
        taskLogService.insertTaskLog(taskId, dataType.getName() + "爬取完成", new Date(), "");
        // 处理数据
        asyncGrabDataHandler.handle(asyncGrabMessage);
    }
}

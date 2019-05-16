package com.treefinance.saas.taskcenter.biz.mq;

import com.treefinance.saas.taskcenter.biz.callback.AsyncGrabDataHandler;
import com.treefinance.saas.taskcenter.biz.callback.AsyncGrabMessage;
import com.treefinance.saas.taskcenter.biz.service.TaskLogService;
import com.treefinance.saas.taskcenter.context.enums.EDataType;
import com.treefinance.saas.taskcenter.share.mq.ConsumeSetting;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;

import java.util.Date;

/**
 * 异步数据回调处理
 * 
 * @author yh-treefinance
 * @date 2017/12/19.
 */
@Component
public class AsyncGrabDataMessageListener extends AbstractJsonMessageListener<AsyncGrabMessage> {
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
    protected void processMessage(@Nonnull AsyncGrabMessage message) {
        Long taskId = message.getTaskId();
        if (taskId == null) {
            logger.warn("非法数据,taskId丢失. message={}", message);
            return;
        }

        final Integer dType = message.getDataType();
        EDataType dataType = dType == null ? null : EDataType.typeOf(dType.byteValue());
        if (dataType == null) {
            logger.warn("非法数据,dataType不正确. message={}", message);
            return;
        }

        taskLogService.insertTaskLog(taskId, dataType.getName() + "爬取完成", new Date(), "");

        // 处理数据
        asyncGrabDataHandler.handle(message);
    }
}

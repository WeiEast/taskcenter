package com.treefinance.saas.taskcenter.biz.service.monitor;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Maps;
import com.treefinance.saas.assistant.model.TaskCallbackMsgMonitorMessage;
import com.treefinance.saas.assistant.plugin.TaskCallbackMsgMonitorPlugin;
import com.treefinance.saas.taskcenter.service.domain.TaskInfo;
import com.treefinance.saas.taskcenter.service.param.CallbackRecordObject;
import com.treefinance.saas.taskcenter.service.param.CallbackRecordObject.CallbackResult;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import java.util.Map;

/**
 * Buddha Bless , No Bug !
 *
 * @author haojiahong
 * @date 2018/3/19
 */
@Service
public class TaskCallbackMsgMonitorImpl implements TaskCallbackMsgMonitor{
    private static final Logger logger = LoggerFactory.getLogger(TaskCallbackMsgMonitorImpl.class);

    @Autowired
    private TaskCallbackMsgMonitorPlugin taskCallbackMsgMonitorPlugin;

    @Override
    public void sendMessage(@Nonnull TaskInfo task, @Nullable CallbackRecordObject callbackRecord) {
        TaskCallbackMsgMonitorMessage message = new TaskCallbackMsgMonitorMessage();
        message.setTaskId(task.getId());
        message.setAccountNo(task.getAccountNo());
        message.setAppId(task.getAppId());
        message.setBizType(task.getBizType());
        message.setCompleteTime(task.getLastUpdateTime());
        message.setWebSite(task.getWebSite());
        message.setUniqueId(task.getUniqueId());
        message.setSaasEnv(String.valueOf(task.getSaasEnv()));
        // 有回调
        if (callbackRecord != null) {
            Map<String, Object> attributeMap = Maps.newHashMap();
            final int responseStatusCode = callbackRecord.getResponseStatusCode();
            attributeMap.put("callbackHttpCode", responseStatusCode);
            if (responseStatusCode == HttpStatus.SC_OK) {
                attributeMap.put("callbackCode", 0);
                attributeMap.put("callbackMsg", "回调成功");
            } else if (responseStatusCode == 0) {
                final Throwable exception = callbackRecord.getException();
                attributeMap.put("callbackMsg", exception == null ? "回调执行错误" : "回调执行失败：" + exception.getMessage());
            } else {
                final CallbackResult callbackResult = callbackRecord.getCallbackResult();
                if (callbackResult != null) {
                    String callbackCode = callbackResult.getCode();
                    String callbackMsg = callbackResult.getErrorMsg();
                    if (StringUtils.isBlank(callbackMsg)) {
                        callbackMsg = "回调错误信息为空";
                    }

                    attributeMap.put("callbackCode", callbackCode);
                    attributeMap.put("callbackMsg", callbackMsg);
                } else {
                    final String responseData = callbackRecord.getResponseData();
                    attributeMap.put("callbackMsg", responseData.length() > 1000 ? responseData.substring(0, 1000) + "..." : responseData);
                }
            }
            message.setAttributes(attributeMap);
        }

        taskCallbackMsgMonitorPlugin.sendMessage(message);
        logger.info("send task callbackMsg message to saas-monitor, message={}", JSON.toJSONString(message));
    }
}

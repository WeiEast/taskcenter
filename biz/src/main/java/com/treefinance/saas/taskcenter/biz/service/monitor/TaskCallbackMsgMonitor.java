package com.treefinance.saas.taskcenter.biz.service.monitor;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.treefinance.saas.assistant.model.TaskCallbackMsgMonitorMessage;
import com.treefinance.saas.assistant.plugin.TaskCallbackMsgMonitorPlugin;
import com.treefinance.saas.taskcenter.biz.service.TaskService;
import com.treefinance.saas.taskcenter.service.domain.TaskInfo;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Buddha Bless , No Bug !
 *
 * @author haojiahong
 * @date 2018/3/19
 */
@Service
public class TaskCallbackMsgMonitor {
    private static final Logger logger = LoggerFactory.getLogger(TaskCallbackMsgMonitor.class);

    @Autowired
    private TaskService taskService;
    @Autowired
    private TaskCallbackMsgMonitorPlugin taskCallbackMsgMonitorPlugin;

    /**
     * 发送消息
     *
     * @param taskId
     * @param httpCode
     * @param result
     * @param isCallback
     */
    public void sendMessage(Long taskId, Integer httpCode, String result, Boolean isCallback) {
        TaskInfo task = taskService.getTaskInfoById(taskId);
        if (task == null) {
            return;
        }
        TaskCallbackMsgMonitorMessage message = new TaskCallbackMsgMonitorMessage();
        message.setTaskId(taskId);
        message.setAccountNo(task.getAccountNo());
        message.setAppId(task.getAppId());
        message.setBizType(task.getBizType());
        message.setCompleteTime(task.getLastUpdateTime());
        message.setWebSite(task.getWebSite());
        message.setUniqueId(task.getUniqueId());
        message.setSaasEnv(String.valueOf(task.getSaasEnv()));
        // 有回调
        if (isCallback) {
            Map<String, Object> attributeMap = Maps.newHashMap();
            attributeMap.put("callbackHttpCode", httpCode);
            if (httpCode == 200) {
                attributeMap.put("callbackCode", 200);
                attributeMap.put("callbackMsg", "回调成功");
            } else {
                try {
                    JSONObject jsonObject = JSON.parseObject(result);
                    String errorMsg = jsonObject.getString("errorMsg");
                    String errorCode = jsonObject.getString("code");
                    attributeMap.put("callbackCode", errorCode);
                    if (StringUtils.isNotBlank(errorMsg)) {
                        attributeMap.put("callbackMsg", errorMsg);
                    } else {
                        attributeMap.put("callbackMsg", "回调错误信息为空");
                    }
                } catch (Exception e) {
                    logger.error("记录回调错误信息:解析返回回调结果json有误,taskId={},回调返回结果result={}", taskId, result);
                    attributeMap.put("callbackMsg", result.length() > 1000 ? result.substring(0, 100) + "..." : result);
                }
            }
            message.setAttributes(attributeMap);
        }

        taskCallbackMsgMonitorPlugin.sendMessage(message);
        logger.info("send task callbackMsg message to saas-monitor, message={}", JSON.toJSONString(message));
    }
}

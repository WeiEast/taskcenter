package com.treefinance.saas.taskcenter.biz.service.monitor;

import com.alibaba.fastjson.JSON;
import com.treefinance.saas.assistant.model.TaskMonitorMessage;
import com.treefinance.saas.assistant.plugin.TaskMonitorPlugin;
import com.treefinance.saas.taskcenter.dto.TaskDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by yh-treefinance on 2017/6/20.
 */
@Service
public class MonitorPluginService {
    private static final Logger logger = LoggerFactory.getLogger(MonitorPluginService.class);

    @Autowired
    private TaskMonitorPlugin taskMonitorPlugin;


    /**
     * 发送任务监控消息
     *
     * @param taskDTO
     */
    public void sendTaskMonitorMessage(TaskDTO taskDTO) {
        TaskMonitorMessage message = new TaskMonitorMessage();
        try {
            message.setTaskId(taskDTO.getId());
            message.setAccountNo(taskDTO.getAccountNo());
            message.setAppId(taskDTO.getAppId());
            message.setBizType(taskDTO.getBizType());
            message.setCompleteTime(taskDTO.getLastUpdateTime());
            message.setStatus(taskDTO.getStatus());
            message.setWebSite(taskDTO.getWebSite());
            message.setUniqueId(taskDTO.getUniqueId());
            message.setStepCode(taskDTO.getStepCode());
            message.setSaasEnv(String.valueOf(taskDTO.getSaasEnv()));
            taskMonitorPlugin.sendMessage(message);
            logger.info("send message to monitor : message={}", JSON.toJSONString(taskDTO));

        } catch (Exception e) {
            logger.error(" send message to monitor failed : body=" + JSON.toJSONString(message), e);
        }
    }

}

package com.treefinance.saas.taskcenter.biz.service.monitor;

import com.treefinance.saas.assistant.model.TaskMonitorMessage;
import com.treefinance.saas.assistant.plugin.TaskMonitorPlugin;
import com.treefinance.saas.taskcenter.common.enums.EBizType;
import com.treefinance.saas.taskcenter.service.domain.TaskInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

import javax.annotation.Nonnull;

/**
 * Created by yh-treefinance on 2017/6/20.
 */
@Service
@Order(Ordered.HIGHEST_PRECEDENCE)
public class TaskMonitor extends AbstractBusinessMonitor<TaskMonitorMessage> {

    @Autowired
    private TaskMonitorPlugin taskMonitorPlugin;

    @Override
    public boolean support(EBizType bizType) {
        return true;
    }

    @Override
    protected TaskMonitorMessage buildMonitorMessage(@Nonnull TaskInfo task) {
        TaskMonitorMessage message = new TaskMonitorMessage();
        message.setTaskId(task.getId());
        message.setAccountNo(task.getAccountNo());
        message.setAppId(task.getAppId());
        message.setBizType(task.getBizType());
        message.setCompleteTime(task.getLastUpdateTime());
        message.setStatus(task.getStatus());
        message.setWebSite(task.getWebSite());
        message.setUniqueId(task.getUniqueId());
        message.setStepCode(task.getStepCode());
        message.setSaasEnv(String.valueOf(task.getSaasEnv()));
        return message;
    }

    @Override
    protected void doSending(@Nonnull TaskMonitorMessage message) {
        taskMonitorPlugin.sendMessage(message);
    }

}

package com.treefinance.saas.taskcenter.biz.service.directive.process.impl;

import com.google.common.collect.Maps;
import com.treefinance.saas.taskcenter.biz.service.MonitorService;
import com.treefinance.saas.taskcenter.biz.service.directive.process.AbstractDirectiveProcessor;
import com.treefinance.saas.taskcenter.common.enums.EDirective;
import com.treefinance.saas.taskcenter.common.enums.ETaskStatus;
import com.treefinance.saas.taskcenter.dto.DirectiveDTO;
import com.treefinance.saas.taskcenter.interation.manager.SpiderTaskManager;
import com.treefinance.saas.taskcenter.service.domain.AttributedTaskInfo;
import com.treefinance.saas.taskcenter.share.AsyncExecutor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 取消任务执行 Created by yh-treefinance on 2017/7/10.
 */
@Component
public class CancelDirectiveProcessor extends AbstractDirectiveProcessor {
    @Autowired
    protected MonitorService monitorService;
    @Autowired
    private AsyncExecutor asyncExecutor;
    @Autowired
    private SpiderTaskManager spiderTaskManager;

    @Override
    protected void doProcess(EDirective directive, DirectiveDTO directiveDTO) {
        AttributedTaskInfo task = directiveDTO.getTask();
        task.setStatus(ETaskStatus.CANCEL.getStatus());
        // 取消任务
        taskService.updateStatusIfDone(task.getId(), ETaskStatus.CANCEL.getStatus());
        Map<String, String> extMap = Maps.newHashMap();
        extMap.put("reason", "user");
        spiderTaskManager.cancelQuietly(task.getId(), extMap);

        monitorService.sendMonitorMessage(task.getId());

        // 异步触发触发回调
        asyncExecutor.runAsync(directiveDTO, this::callback);
    }

}

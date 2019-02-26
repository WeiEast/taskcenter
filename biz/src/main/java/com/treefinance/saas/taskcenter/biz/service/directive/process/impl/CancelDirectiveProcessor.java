package com.treefinance.saas.taskcenter.biz.service.directive.process.impl;

import com.datatrees.spider.share.api.SpiderTaskApi;
import com.google.common.collect.Maps;
import com.treefinance.saas.taskcenter.share.AsyncExecutor;
import com.treefinance.saas.taskcenter.biz.service.directive.process.AbstractDirectiveProcessor;
import com.treefinance.saas.taskcenter.biz.service.monitor.MonitorService;
import com.treefinance.saas.taskcenter.context.enums.EDirective;
import com.treefinance.saas.taskcenter.context.enums.ETaskStatus;
import com.treefinance.saas.taskcenter.dto.DirectiveDTO;
import com.treefinance.saas.taskcenter.dto.TaskDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 取消任务执行
 * Created by yh-treefinance on 2017/7/10.
 */
@Component
public class CancelDirectiveProcessor extends AbstractDirectiveProcessor {
    @Autowired
    protected MonitorService monitorService;
    @Autowired
    private AsyncExecutor asyncExecutor;
    @Autowired
    private SpiderTaskApi spiderTaskApi;

    @Override
    protected void doProcess(EDirective directive, DirectiveDTO directiveDTO) {
        TaskDTO taskDTO = directiveDTO.getTask();
        taskDTO.setStatus(ETaskStatus.CANCEL.getStatus());
        // 取消任务
        taskService.updateStatusIfDone(taskDTO.getId(), ETaskStatus.CANCEL.getStatus());
        Map<String, String> extMap = Maps.newHashMap();
        extMap.put("reason", "user");
        spiderTaskApi.cancel(taskDTO.getId(), extMap);
        monitorService.sendMonitorMessage(taskDTO.getId());

        // 异步触发触发回调
        asyncExecutor.runAsync(directiveDTO, this::callback);
    }


}

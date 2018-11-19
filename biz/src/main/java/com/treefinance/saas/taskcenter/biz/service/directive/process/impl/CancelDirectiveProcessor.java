package com.treefinance.saas.taskcenter.biz.service.directive.process.impl;

import com.datatrees.spider.share.api.SpiderTaskApi;
import com.google.common.collect.Maps;
import com.treefinance.saas.taskcenter.biz.service.common.AsyncExecutor;
import com.treefinance.saas.taskcenter.biz.service.directive.process.AbstractDirectiveProcessor;
import com.treefinance.saas.taskcenter.biz.service.monitor.MonitorService;
import com.treefinance.saas.taskcenter.common.enums.EDirective;
import com.treefinance.saas.taskcenter.common.enums.ETaskStatus;
import com.treefinance.saas.taskcenter.common.model.dto.DirectiveDTO;
import com.treefinance.saas.taskcenter.common.model.dto.TaskDTO;
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
        taskService.cancelTaskWithStep(taskDTO.getId());
        Map<String, String> extMap = Maps.newHashMap();
        extMap.put("reason", "user");
        spiderTaskApi.cancel(taskDTO.getId(), extMap);
        monitorService.sendMonitorMessage(taskDTO.getId());

        // 异步触发触发回调
        asyncExecutor.runAsync(directiveDTO, _directiveDTO -> callback(_directiveDTO));
    }


}

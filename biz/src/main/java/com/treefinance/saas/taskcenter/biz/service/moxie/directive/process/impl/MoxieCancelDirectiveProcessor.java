package com.treefinance.saas.taskcenter.biz.service.moxie.directive.process.impl;

import com.treefinance.saas.taskcenter.biz.service.moxie.directive.process.MoxieAbstractDirectiveProcessor;
import com.treefinance.saas.taskcenter.common.enums.ETaskStatus;
import com.treefinance.saas.taskcenter.context.enums.moxie.EMoxieDirective;
import com.treefinance.saas.taskcenter.dto.moxie.MoxieDirectiveDTO;
import com.treefinance.saas.taskcenter.service.domain.AttributedTaskInfo;
import org.springframework.stereotype.Component;

/**
 * 取消任务执行 Created by yh-treefinance on 2017/7/10.
 */
@Component
public class MoxieCancelDirectiveProcessor extends MoxieAbstractDirectiveProcessor {

    @Override
    protected void doProcess(EMoxieDirective directive, MoxieDirectiveDTO directiveDTO) {
        AttributedTaskInfo task = directiveDTO.getTask();
        task.setStatus(ETaskStatus.CANCEL.getStatus());
        // 取消任务
        taskService.updateStatusIfDone(task.getId(), ETaskStatus.CANCEL.getStatus());
    }

}

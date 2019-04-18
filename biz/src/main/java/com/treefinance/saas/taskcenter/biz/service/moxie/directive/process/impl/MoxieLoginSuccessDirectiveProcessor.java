package com.treefinance.saas.taskcenter.biz.service.moxie.directive.process.impl;

import com.treefinance.saas.taskcenter.biz.service.moxie.directive.process.MoxieAbstractDirectiveProcessor;
import com.treefinance.saas.taskcenter.context.enums.ETaskStep;
import com.treefinance.saas.taskcenter.context.enums.moxie.EMoxieDirective;
import com.treefinance.saas.taskcenter.dto.moxie.MoxieDirectiveDTO;
import com.treefinance.saas.taskcenter.service.domain.AttributedTaskInfo;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * Created by haojiahong on 2017/9/15.
 */
@Component
public class MoxieLoginSuccessDirectiveProcessor extends MoxieAbstractDirectiveProcessor {

    @Override
    protected void doProcess(EMoxieDirective directive, MoxieDirectiveDTO directiveDTO) {
        AttributedTaskInfo task = directiveDTO.getTask();
        // 1.记录登录日志
        taskLogService.insertTaskLog(task.getId(), ETaskStep.LOGIN_SUCCESS.getText(), new Date(), null);

    }
}

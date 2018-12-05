package com.treefinance.saas.taskcenter.biz.service.moxie.directive.process.impl;

import com.treefinance.saas.taskcenter.biz.service.moxie.directive.process.MoxieAbstractDirectiveProcessor;
import com.treefinance.saas.taskcenter.common.enums.ETaskStep;
import com.treefinance.saas.taskcenter.common.enums.moxie.EMoxieDirective;
import com.treefinance.saas.taskcenter.common.model.dto.TaskDTO;
import com.treefinance.saas.taskcenter.common.model.moxie.MoxieDirectiveDTO;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * Created by haojiahong on 2017/9/15.
 */
@Component
public class MoxieLoginSuccessDirectiveProcessor extends MoxieAbstractDirectiveProcessor {

    @Override
    protected void doProcess(EMoxieDirective directive, MoxieDirectiveDTO directiveDTO) {
        TaskDTO taskDTO = directiveDTO.getTask();
        // 1.记录登录日志
        taskLogService.insertTaskLog(taskDTO.getId(), ETaskStep.LOGIN_SUCCESS.getText(), new Date(), null);

    }
}

package com.treefinance.saas.taskcenter.biz.service.moxie.directive.process.impl;

import com.google.common.collect.Maps;
import com.treefinance.saas.taskcenter.biz.service.moxie.directive.process.MoxieAbstractDirectiveProcessor;
import com.treefinance.saas.taskcenter.common.enums.ETaskAttribute;
import com.treefinance.saas.taskcenter.common.enums.ETaskStep;
import com.treefinance.saas.taskcenter.common.enums.moxie.EMoxieDirective;
import com.treefinance.saas.taskcenter.common.model.dto.TaskDTO;
import com.treefinance.saas.taskcenter.common.model.moxie.MoxieDirectiveDTO;
import com.treefinance.saas.taskcenter.common.util.JsonUtils;
import com.treefinance.saas.taskcenter.dao.entity.TaskAttribute;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Map;

/**
 * Created by haojiahong on 2017/9/15.
 */
@Component
public class MoxieLoginFailDirectiveProcessor extends MoxieAbstractDirectiveProcessor {

    @Override
    @Transactional
    protected void doProcess(EMoxieDirective directive, MoxieDirectiveDTO directiveDTO) {
        TaskDTO taskDTO = directiveDTO.getTask();
        TaskAttribute taskAttribute = taskAttributeService.findByName(taskDTO.getId(), ETaskAttribute.FUND_MOXIE_TASKID.getAttribute(), false);
        String moxieTaskId = "";
        if (taskAttribute != null) {
            moxieTaskId = taskAttribute.getValue();
        }
        Map<String, Object> map = Maps.newHashMap();
        map.put("error", directiveDTO.getRemark());
        map.put("moxieTaskId", moxieTaskId);

        // 1.记录登录日志
        taskLogService.insertTaskLog(taskDTO.getId(), ETaskStep.LOGIN_FAIL.getText(), new Date(), JsonUtils.toJsonString(map));


    }
}

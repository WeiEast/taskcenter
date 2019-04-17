package com.treefinance.saas.taskcenter.biz.service.moxie.directive.process.impl;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Maps;
import com.treefinance.saas.taskcenter.biz.service.moxie.directive.process.MoxieAbstractDirectiveProcessor;
import com.treefinance.saas.taskcenter.context.enums.ETaskAttribute;
import com.treefinance.saas.taskcenter.context.enums.ETaskStep;
import com.treefinance.saas.taskcenter.context.enums.moxie.EMoxieDirective;
import com.treefinance.saas.taskcenter.dao.entity.TaskAttribute;
import com.treefinance.saas.taskcenter.dto.TaskDTO;
import com.treefinance.saas.taskcenter.dto.moxie.MoxieDirectiveDTO;
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
        TaskAttribute taskAttribute = taskAttributeService.queryAttributeByTaskIdAndName(taskDTO.getId(), ETaskAttribute.FUND_MOXIE_TASKID.getAttribute(), false);
        String moxieTaskId = "";
        if (taskAttribute != null) {
            moxieTaskId = taskAttribute.getValue();
        }
        Map<String, Object> map = Maps.newHashMap();
        map.put("error", directiveDTO.getRemark());
        map.put("moxieTaskId", moxieTaskId);

        // 1.记录登录日志
        taskLogService.insertTaskLog(taskDTO.getId(), ETaskStep.LOGIN_FAIL.getText(), new Date(), JSON.toJSONString(map));

    }
}

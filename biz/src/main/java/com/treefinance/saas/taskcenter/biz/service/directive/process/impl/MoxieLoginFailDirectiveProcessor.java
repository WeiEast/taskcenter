/*
 * Copyright © 2015 - 2017 杭州大树网络技术有限公司. All Rights Reserved
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.treefinance.saas.taskcenter.biz.service.directive.process.impl;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Maps;
import com.treefinance.saas.taskcenter.biz.service.directive.process.AbstractCallbackDirectiveProcessor;
import com.treefinance.saas.taskcenter.biz.service.directive.process.DirectiveContext;
import com.treefinance.saas.taskcenter.biz.service.directive.process.MoxieDirectiveProcessor;
import com.treefinance.saas.taskcenter.common.enums.EDirective;
import com.treefinance.saas.taskcenter.common.enums.ETaskAttribute;
import com.treefinance.saas.taskcenter.common.enums.ETaskStep;
import com.treefinance.saas.taskcenter.dao.entity.TaskAttribute;
import com.treefinance.saas.taskcenter.service.domain.AttributedTaskInfo;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Map;

/**
 * Created by haojiahong on 2017/9/15.
 */
@Component
public class MoxieLoginFailDirectiveProcessor extends AbstractCallbackDirectiveProcessor implements MoxieDirectiveProcessor {

    @Override
    public EDirective getSpecifiedDirective() {
        return EDirective.LOGIN_FAIL;
    }

    @Override
    protected void doProcess(DirectiveContext context) {
        AttributedTaskInfo task = context.getTask();
        TaskAttribute taskAttribute = taskAttributeService.queryAttributeByTaskIdAndName(task.getId(), ETaskAttribute.FUND_MOXIE_TASKID.getAttribute(), false);
        String moxieTaskId = "";
        if (taskAttribute != null) {
            moxieTaskId = taskAttribute.getValue();
        }
        Map<String, Object> map = Maps.newHashMap();
        map.put("error", context.getRemark());
        map.put("moxieTaskId", moxieTaskId);

        // 1.记录登录日志
        taskLogService.insertTaskLog(task.getId(), ETaskStep.LOGIN_FAIL.getText(), new Date(), JSON.toJSONString(map));
    }
}

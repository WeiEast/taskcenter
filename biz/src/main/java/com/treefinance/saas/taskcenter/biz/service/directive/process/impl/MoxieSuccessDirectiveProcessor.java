/*
 * Copyright © 2015 - 2017 杭州大树网络技术有限公司. All Rights Reserved
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package com.treefinance.saas.taskcenter.biz.service.directive.process.impl;

import com.treefinance.saas.taskcenter.biz.service.directive.process.AbstractCallbackDirectiveProcessor;
import com.treefinance.saas.taskcenter.biz.service.directive.process.DirectiveContext;
import com.treefinance.saas.taskcenter.biz.service.directive.process.MoxieDirectiveProcessor;
import com.treefinance.saas.taskcenter.common.enums.EDirective;
import com.treefinance.saas.taskcenter.common.enums.ETaskStatus;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 成功指令处理
 * 
 * @author yh-treefinance
 * @date 2017/7/6.
 */
@Component
public class MoxieSuccessDirectiveProcessor extends AbstractCallbackDirectiveProcessor implements MoxieDirectiveProcessor {

    @Override
    public EDirective getSpecifiedDirective() {
        return EDirective.TASK_SUCCESS;
    }

    @Override
    protected void doProcess(DirectiveContext context) {
        Long taskId = context.getTaskId();

        // 生成数据map
        Map<String, Object> dataMap = generateDataMap(context);
        // 回调之前预处理
        precallback(dataMap, context);
        // 触发回调: 0-无需回调，1-回调成功，-1-回调失败
        int result = callback(dataMap, context);

        if (result == 0 || result == 1) {
            context.updateTaskStatus(ETaskStatus.SUCCESS);
        } else {
            // 指令发生变更 ： task_success -> callback_fail
            taskNextDirectiveService.insert(taskId, context.getDirectiveString());

            context.updateTaskStatus(ETaskStatus.FAIL);

            context.updateDirective(EDirective.CALLBACK_FAIL);
        }
        // 更新任务状态,记录任务成功日志
        String stepCode = taskService.updateStatusIfDone(taskId, context.getTaskStatus());
        context.updateStepCode(stepCode);

    }

}

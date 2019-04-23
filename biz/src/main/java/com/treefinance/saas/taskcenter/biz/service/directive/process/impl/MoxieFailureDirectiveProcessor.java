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

import com.treefinance.saas.taskcenter.biz.service.directive.process.AbstractCallbackDirectiveProcessor;
import com.treefinance.saas.taskcenter.biz.service.directive.process.DirectiveContext;
import com.treefinance.saas.taskcenter.biz.service.directive.process.MoxieDirectiveProcessor;
import com.treefinance.saas.taskcenter.common.enums.EDirective;
import com.treefinance.saas.taskcenter.common.enums.ETaskStatus;
import com.treefinance.saas.taskcenter.share.AsyncExecutor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author haojiahong
 * @date 2017/9/15.
 */
@Component
public class MoxieFailureDirectiveProcessor extends AbstractCallbackDirectiveProcessor implements MoxieDirectiveProcessor {
    @Autowired
    private AsyncExecutor asyncExecutor;

    @Override
    public EDirective getSpecifiedDirective() {
        return EDirective.TASK_FAIL;
    }

    @Override
    protected void doProcess(DirectiveContext context) {
        // 任务置为失败
        context.updateTaskStatus(ETaskStatus.FAIL);

        // 更新任务状态,记录失败任务日志
        final Long taskId = context.getTaskId();
        String stepCode = taskService.updateStatusIfDone(taskId, ETaskStatus.FAIL.getStatus());
        context.updateStepCode(stepCode);

        // 成数据map,包装数据:任务失败后返回失败信息加密后通过指令传递给前端
        Map<String, Object> dataMap = generateDataMap(context);
        // 回调之前预处理
        precallback(dataMap, context);
        // 异步触发触发回调
        asyncExecutor.runAsync(context, dto -> callback(dataMap, dto));

    }
}

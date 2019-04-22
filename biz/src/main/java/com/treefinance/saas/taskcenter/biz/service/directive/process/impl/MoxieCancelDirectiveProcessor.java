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
import org.springframework.stereotype.Component;

/**
 * 取消任务执行 Created by yh-treefinance on 2017/7/10.
 */
@Component
public class MoxieCancelDirectiveProcessor extends AbstractCallbackDirectiveProcessor implements MoxieDirectiveProcessor {

    @Override
    public EDirective getSpecifiedDirective() {
        return EDirective.TASK_CANCEL;
    }

    @Override
    protected void doProcess(DirectiveContext context) {
        context.updateTaskStatus(ETaskStatus.CANCEL);

        // 取消任务
        final Long taskId = context.getTaskId();
        taskService.updateStatusIfDone(taskId, ETaskStatus.CANCEL.getStatus());
    }

}

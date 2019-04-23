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

package com.treefinance.saas.taskcenter.biz.service.directive.process.interceptor;

import com.treefinance.saas.taskcenter.biz.service.MonitorService;
import com.treefinance.saas.taskcenter.biz.service.TaskNextDirectiveService;
import com.treefinance.saas.taskcenter.biz.service.directive.DirectiveHelper;
import com.treefinance.saas.taskcenter.biz.service.directive.process.DirectiveContext;
import com.treefinance.saas.taskcenter.service.AccountNoService;
import com.treefinance.saas.taskcenter.service.domain.DirectiveEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;

/**
 * @author Jerry
 * @date 2019-03-05 17:17
 */
@Component
@Order(1)
public class DefaultProcessorInterceptor implements ProcessorInterceptor {
    @Autowired
    private TaskNextDirectiveService taskDirectiveService;
    @Autowired
    private AccountNoService accountNoService;
    @Autowired
    private MonitorService monitorService;

    @Override
    public void afterCompletion(@Nonnull DirectiveContext context) {
        if (!context.isFromMoxie()) {
            // 非魔蝎导入任务，保存导入账号
            accountNoService.saveAccountNoIfAbsent(context.getTaskId());
        }

        // 保存最新的指令信息
        DirectiveEntity directiveEntity = DirectiveHelper.buildDirectiveEntity(context);
        taskDirectiveService.saveDirective(directiveEntity);

        // 发送监控消息
        monitorService.sendMonitorMessage(context.getTaskId());
    }

}

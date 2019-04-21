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

import com.treefinance.saas.taskcenter.biz.service.MonitorService;
import com.treefinance.saas.taskcenter.biz.service.directive.process.AbstractCallbackDirectiveProcessor;
import com.treefinance.saas.taskcenter.biz.service.directive.process.DirectiveContext;
import com.treefinance.saas.taskcenter.biz.service.directive.process.MoxieDirectiveProcessor;
import com.treefinance.saas.taskcenter.common.enums.EDirective;
import com.treefinance.saas.taskcenter.common.enums.ETaskStatus;
import com.treefinance.saas.taskcenter.interation.manager.domain.AppLicense;
import com.treefinance.saas.taskcenter.service.domain.AttributedTaskInfo;
import com.treefinance.saas.taskcenter.share.AsyncExecutor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 *
 */
@Component
public class MoxieFailureDirectiveProcessor extends AbstractCallbackDirectiveProcessor implements MoxieDirectiveProcessor {
    @Autowired
    private AsyncExecutor asyncExecutor;
    @Autowired
    private MonitorService monitorService;

    @Override
    public EDirective getSpecifiedDirective() {
        return EDirective.TASK_FAIL;
    }

    @Override
    protected void doProcess(DirectiveContext context) {
        AttributedTaskInfo task = context.getTask();
        String appId = task.getAppId();

        // 任务置为失败
        task.setStatus(ETaskStatus.FAIL.getStatus());

        // 更新任务状态,记录失败任务日志
        String stepCode = taskService.updateStatusIfDone(task.getId(), ETaskStatus.FAIL.getStatus());
        task.setStepCode(stepCode);

        // 发送监控消息
        monitorService.sendMonitorMessage(task.getId());

        // 获取商户秘钥,包装数据:任务失败后返回失败信息加密后通过指令传递给前端
        AppLicense appLicense = licenseManager.getAppLicenseByAppId(appId);
        // 成数据map
        Map<String, Object> dataMap = generateDataMap(context);
        // 回调之前预处理
        precallback(dataMap, appLicense, context);
        // 异步触发触发回调
        asyncExecutor.runAsync(context, dto -> callback(dataMap, appLicense, dto));

    }
}

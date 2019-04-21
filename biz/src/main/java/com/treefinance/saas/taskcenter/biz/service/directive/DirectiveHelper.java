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

package com.treefinance.saas.taskcenter.biz.service.directive;

import com.treefinance.saas.taskcenter.biz.service.directive.process.DirectiveContext;
import com.treefinance.saas.taskcenter.common.enums.EDirective;
import com.treefinance.saas.taskcenter.service.domain.DirectiveEntity;

/**
 * @author Jerry
 * @date 2019-03-06 19:36
 */
public final class DirectiveHelper {

    private DirectiveHelper() {}

    /**
     * 创建{@link DirectiveEntity}
     * 
     * @param context {@link DirectivePacket}
     * @return {@link DirectiveEntity}
     */
    public static DirectiveEntity buildDirectiveEntity(DirectiveContext context) {
        DirectiveEntity directiveEntity = new DirectiveEntity();
        directiveEntity.setTaskId(context.getTaskId());
        directiveEntity.setDirective(context.getDirectiveString());
        directiveEntity.setDirectiveId(context.getDirectiveId());
        directiveEntity.setRemark(context.getRemark());

        return directiveEntity;
    }

    /**
     * 创建{@link DirectiveEntity}
     *
     * @param taskId 任务ID
     * @param directive 指令
     * @return {@link DirectiveEntity}
     */
    public static DirectiveEntity buildDirectiveEntity(Long taskId, EDirective directive) {
        DirectiveEntity directiveEntity = new DirectiveEntity();
        directiveEntity.setTaskId(taskId);
        directiveEntity.setDirective(directive.getText());

        return directiveEntity;
    }
}

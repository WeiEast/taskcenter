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

package com.treefinance.saas.taskcenter.biz.service.monitor;

import com.alibaba.fastjson.JSON;
import com.google.common.base.Stopwatch;
import com.treefinance.saas.assistant.model.base.MonitorMessage;
import com.treefinance.saas.taskcenter.service.domain.TaskInfo;
import com.treefinance.saas.taskcenter.service.impl.AbstractService;
import com.treefinance.toolkit.util.Preconditions;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nonnull;

/**
 * @author Jerry
 * @date 2019-03-06 12:35
 */
abstract class AbstractBusinessMonitor<T extends MonitorMessage> extends AbstractService implements BusinessMonitor {

    @Override
    public final void sendMessage(@Nonnull TaskInfo task) {
        logger.info("sendMonitorMessage: task={}", task);

        Stopwatch stopwatch = Stopwatch.createStarted();

        T message = null;
        try {
            // 创建监控消息
            message = buildMonitorMessage(task);
            Preconditions.notNull("message", message);
            // 发送监控消息
            doSending(message);
        } catch (Exception e) {
            logger.warn("发送业务监控消息异常！", e);
        } finally {
            logger.info("send business monitor-message to saas-monitor! - cost: {}, message: {}", stopwatch, message != null ? JSON.toJSONString(message) : StringUtils.EMPTY);
        }
    }

    /**
     * 生成监控消息
     * 
     * @param task 任务
     * @return 监控消息
     */
    protected abstract T buildMonitorMessage(@Nonnull TaskInfo task);

    /**
     * 发生监控消息
     * 
     * @param message 监控消息
     */
    protected abstract void doSending(@Nonnull T message);
}

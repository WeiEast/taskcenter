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

package com.treefinance.saas.taskcenter.biz.service.impl;

import com.treefinance.saas.taskcenter.biz.service.MonitorService;
import com.treefinance.saas.taskcenter.biz.service.TaskService;
import com.treefinance.saas.taskcenter.biz.service.monitor.BusinessMonitor;
import com.treefinance.saas.taskcenter.biz.service.monitor.BusinessMonitorManager;
import com.treefinance.saas.taskcenter.biz.service.monitor.TaskCallbackMsgMonitor;
import com.treefinance.saas.taskcenter.common.enums.EBizType;
import com.treefinance.saas.taskcenter.common.enums.ETaskStatus;
import com.treefinance.saas.taskcenter.service.domain.TaskInfo;
import com.treefinance.saas.taskcenter.service.param.CallbackRecordObject;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionSynchronizationAdapter;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.List;

/**
 * @author yh-treefinance
 * @date 2017/6/20.
 */
@Service
public class MonitorServiceImpl implements MonitorService {
    private static final Logger logger = LoggerFactory.getLogger(MonitorServiceImpl.class);

    @Autowired
    private TaskCallbackMsgMonitor taskCallbackMsgMonitor;
    @Autowired
    private ThreadPoolTaskExecutor threadPoolExecutor;
    @Autowired
    private TaskService taskService;
    @Autowired
    private BusinessMonitorManager businessMonitorManager;

    @Override
    public void sendMonitorMessage(Long taskId) {
        // 事务提交之后，发送消息(如果事务回滚,任务可能未更新为完成状态,不需要发送监控消息)
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronizationAdapter() {
            @Override
            public void afterCommit() {
                logger.info("TransactionSynchronizationManager: afterCommit taskId={}", taskId);
                threadPoolExecutor.execute(new MonitorMessageSendThread(taskId));
            }
        });
    }

    @Override
    public void sendTaskCallbackMsgMonitorMessage(TaskInfo task, CallbackRecordObject callbackRecord) {
        try {
            taskCallbackMsgMonitor.sendMessage(task, callbackRecord);
            logger.info("sent TaskCallbackMsgMonitorMessage >> taskId={}, callbackRecord={}", task.getId(), callbackRecord);
        } catch (Exception e) {
            logger.error("Error sending TaskCallbackMsgMonitorMessage >> taskId={}, callbackRecord={}", task.getId(), callbackRecord, e);
        }
    }

    /**
     * Good Luck Bro , No Bug !
     *
     * @author haojiahong
     * @date 2018/5/29
     */
    private class MonitorMessageSendThread implements Runnable {

        private Long taskId;

        MonitorMessageSendThread(Long taskId) {
            this.taskId = taskId;
        }

        @Override
        public void run() {
            TaskInfo task = taskService.getTaskInfoById(taskId);
            Byte status = task.getStatus();
            // 仅成功、失败、取消发送任务
            if (!ETaskStatus.SUCCESS.getStatus().equals(status) && !ETaskStatus.FAIL.getStatus().equals(status) && !ETaskStatus.CANCEL.getStatus().equals(status)) {
                return;
            }

            // 发送任务监控消息
            EBizType bizType = EBizType.of(task.getBizType());
            List<BusinessMonitor> monitors = businessMonitorManager.getMonitors(bizType);
            if (CollectionUtils.isNotEmpty(monitors)) {
                for (BusinessMonitor monitor : monitors) {
                    monitor.sendMessage(task);
                }
            }
        }

    }
}

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

package com.treefinance.saas.taskcenter.biz.schedule.detector;

import com.treefinance.saas.assistant.model.Constants;
import com.treefinance.saas.taskcenter.biz.schedule.handler.TaskTimeoutHandler;
import com.treefinance.saas.taskcenter.biz.service.TaskService;
import com.treefinance.saas.taskcenter.context.enums.ETaskStatus;
import com.treefinance.saas.taskcenter.dao.entity.Task;
import com.treefinance.saas.taskcenter.service.TaskAttributeService;
import com.treefinance.saas.taskcenter.service.TaskLifecycleService;
import com.treefinance.saas.taskcenter.share.cache.redis.RedisKeyUtils;
import com.treefinance.saas.taskcenter.share.cache.redis.RedissonLocks;
import com.treefinance.toolkit.util.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;

import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author Jerry
 * @date 2019-03-12 19:07
 */
@Component
public class TaskTimeoutDetectorImpl implements TaskTimeoutDetector {
    private static final Logger LOGGER = LoggerFactory.getLogger(TaskTimeoutDetectorImpl.class);

    @Autowired
    private TaskService taskService;
    @Autowired
    private TaskAttributeService taskAttributeService;
    @Autowired
    private TaskLifecycleService taskLifecycleService;
    @Autowired
    private List<TaskTimeoutHandler> taskTimeoutHandlers;
    @Autowired
    private RedissonLocks redissonLocks;

    @Override
    public void detect(@Nonnull Long taskId) throws InterruptedException {
        LOGGER.info("任务抓取超时异步处理:taskId={}", taskId);
        String lockKey = RedisKeyUtils.genRedisLockKey("task_timeout_check_job", Constants.SAAS_ENV_VALUE, String.valueOf(taskId));
        redissonLocks.tryLock(lockKey, 10, 180, TimeUnit.SECONDS, () -> {
            Task task = taskService.getTaskById(taskId);
            // 如果任务已结束,则不处理.
            if (!ETaskStatus.RUNNING.getStatus().equals(task.getStatus())) {
                return;
            }

            Date loginTime = taskAttributeService.queryLoginTime(taskId);
            if (loginTime == null) {
                return;
            }

            Integer timeoutInSeconds = taskLifecycleService.getTimeoutInSeconds(taskId);
            if (timeoutInSeconds == null) {
                return;
            }

            Date expireTime = DateUtils.plusSeconds(loginTime, timeoutInSeconds);
            Date now = new Date();
            if (!expireTime.after(now)) {
                LOGGER.info("任务抓取超时: taskId={}, now={}, expireTime={}", taskId, DateUtils.format(now), DateUtils.format(expireTime));

                taskTimeoutHandlers.forEach(handler -> {
                    try {
                        handler.handle(task, timeoutInSeconds, loginTime);
                        LOGGER.info("handle timeout task: handler={},task={},loginTime={},timeout={}", handler.getClass(), taskId, DateUtils.format(loginTime), timeoutInSeconds);
                    } catch (Exception e) {
                        LOGGER.error("handle timeout task error: handler={}, taskId={}, loginTime={}, timeout={}s", handler.getClass(), taskId, DateUtils.format(loginTime),
                            timeoutInSeconds, e);
                    }
                });
            }
        });
    }
}

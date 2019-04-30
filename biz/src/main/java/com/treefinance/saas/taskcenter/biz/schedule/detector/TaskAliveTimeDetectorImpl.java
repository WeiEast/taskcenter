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
import com.treefinance.saas.taskcenter.biz.service.TaskService;
import com.treefinance.saas.taskcenter.context.config.DiamondConfig;
import com.treefinance.saas.taskcenter.service.TaskLifecycleService;
import com.treefinance.saas.taskcenter.share.cache.redis.RedisKeyUtils;
import com.treefinance.saas.taskcenter.share.cache.redis.RedissonLocks;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;

import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * @author Jerry
 * @date 2019-03-12 19:25
 */
@Component
public class TaskAliveTimeDetectorImpl implements TaskAliveTimeDetector {
    private static final Logger LOGGER = LoggerFactory.getLogger(TaskAliveTimeDetectorImpl.class);

    @Autowired
    private TaskService taskService;
    @Autowired
    private TaskLifecycleService taskLifecycleService;
    @Autowired
    private DiamondConfig diamondConfig;
    @Autowired
    private RedissonLocks redissonLocks;

    @Override
    public void detect(@Nonnull Long taskId, @Nonnull Date detectTime) throws InterruptedException {
        LOGGER.info("任务活跃超时异步处理: taskId={}, detectTime={}", taskId, detectTime);

        // 保证取消任务只会执行一次
        String lockKey = RedisKeyUtils.genRedisLockKey("task_alive_time_check_job", Constants.SAAS_ENV_VALUE, String.valueOf(taskId));
        redissonLocks.tryLock(lockKey, 10, 180, TimeUnit.SECONDS, () -> {
            Long lastAliveTimeInMills = taskLifecycleService.queryAliveTimeInMills(taskId);
            if (lastAliveTimeInMills == null) {
                LOGGER.info("任务已经被取消了taskId={}", taskId);
                return;
            }

            long diff = diamondConfig.getTaskMaxAliveTime();
            if (detectTime.getTime() - lastAliveTimeInMills > diff) {
                LOGGER.info("任务活跃时间超时,取消任务,taskId={}", taskId);
                taskService.cancelTask(taskId);
            }
        });
    }
}

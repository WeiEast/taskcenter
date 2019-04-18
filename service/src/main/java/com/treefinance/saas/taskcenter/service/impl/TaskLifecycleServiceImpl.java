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

package com.treefinance.saas.taskcenter.service.impl;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.treefinance.saas.taskcenter.context.enums.ETaskAttribute;
import com.treefinance.saas.taskcenter.context.enums.ETaskStatus;
import com.treefinance.saas.taskcenter.dao.entity.Task;
import com.treefinance.saas.taskcenter.dao.repository.TaskAttributeRepository;
import com.treefinance.saas.taskcenter.dao.repository.TaskRepository;
import com.treefinance.saas.taskcenter.exception.UnexpectedException;
import com.treefinance.saas.taskcenter.interation.manager.BizTypeManager;
import com.treefinance.saas.taskcenter.service.TaskLifecycleService;
import com.treefinance.saas.taskcenter.share.cache.redis.RedisDao;
import com.treefinance.saas.taskcenter.share.cache.redis.RedissonLocks;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Nonnull;

import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * @author haojiahong
 * @date 2018/9/21
 */
@Service
public class TaskLifecycleServiceImpl implements TaskLifecycleService {

    private static final Logger logger = LoggerFactory.getLogger(TaskLifecycleServiceImpl.class);
    private final static String PREFIX_ALIVE_TIME_UPDATE_LOCK_KEY = "task-center:alive-time-update-lock:%s";
    private final static String PREFIX_TASK_ACTIVE_TIME_KEY = "task-center:task-active-time:%s";

    @Autowired
    private TaskRepository taskRepository;
    @Autowired
    private TaskAttributeRepository taskAttributeRepository;
    @Autowired
    private BizTypeManager bizTypeManager;
    @Autowired
    private RedisDao redisDao;
    @Autowired
    private RedissonLocks redissonLocks;

    /**
     * 本地任务缓存
     */
    private final LoadingCache<Long, Task> cache = CacheBuilder.newBuilder().expireAfterWrite(5, TimeUnit.MINUTES).maximumSize(20000).build(new CacheLoader<Long, Task>() {
        @Override
        public Task load(@Nonnull Long taskId) {
            return taskRepository.getTaskById(taskId);
        }
    });

    private String getLockKey(Long taskId) {
        return String.format(PREFIX_ALIVE_TIME_UPDATE_LOCK_KEY, taskId);
    }

    private String getAliveTimeCacheKey(Long taskId) {
        return String.format(PREFIX_TASK_ACTIVE_TIME_KEY, taskId);
    }

    @Override
    public String queryAliveTime(Long taskId) {
        String key = getAliveTimeCacheKey(taskId);
        String lastAliveTimeStr = StringUtils.trim(redisDao.get(key));

        if (StringUtils.isEmpty(lastAliveTimeStr)) {
            try {
                String lockKey = getLockKey(taskId);
                lastAliveTimeStr = redissonLocks.tryLock(lockKey, 5, 60, TimeUnit.SECONDS, isLock -> {
                    Date date = taskAttributeRepository.queryAttributeValueAsDate(taskId, ETaskAttribute.ALIVE_TIME.getAttribute());
                    if (date != null) {
                        if (isLock) {
                            this.cacheAliveTime(taskId, date);
                        }
                        return Long.toString(date.getTime());
                    }
                    return null;
                });
            } catch (InterruptedException e) {
                throw new UnexpectedException("Thread interrupted when querying alive-time! - taskId: " + taskId, e);
            }
        }
        return lastAliveTimeStr;
    }

    @Override
    public Long queryAliveTimeInMills(Long taskId) {
        String key = getAliveTimeCacheKey(taskId);
        String lastAliveTimeStr = StringUtils.trim(redisDao.get(key));

        if (StringUtils.isNotEmpty(lastAliveTimeStr)) {
            return Long.valueOf(lastAliveTimeStr);
        }

        try {
            String lockKey = getLockKey(taskId);
            return redissonLocks.tryLock(lockKey, 5, 60, TimeUnit.SECONDS, isLock -> {
                Date date = taskAttributeRepository.queryAttributeValueAsDate(taskId, ETaskAttribute.ALIVE_TIME.getAttribute());
                if (date != null) {
                    if (isLock) {
                        this.cacheAliveTime(taskId, date);
                    }
                    return date.getTime();
                }
                return null;
            });
        } catch (InterruptedException e) {
            throw new UnexpectedException("Thread interrupted when querying alive-time! - taskId: " + taskId, e);
        }
    }

    @Override
    public void updateAliveTime(Long taskId, Date date) {
        try {
            String lockKey = getLockKey(taskId);
            redissonLocks.tryLock(lockKey, 5, 60, TimeUnit.SECONDS, () -> {
                Task task = taskRepository.getTaskById(taskId);
                if (ETaskStatus.RUNNING.getStatus().equals(task.getStatus())) {
                    taskAttributeRepository.insertOrUpdateAttribute(taskId, ETaskAttribute.ALIVE_TIME.getAttribute(), date);
                    cacheAliveTime(taskId, date);
                } else {
                    logger.info("任务已结束,无需更新任务活跃时间,taskId={}", taskId);
                }
            });
        } catch (InterruptedException e) {
            throw new UnexpectedException("Thread interrupted when updating alive-time! - taskId: " + taskId, e);
        }
    }

    private void cacheAliveTime(Long taskId, Date date) {
        String key = getAliveTimeCacheKey(taskId);
        String value = Long.toString(date.getTime());
        if(redisDao.setEx(key, value, 30, TimeUnit.MINUTES)){
            logger.info("记录任务活跃时间缓存! taskId={}, key={}, value={}", taskId, key, value);
        } else {
            logger.warn("添加任务活跃时间缓存失败！ taskId={}, key={}，value={}", taskId, key, value);
        }
    }

    @Override
    public void deleteAliveTime(Long taskId) {
        try {
            String lockKey = getLockKey(taskId);
            redissonLocks.tryLock(lockKey, 5, 60, TimeUnit.SECONDS, () -> {
                redisDao.deleteKey(getAliveTimeCacheKey(taskId));
                taskAttributeRepository.deleteAttributeByTaskIdAndName(taskId, ETaskAttribute.ALIVE_TIME.getAttribute());
                logger.info("删除记录的任务活跃时间, taskId={}", taskId);
            });
        } catch (InterruptedException e) {
            throw new UnexpectedException("Thread interrupted when deleting alive-time! - taskId: " + taskId, e);
        }
    }

    @Override
    public Integer getTimeoutInSeconds(Long taskId) {
        Task task = cache.getUnchecked(taskId);

        return bizTypeManager.getBizTimeout(task.getBizType());
    }
}

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

package com.treefinance.saas.taskcenter.biz.service.impl;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.treefinance.saas.taskcenter.biz.service.AppBizTypeService;
import com.treefinance.saas.taskcenter.biz.service.TaskAttributeService;
import com.treefinance.saas.taskcenter.biz.service.TaskTimeService;
import com.treefinance.saas.taskcenter.biz.service.task.TaskTimeoutHandler;
import com.treefinance.saas.taskcenter.biz.service.thread.TaskActiveTimeoutThread;
import com.treefinance.saas.taskcenter.biz.service.thread.TaskCrawlerTimeoutThread;
import com.treefinance.saas.taskcenter.context.RedisKeyUtils;
import com.treefinance.saas.taskcenter.context.enums.ETaskAttribute;
import com.treefinance.saas.taskcenter.dao.entity.Task;
import com.treefinance.saas.taskcenter.dao.entity.TaskAttribute;
import com.treefinance.saas.taskcenter.dao.repository.TaskRepository;
import com.treefinance.saas.taskcenter.dto.AppBizType;
import com.treefinance.saas.taskcenter.share.cache.redis.RedisDao;
import com.treefinance.toolkit.util.DateUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * 任务时间Service Created by yh-treefinance on 2017/8/3.
 */
@Service
public class TaskTimeServiceImpl implements TaskTimeService {
    /**
     * logger
     */
    private static final Logger logger = LoggerFactory.getLogger(TaskTimeServiceImpl.class);

    @Autowired
    private TaskRepository taskRepository;
    @Autowired
    private AppBizTypeService appBizTypeService;
    @Autowired
    private List<TaskTimeoutHandler> taskTimeoutHandlers;
    @Autowired
    private RedisDao redisDao;
    @Autowired
    private TaskAttributeService taskAttributeService;
    @Autowired
    private ThreadPoolTaskExecutor threadPoolExecutor;

    /**
     * 本地任务缓存
     */
    private final LoadingCache<Long, Task> cache = CacheBuilder.newBuilder().expireAfterWrite(5, TimeUnit.MINUTES).maximumSize(20000).build(new CacheLoader<Long, Task>() {
        @Override
        public Task load(Long taskId) throws Exception {
            return taskRepository.getTaskById(taskId);
        }
    });

    @Override
    public void updateLoginTime(Long taskId, Date date) {
        if (taskId == null || date == null) {
            return;
        }
        taskAttributeService.insertOrUpdate(taskId, ETaskAttribute.LOGIN_TIME.getAttribute(), date);

        String key = RedisKeyUtils.genTaskLoginTimeKey(taskId);
        redisDao.setEx(key, String.valueOf(date.getTime()), 1, TimeUnit.HOURS);
        logger.info("记录任务登录时间:taskId={},key={},value={}", taskId, key, DateUtils.format(date));
    }

    @Override
    public Date getLoginTime(Long taskId) {
        String key = RedisKeyUtils.genTaskLoginTimeKey(taskId);
        String dateTime = redisDao.get(key);
        if (StringUtils.isNotBlank(dateTime)) {
            return new Date(Long.valueOf(dateTime));
        } else {
            TaskAttribute taskAttribute = taskAttributeService.findByName(taskId, ETaskAttribute.LOGIN_TIME.getAttribute(), false);
            if (taskAttribute == null) {
                logger.info("获取登录时间时,未查询到任务登录时间,任务未登录.taskId={}", taskId);
                return null;
            }
            Date date = DateUtils.parse(taskAttribute.getValue());
            this.updateLoginTime(taskId, date);
            return date;
        }
    }


    @Override
    public Integer getCrawlerTimeoutSeconds(Long taskId) {
        Task task = null;
        try {
            task = cache.get(taskId);
        } catch (ExecutionException e) {
            logger.error("获取设置的任务抓取超时时长时,未查询到任务信息taskId={}", taskId);
        }
        if (task == null) {
            return null;
        }
        AppBizType bizType = appBizTypeService.getAppBizType(task.getBizType());
        if (bizType == null || bizType.getTimeout() == null) {
            logger.error("获取设置的任务抓取超时时长时,未查询到任务相关的bizType信息,taskId={}", taskId);
            return null;
        }
        return bizType.getTimeout();
    }


    @Override
    public void handleTaskTimeout(Long taskId) {
        logger.info("任务抓取超时异步处理:taskId={}", taskId);
        threadPoolExecutor.execute(new TaskCrawlerTimeoutThread(taskId, taskTimeoutHandlers));
    }

    @Override
    public void handleTaskAliveTimeout(Long taskId, Date startTime) {
        logger.info("任务活跃超时异步处理:taskId={},startTime={}", taskId, startTime);
        threadPoolExecutor.execute(new TaskActiveTimeoutThread(taskId, startTime));
    }

}

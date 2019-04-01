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

import com.google.common.collect.Maps;
import com.treefinance.saas.taskcenter.biz.service.TaskAliveService;
import com.treefinance.saas.taskcenter.biz.service.TaskAttributeService;
import com.treefinance.saas.taskcenter.context.enums.ETaskAttribute;
import com.treefinance.saas.taskcenter.context.enums.ETaskStatus;
import com.treefinance.saas.taskcenter.dao.entity.Task;
import com.treefinance.saas.taskcenter.dao.entity.TaskAttribute;
import com.treefinance.saas.taskcenter.dao.repository.TaskRepository;
import com.treefinance.saas.taskcenter.share.cache.redis.RedisDao;
import com.treefinance.toolkit.util.DateUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author haojiahong
 * @date 2018/9/21
 */
@Service
public class TaskAliveServiceImpl implements TaskAliveService {

    private static final Logger logger = LoggerFactory.getLogger(TaskAliveServiceImpl.class);
    private final static String PREFIX_ALIVE_TIME_UPDATE_LOCK_KEY = "task-center:alive-time-update-lock:%s";
    private final static String PREFIX_TASK_ACTIVE_TIME_KEY = "task-center:task-active-time:%s";

    @Autowired
    private TaskRepository taskRepository;
    @Autowired
    private TaskAttributeService taskAttributeService;
    @Autowired
    private RedisDao redisDao;

    @Override
    public void updateTaskActiveTime(Long taskId, Date date) {
        Map<String, Object> lockMap = Maps.newHashMap();
        try {
            lockMap = redisDao.acquireLock(String.format(PREFIX_ALIVE_TIME_UPDATE_LOCK_KEY, taskId), 60 * 1000L);
            if (lockMap != null) {
                Task task = taskRepository.getTaskById(taskId);
                if (!ETaskStatus.RUNNING.getStatus().equals(task.getStatus())) {
                    logger.info("任务已结束,无需更新任务活跃时间,taskId={}", taskId);
                    return;
                }
                taskAttributeService.insertOrUpdate(taskId, ETaskAttribute.ALIVE_TIME.getAttribute(), date);
                String key = String.format(PREFIX_TASK_ACTIVE_TIME_KEY, taskId);
                String value = Long.toString(date.getTime());
                redisDao.setEx(key, value, 30, TimeUnit.MINUTES);
            }
        } finally {
            redisDao.releaseLock(String.format(PREFIX_ALIVE_TIME_UPDATE_LOCK_KEY, taskId), lockMap, 60 * 1000L);
        }
    }

    @Override
    public String getTaskAliveTime(Long taskId) {
        String key = String.format(PREFIX_TASK_ACTIVE_TIME_KEY, taskId);
        String lastActiveTimeStr = redisDao.get(key);
        if (StringUtils.isNotBlank(lastActiveTimeStr)) {
            return lastActiveTimeStr;
        } else {
            TaskAttribute taskAttribute = taskAttributeService.findByName(taskId, ETaskAttribute.ALIVE_TIME.getAttribute(), false);
            if (taskAttribute != null) {
                Date date = DateUtils.parse(taskAttribute.getValue());
                this.updateTaskActiveTime(taskId, date);
                return Long.toString(date.getTime());
            }
            return null;
        }
    }

    @Override
    public void deleteTaskAliveTime(Long taskId) {
        String key = String.format(PREFIX_TASK_ACTIVE_TIME_KEY, taskId);
        redisDao.deleteKey(key);
        taskAttributeService.deleteByTaskIdAndName(taskId, ETaskAttribute.ALIVE_TIME.getAttribute());
        logger.info("删除记录的任务活跃时间, taskId={}", taskId);
    }

}

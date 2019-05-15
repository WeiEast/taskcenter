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

import com.treefinance.saas.taskcenter.common.enums.ETaskAttribute;
import com.treefinance.saas.taskcenter.dao.entity.TaskAttribute;
import com.treefinance.saas.taskcenter.dao.param.TaskAttributeQuery;
import com.treefinance.saas.taskcenter.dao.repository.TaskAttributeRepository;
import com.treefinance.saas.taskcenter.exception.UnexpectedException;
import com.treefinance.saas.taskcenter.service.TaskAttributeService;
import com.treefinance.saas.taskcenter.service.param.TaskAttributeSaveObject;
import com.treefinance.saas.taskcenter.share.cache.redis.RedisDao;
import com.treefinance.saas.taskcenter.share.cache.redis.RedisKeyUtils;
import com.treefinance.saas.taskcenter.share.cache.redis.RedissonLocks;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Created by chenjh on 2017/7/5.
 * <p>
 * 任务拓展属性业务层
 */
@Service
public class TaskAttributeServiceImpl implements TaskAttributeService {
    private static final Logger logger = LoggerFactory.getLogger(TaskAttributeServiceImpl.class);

    private static final String LOCK_KEY = "task_login_time_save_lock:";
    @Autowired
    private TaskAttributeRepository taskAttributeRepository;
    @Autowired
    private RedisDao redisDao;
    @Autowired
    private RedissonLocks redissonLocks;

    @Override
    public TaskAttribute queryAttributeByTaskIdAndName(Long taskId, String name, boolean decrypt) {
        return taskAttributeRepository.queryAttributeByTaskIdAndName(taskId, name, decrypt);
    }

    @Override
    public TaskAttribute queryAttributeByNameAndValue(String name, String value, boolean encrypt) {
        return taskAttributeRepository.queryAttributeByNameAndValue(name, value, encrypt);
    }

    @Override
    public Map<String, String> getAttributeMapByTaskId(@Nonnull Long taskId, boolean decrypt) {
        return taskAttributeRepository.getAttributeMapByTaskId(taskId, decrypt);
    }

    @Override
    public Map<String, String> getAttributeMapByTaskIdAndInNames(@Nonnull Long taskId, @Nonnull String[] names, boolean decrypt) {
        return taskAttributeRepository.getAttributeMapByTaskIdAndInNames(taskId, Arrays.asList(names), decrypt);
    }

    @Override
    public List<TaskAttribute> listAttributesByTaskId(Long taskId) {
        return taskAttributeRepository.listAttributesByTaskId(taskId);
    }

    @Override
    public List<TaskAttribute> listAttributesByTaskIdAndInNames(@Nonnull Long taskId, @Nonnull String[] names, boolean decrypt) {
        return taskAttributeRepository.listAttributesByTaskIdAndInNames(taskId, Arrays.asList(names), decrypt);
    }

    @Override
    public List<TaskAttribute> listAttributesInTaskIdsAndByName(@Nonnull List<Long> taskIds, @Nonnull String name) {
        return taskAttributeRepository.listAttributesInTaskIdsAndByName(taskIds, name);
    }

    @Override
    public List<TaskAttribute> listAttributesInTaskIdsAndByName(@Nonnull List<Long> taskIds, @Nonnull String name, boolean decrypt) {
        return taskAttributeRepository.listAttributesInTaskIdsAndByName(taskIds, name, decrypt);
    }

    @Override
    public List<TaskAttribute> queryAttributes(@Nonnull TaskAttributeQuery query) {
        return taskAttributeRepository.queryAttributes(query);
    }

    @Override
    public Long insert(@Nonnull Long taskId, @Nonnull String name, @Nullable String value, boolean encrypt) {
        return taskAttributeRepository.insertAttribute(taskId, name, value, encrypt);
    }

    @Override
    public void insertOrUpdate(@Nonnull Long taskId, @Nonnull String name, @Nullable String value, boolean encrypt) {
        taskAttributeRepository.insertOrUpdateAttribute(taskId, name, value, encrypt);
    }

    @Override
    public void deleteAttributeByTaskIdAndName(@Nonnull Long taskId, @Nonnull String name) {
        taskAttributeRepository.deleteAttributeByTaskIdAndName(taskId, name);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void saveAttributes(@Nonnull Long taskId, @Nonnull List<TaskAttributeSaveObject> attributes) {
        if (CollectionUtils.isNotEmpty(attributes)) {
            for (TaskAttributeSaveObject attribute : attributes) {
                taskAttributeRepository.insertOrUpdateAttribute(taskId, attribute.getName(), attribute.getValue(), attribute.isSensitive());
            }
        }
    }

    @Override
    public Date queryLoginTime(@Nonnull Long taskId) {
        String key = RedisKeyUtils.genTaskLoginTimeKey(taskId);
        String dateTime = StringUtils.trim(redisDao.get(key));
        if (StringUtils.isNotEmpty(dateTime)) {
            return new Date(Long.parseLong(dateTime));
        }

        String lockKey = getLockKey(taskId);
        try {
            return redissonLocks.tryLock(lockKey, 5, 60, TimeUnit.SECONDS, isLock -> {
                Date date = taskAttributeRepository.queryAttributeValueAsDate(taskId, ETaskAttribute.LOGIN_TIME.getAttribute());
                if (date != null && isLock) {
                    this.cacheLoginTime(taskId, date);
                } else {
                    logger.info("获取登录时间时,未查询到任务登录时间,任务未登录.taskId={}", taskId);
                }
                return date;
            });
        } catch (InterruptedException e) {
            throw new UnexpectedException("Thread interrupted when querying login-time! - taskId: " + taskId, e);
        }
    }

    @Override
    public void saveLoginTime(@Nonnull Long taskId, @Nonnull Date date) {
        try {
            String lockKey = getLockKey(taskId);
            redissonLocks.tryLock(lockKey, 5, 60, TimeUnit.SECONDS, () -> {
                taskAttributeRepository.insertOrUpdateAttribute(taskId, ETaskAttribute.LOGIN_TIME.getAttribute(), date);

                cacheLoginTime(taskId, date);
            });
        } catch (InterruptedException e) {
            throw new UnexpectedException("Thread interrupted when saving login-time! - taskId: " + taskId, e);
        }
    }

    private void cacheLoginTime(@Nonnull Long taskId, @Nonnull Date date) {
        String key = RedisKeyUtils.genTaskLoginTimeKey(taskId);
        String value = String.valueOf(date.getTime());
        if (redisDao.setEx(key, value, 1, TimeUnit.HOURS)) {
            logger.info("记录任务登录时间缓存! taskId={}, key={}, value={}", taskId, key, value);
        } else {
            logger.warn("添加任务登录时间缓存失败! taskId={}, key={}，value={}", taskId, key, value);
        }
    }

    private String getLockKey(Long taskId) {
        return LOCK_KEY + taskId;
    }

    @Override
    public Long findTaskIdByMoxieTid(String moxieTaskId) {
        if (StringUtils.isNotBlank(moxieTaskId)) {
            TaskAttribute taskAttribute = taskAttributeRepository.queryAttributeByNameAndValue(ETaskAttribute.FUND_MOXIE_TASKID.getAttribute(), moxieTaskId, false);
            if (taskAttribute != null) {
                return taskAttribute.getTaskId();
            }
        }
        return null;
    }

}

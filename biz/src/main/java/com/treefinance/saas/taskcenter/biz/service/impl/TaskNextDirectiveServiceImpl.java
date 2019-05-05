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

import com.alibaba.fastjson.JSON;
import com.treefinance.saas.taskcenter.biz.service.TaskNextDirectiveService;
import com.treefinance.saas.taskcenter.common.enums.EDirective;
import com.treefinance.saas.taskcenter.dao.entity.TaskNextDirective;
import com.treefinance.saas.taskcenter.dao.repository.TaskNextDirectiveRepository;
import com.treefinance.saas.taskcenter.exception.UnexpectedException;
import com.treefinance.saas.taskcenter.service.domain.DirectiveEntity;
import com.treefinance.saas.taskcenter.share.cache.redis.RedisDao;
import com.treefinance.saas.taskcenter.share.cache.redis.RedissonLocks;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by luoyihua on 2017/4/26.
 */
@Service
public class TaskNextDirectiveServiceImpl implements TaskNextDirectiveService {
    private static final Logger logger = LoggerFactory.getLogger(TaskNextDirectiveServiceImpl.class);
    private static final int DAY_SECOND = 24 * 60 * 60;
    private static final String LOCK_KEY = "task_directive_save_lock:";

    @Autowired
    private TaskNextDirectiveRepository taskNextDirectiveRepository;
    @Autowired
    private RedisDao redisDao;
    @Autowired
    private RedissonLocks redissonLocks;

    @Override
    public List<TaskNextDirective> listDirectivesDescWithCreateTimeByTaskId(@Nonnull Long taskId) {
        return taskNextDirectiveRepository.listDirectivesDescWithCreateTimeByTaskId(taskId);
    }

    @Override
    public Long insert(@Nonnull Long taskId, String directive, String remark) {
        TaskNextDirective taskNextDirective = taskNextDirectiveRepository.insertDirective(taskId, directive, remark);

        return taskNextDirective.getId();
    }


    @Override
    public void saveDirective(@Nonnull DirectiveEntity directiveEntity) {
        String lockKey = getLockKey(directiveEntity.getTaskId());
        redissonLocks.lock(lockKey, () -> {
            this.insert(directiveEntity);

            cacheDirective(directiveEntity);
        });
    }

    private String cacheDirective(@Nonnull DirectiveEntity directiveEntity) {
        String key = generaRedisKey(directiveEntity.getTaskId());
        String value = JSON.toJSONString(directiveEntity);
        if (redisDao.setEx(key, value, DAY_SECOND, TimeUnit.SECONDS)) {
            logger.info("指令已经放到redis缓存,有效期一天! key={}，value={}", key, value);
        } else {
            logger.warn("添加指令缓存失败! key={}，value={}", key, value);
        }

        return value;
    }

    private String getLockKey(Long taskId) {
        return LOCK_KEY + taskId;
    }

    private String readCachedDirective(@Nonnull Long taskId) {
        String key = generaRedisKey(taskId);
        return StringUtils.trim(redisDao.get(key));
    }

    @Override
    public DirectiveEntity queryPresentDirective(@Nonnull Long taskId) {
        String value = readCachedDirective(taskId);

        if (StringUtils.isNotEmpty(value)) {
            return JSON.parseObject(value, DirectiveEntity.class);
        }

        String lockKey = getLockKey(taskId);
        try {
            return redissonLocks.tryLock(lockKey, 5, 60, TimeUnit.SECONDS, isLock -> {
                DirectiveEntity directiveEntity = null;
                TaskNextDirective taskDirective = taskNextDirectiveRepository.getLastDirectiveByTaskId(taskId);
                if (taskDirective != null) {
                    directiveEntity = new DirectiveEntity();
                    directiveEntity.setTaskId(taskDirective.getTaskId());
                    directiveEntity.setDirective(taskDirective.getDirective());
                    directiveEntity.setRemark(taskDirective.getRemark());

                    if (isLock) {
                        cacheDirective(directiveEntity);
                    }
                }
                return directiveEntity;
            });
        } catch (InterruptedException e) {
            throw new UnexpectedException("Unexpected exception when querying present directive! - taskId:" + taskId, e);
        }
    }

    @Override
    public String queryPresentDirectiveAsJson(@Nonnull Long taskId) {
        String value = readCachedDirective(taskId);

        if (StringUtils.isEmpty(value)) {
            String lockKey = getLockKey(taskId);
            try {
                value = redissonLocks.tryLock(lockKey, 5, 60, TimeUnit.SECONDS, isLock -> {
                    DirectiveEntity directiveEntity;
                    TaskNextDirective taskDirective = taskNextDirectiveRepository.getLastDirectiveByTaskId(taskId);
                    if (taskDirective != null) {
                        directiveEntity = new DirectiveEntity();
                        directiveEntity.setTaskId(taskDirective.getTaskId());
                        directiveEntity.setDirective(taskDirective.getDirective());
                        directiveEntity.setRemark(taskDirective.getRemark());

                        if (isLock) {
                            return cacheDirective(directiveEntity);
                        }
                        return JSON.toJSONString(directiveEntity);
                    }

                    return null;
                });
            } catch (InterruptedException e) {
                throw new UnexpectedException("Unexpected exception when querying present directive! - taskId:" + taskId, e);
            }
        }

        return value;
    }

    @Override
    public void awaitNext(@Nonnull Long taskId) {
        logger.info("新增过渡指令 >> {}, taskId: {}", EDirective.WAITING, taskId);
        DirectiveEntity directiveEntity = new DirectiveEntity();
        directiveEntity.setTaskId(taskId);
        directiveEntity.setDirective(EDirective.WAITING.value());
        directiveEntity.setRemark("请等待");

        this.saveDirective(directiveEntity);
    }

    @Override
    public void compareAndEnd(@Nonnull Long taskId, @Nullable String directive) {
        if (StringUtils.isNotEmpty(directive)) {
            String lockKey = getLockKey(taskId);
            redissonLocks.lock(lockKey, () -> {
                final DirectiveEntity directiveEntity = this.queryPresentDirective(taskId);
                if(directiveEntity != null){
                    String existDirective = directiveEntity.getDirective();
                    if (directive.equals(existDirective)) {
                        this.awaitNext(taskId);
                        logger.info("当前指令\"{}\"已结束，等待下个指令！taskId: {}", directive, taskId);
                    } else {
                        logger.warn("当前指令已更新，和指定指令不一致，拒绝结束操作！taskId: {}, 当前指令: {}, 目标指令: {}", taskId, existDirective, directive);
                    }
                } else {
                    logger.warn("taskId={}, 指令\"{}\"不存在", taskId, directive);
                }
            });
        } else {
            this.awaitNext(taskId);
            logger.info("当前指令已结束，等待下个指令！taskId: {}", taskId);
        }
    }

    private String generaRedisKey(Long taskId) {
        return String.format("saas-gateway:nextDirective:%s", taskId);
    }
}

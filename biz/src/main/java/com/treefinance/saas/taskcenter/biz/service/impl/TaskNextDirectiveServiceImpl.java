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

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.treefinance.b2b.saas.util.JsonUtils;
import com.treefinance.saas.taskcenter.biz.service.TaskNextDirectiveService;
import com.treefinance.saas.taskcenter.dao.entity.TaskNextDirective;
import com.treefinance.saas.taskcenter.dao.repository.TaskNextDirectiveRepository;
import com.treefinance.saas.taskcenter.dto.DirectiveDTO;
import com.treefinance.saas.taskcenter.share.cache.redis.RedisDao;
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
    private final static int DAY_SECOND = 24 * 60 * 60;

    @Autowired
    private TaskNextDirectiveRepository taskNextDirectiveRepository;
    @Autowired
    private RedisDao redisDao;


    @Override
    public TaskNextDirective getLastDirectiveByTaskId(@Nonnull Long taskId) {
        return taskNextDirectiveRepository.getLastDirectiveByTaskId(taskId);
    }

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
    public void insertAndCacheNextDirective(@Nonnull Long taskId, @Nonnull DirectiveDTO directive) {
        this.insert(taskId, directive.getDirective(), directive.getRemark());

        String content = JsonUtils.toJsonString(directive, "task");
        String key = generaRedisKey(taskId);
        if (redisDao.setEx(key, content, DAY_SECOND, TimeUnit.SECONDS)) {
            logger.info("指令已经放到redis缓存,有效期一天, key={}，content={}", key, content);
        }
    }

    private String generaRedisKey(Long taskId) {
        return String.format("saas-gateway:nextDirective:%s", taskId);
    }

    @Override
    public String getNextDirective(@Nonnull Long taskId) {
        String key = generaRedisKey(taskId);
        String value = redisDao.get(key);

        if (StringUtils.isBlank(value)) {
            TaskNextDirective taskNextDirective = this.getLastDirectiveByTaskId(taskId);
            if (taskNextDirective != null) {
                DirectiveDTO directiveDTO = new DirectiveDTO();
                directiveDTO.setTaskId(taskNextDirective.getTaskId());
                directiveDTO.setDirective(taskNextDirective.getDirective());
                directiveDTO.setRemark(taskNextDirective.getRemark());

                value = JSON.toJSONString(directiveDTO);
            }
        }
        return value;
    }

    @Override
    public void deleteNextDirective(@Nonnull Long taskId) {
        redisDao.deleteKey(generaRedisKey(taskId));
        this.insert(taskId, "waiting", "请等待");
    }

    @Override
    public void deleteNextDirective(@Nonnull Long taskId, @Nullable String directive) {
        if (StringUtils.isNotEmpty(directive)) {
            String value = this.getNextDirective(taskId);
            if (StringUtils.isNotEmpty(value)) {
                JSONObject jasonObject = JSON.parseObject(value);
                String existDirective = jasonObject.getString("directive");
                if (directive.equals(existDirective)) {
                    this.deleteNextDirective(taskId);
                    logger.info("taskId={},下一指令信息={}已删除", taskId, existDirective);
                } else {
                    logger.info("taskId={},需要删除的指令信息={}和缓存的指令信息={}不一致", taskId, directive, existDirective);
                }
            } else {
                logger.info("taskId={},下一指令信息={}不存在", taskId, directive);
            }
        } else {
            this.deleteNextDirective(taskId);
            logger.info("taskId={},下一指令信息已删除", taskId);
        }
    }
}

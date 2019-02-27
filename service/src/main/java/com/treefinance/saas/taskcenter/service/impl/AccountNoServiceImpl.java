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

package com.treefinance.saas.taskcenter.service.impl;

import com.datatrees.spider.share.api.SpiderTaskApi;
import com.treefinance.saas.taskcenter.dao.entity.Task;
import com.treefinance.saas.taskcenter.dao.repository.TaskRepository;
import com.treefinance.saas.taskcenter.service.AccountNoService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Jerry
 * @date 2019-02-27 20:01
 */
@Service
public class AccountNoServiceImpl implements AccountNoService {
    private static final Logger LOGGER = LoggerFactory.getLogger(AccountNoServiceImpl.class);

    private final TaskRepository taskRepository;
    private final SpiderTaskApi spiderTaskApi;

    @Autowired
    public AccountNoServiceImpl(TaskRepository taskRepository, SpiderTaskApi spiderTaskApi) {
        this.taskRepository = taskRepository;
        this.spiderTaskApi = spiderTaskApi;
    }

    @Override
    public void saveAccountNoIfAbsent(Long taskId) {
        Task task = taskRepository.getTaskById(taskId);
        if (StringUtils.isBlank(task.getAccountNo())) {
            try {
                String accountNo = spiderTaskApi.getTaskAccountNo(taskId);
                LOGGER.info("记录任务accountNo:调用爬数查询任务账号信息,taskId={},accountNo={}", taskId, accountNo);
                if (StringUtils.isNotBlank(accountNo)) {
                    taskRepository.updateAccountNoById(taskId, accountNo);
                }
            } catch (Exception e) {
                LOGGER.error("记录任务accountNo:调用爬数查询任务账号信息异常,taskId={}", taskId, e);
            }
        }
    }
}

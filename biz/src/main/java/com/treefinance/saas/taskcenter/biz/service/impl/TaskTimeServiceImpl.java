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

import com.treefinance.saas.taskcenter.biz.service.TaskTimeService;
import com.treefinance.saas.taskcenter.biz.service.task.TaskTimeoutHandler;
import com.treefinance.saas.taskcenter.biz.service.thread.TaskActiveTimeoutThread;
import com.treefinance.saas.taskcenter.biz.service.thread.TaskCrawlerTimeoutThread;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

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
    private List<TaskTimeoutHandler> taskTimeoutHandlers;
    @Autowired
    private ThreadPoolTaskExecutor threadPoolExecutor;

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

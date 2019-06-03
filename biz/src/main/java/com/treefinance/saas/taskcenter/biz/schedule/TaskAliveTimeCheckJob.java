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

package com.treefinance.saas.taskcenter.biz.schedule;

import com.treefinance.saas.assistant.model.Constants;
import com.treefinance.saas.taskcenter.biz.schedule.detector.TaskAliveTimeDetector;
import com.treefinance.saas.taskcenter.biz.service.TaskService;
import com.treefinance.saas.taskcenter.dao.entity.Task;
import com.treefinance.saas.taskcenter.util.SystemUtils;
import com.treefinance.toolkit.util.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

/**
 * @author Jerry
 * @date 2019-03-04 16:02
 */
@Component("taskAliveTimeCheckSchedule")
public class TaskAliveTimeCheckJob extends BaseSingleJob {

    @Autowired
    private TaskService taskService;
    @Autowired
    private TaskAliveTimeDetector aliveTimeDetector;

    @Override
    protected void process() {
        Date now = SystemUtils.now();
        Date before = DateUtils.minusMinutes(now, 60);
        List<Task> tasks = taskService.listRunningTasks(Byte.valueOf(Constants.SAAS_ENV_VALUE), before, now);
        try {
            for (Task task : tasks) {
                aliveTimeDetector.detect(task.getId(), now);
            }
        } catch (InterruptedException e) {
            logger.warn("TaskAliveTimeCheckJob was interrupted!", e);
        }
    }

}

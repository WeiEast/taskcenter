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

import com.treefinance.saas.taskcenter.biz.service.TaskSupportService;
import com.treefinance.saas.taskcenter.dao.entity.TaskSupport;
import com.treefinance.saas.taskcenter.dao.repository.TaskSupportRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Jerry
 * @since 10:56 02/05/2017
 */
@Service
public class TaskSupportServiceImpl implements TaskSupportService {

    @Autowired
    private TaskSupportRepository taskSupportRepository;

    @Override
    public List<TaskSupport> getSupportedList(String supportType, Integer id, String name) {
        return taskSupportRepository.queryEnabledASCWithSort(supportType, id, name);
    }

}
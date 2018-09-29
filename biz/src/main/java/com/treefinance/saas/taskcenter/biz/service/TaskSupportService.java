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

package com.treefinance.saas.taskcenter.biz.service;

import com.treefinance.saas.taskcenter.dao.entity.TaskSupport;
import com.treefinance.saas.taskcenter.dao.entity.TaskSupportCriteria;
import com.treefinance.saas.taskcenter.dao.mapper.TaskSupportMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Jerry
 * @since 10:56 02/05/2017
 */
@Service
public class TaskSupportService {

    @Autowired
    private TaskSupportMapper taskSupportMapper;


    public List<TaskSupport> getSupportedList(String supportType, Integer id, String name) {
        TaskSupportCriteria supportCriteria = new TaskSupportCriteria();
        supportCriteria.setOrderByClause("Sort ASC");
        TaskSupportCriteria.Criteria innerCriteria = supportCriteria.createCriteria();
        innerCriteria.andEnableEqualTo(Boolean.TRUE).andCategoryEqualTo(supportType);
        if (StringUtils.isBlank(name)) {
            innerCriteria.andIsShowEqualTo(Boolean.TRUE);
        }
        if (id != null) {
            innerCriteria.andIdEqualTo(id);
        }
        if (StringUtils.isNotBlank(name)) {
            innerCriteria.andTypeEqualTo(name);
        }

        return taskSupportMapper.selectByExample(supportCriteria);
    }

}

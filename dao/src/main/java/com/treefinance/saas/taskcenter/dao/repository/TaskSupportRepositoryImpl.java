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

package com.treefinance.saas.taskcenter.dao.repository;

import com.treefinance.saas.taskcenter.dao.entity.TaskSupport;
import com.treefinance.saas.taskcenter.dao.entity.TaskSupportCriteria;
import com.treefinance.saas.taskcenter.dao.mapper.TaskSupportMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author Jerry
 * @date 2018/11/21 01:07
 */
@Repository
public class TaskSupportRepositoryImpl implements TaskSupportRepository {
    @Autowired
    private TaskSupportMapper taskSupportMapper;

    @Override
    public List<TaskSupport> queryEnabledASCWithSort(String category, Integer id, String type) {
        TaskSupportCriteria supportCriteria = new TaskSupportCriteria();
        supportCriteria.setOrderByClause("Sort ASC");
        TaskSupportCriteria.Criteria criteria = supportCriteria.createCriteria().andEnableEqualTo(Boolean.TRUE).andCategoryEqualTo(category);
        if (id != null) {
            criteria.andIdEqualTo(id);
        }
        if (StringUtils.isNotBlank(type)) {
            criteria.andTypeEqualTo(type);
        } else {
            criteria.andIsShowEqualTo(Boolean.TRUE);
        }

        return taskSupportMapper.selectByExample(supportCriteria);
    }
}
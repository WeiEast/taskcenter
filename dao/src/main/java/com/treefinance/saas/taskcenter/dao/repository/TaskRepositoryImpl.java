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

import com.treefinance.saas.taskcenter.context.BizObjectValidator;
import com.treefinance.saas.taskcenter.dao.entity.Task;
import com.treefinance.saas.taskcenter.dao.entity.TaskAndTaskAttribute;
import com.treefinance.saas.taskcenter.dao.entity.TaskCriteria;
import com.treefinance.saas.taskcenter.dao.mapper.TaskAndTaskAttributeMapper;
import com.treefinance.saas.taskcenter.dao.mapper.TaskMapper;
import com.treefinance.saas.taskcenter.dao.param.TaskAttrCompositeQuery;
import com.treefinance.saas.taskcenter.dao.param.TaskPagingQuery;
import com.treefinance.saas.taskcenter.dao.param.TaskParams;
import com.treefinance.saas.taskcenter.dao.param.TaskQuery;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.annotation.Nonnull;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Jerry
 * @date 2018/11/19 19:54
 */
@Repository
public class TaskRepositoryImpl extends AbstractRepository implements TaskRepository {

    @Autowired
    private TaskMapper taskMapper;
    @Autowired
    private TaskAndTaskAttributeMapper taskAndTaskAttributeMapper;

    @Override
    public Task getTaskById(@Nonnull Long id) {
        Task task = taskMapper.selectByPrimaryKey(id);

        BizObjectValidator.notNull(task, "任务不存在！- taskId: " + id);

        decryptFields(task);

        return task;
    }

    private void decryptFields(Task task) {
        String accountNo = task.getAccountNo();
        task.setAccountNo(decryptNormal(accountNo));
    }

    @Override
    public List<Long> listTaskIdsByAppIdAndBizTypeAndUniqueId(@Nonnull String appId, @Nonnull Byte bizType, @Nonnull String uniqueId) {
        TaskCriteria taskCriteria = new TaskCriteria();
        taskCriteria.createCriteria().andAppIdEqualTo(appId).andBizTypeEqualTo(bizType).andUniqueIdEqualTo(uniqueId);
        List<Task> tasks = taskMapper.selectByExample(taskCriteria);

        if (CollectionUtils.isNotEmpty(tasks)) {
            return tasks.stream().map(Task::getId).collect(Collectors.toList());
        }

        return Collections.emptyList();
    }

    @Override
    public List<Task> listTasksByStatusAndEnvAndCreateTimeBetween(@Nonnull Byte status, @Nonnull Byte saasEnv, @Nonnull Date startDate, @Nonnull Date endDate) {
        TaskCriteria criteria = new TaskCriteria();
        criteria.createCriteria().andStatusEqualTo(status).andSaasEnvEqualTo(saasEnv).andCreateTimeGreaterThanOrEqualTo(startDate).andCreateTimeLessThan(endDate);

        List<Task> tasks = taskMapper.selectByExample(criteria);

        return postHandle(tasks);
    }

    private List<Task> postHandle(List<Task> tasks) {
        if (CollectionUtils.isNotEmpty(tasks)) {
            tasks.forEach(this::decryptFields);
        }

        return tasks;
    }

    @Override
    public Task insertTask(@Nonnull TaskParams taskParams) {
        Task task = new Task();
        task.setId(generateUniqueId());
        task.setUniqueId(taskParams.getUniqueId());
        task.setAppId(taskParams.getAppId());
        task.setBizType(taskParams.getBizType());
        task.setWebSite(StringUtils.trimToNull(taskParams.getWebsite()));
        task.setSaasEnv(taskParams.getSaasEnv());
        task.setStatus((byte)0);
        taskMapper.insertSelective(task);
        return task;
    }

    @Override
    public int updateTaskByIdAndStatusNotIn(@Nonnull Task task, @Nonnull Byte... statuses) {
        TaskCriteria taskCriteria = new TaskCriteria();
        taskCriteria.createCriteria().andIdEqualTo(task.getId()).andStatusNotIn(Arrays.asList(statuses));

        return taskMapper.updateByExampleSelective(task, taskCriteria);
    }

    @Override
    public int updateTaskByIdAndStatusNotIn(@Nonnull TaskParams taskParams, @Nonnull Long id, @Nonnull Byte... statuses) {
        TaskCriteria taskCriteria = new TaskCriteria();
        taskCriteria.createCriteria().andIdEqualTo(id).andStatusNotIn(Arrays.asList(statuses));

        Task task = new Task();

        String appId = taskParams.getAppId();
        if (StringUtils.isNotEmpty(appId)) {
            task.setAppId(appId);
        }

        String uniqueId = taskParams.getUniqueId();
        if (StringUtils.isNotEmpty(uniqueId)) {
            task.setUniqueId(uniqueId);
        }

        String accountNo = taskParams.getAccountNo();
        if (StringUtils.isNotEmpty(accountNo)) {
            addWithEncryption(task, accountNo);
        }

        String website = taskParams.getWebsite();
        if (StringUtils.isNotEmpty(website)) {
            task.setWebSite(website);
        }

        String stepCode = taskParams.getStepCode();
        if (StringUtils.isNotEmpty(stepCode)) {
            task.setStepCode(stepCode);
        }

        Byte bizType = taskParams.getBizType();
        if (bizType != null) {
            task.setBizType(bizType);
        }

        Byte status = taskParams.getStatus();
        if (status != null) {
            task.setStatus(status);
        }

        Byte saasEnv = taskParams.getSaasEnv();
        if (saasEnv != null) {
            task.setSaasEnv(saasEnv);
        }

        return taskMapper.updateByExampleSelective(task, taskCriteria);
    }

    private void addWithEncryption(Task task, String accountNo) {
        task.setAccountNo(encryptNormal(accountNo));
    }

    @Override
    public void updateAccountNoById(@Nonnull Long taskId, @Nonnull String accountNo) {
        Task task = new Task();
        task.setId(taskId);
        addWithEncryption(task, accountNo);
        taskMapper.updateByPrimaryKeySelective(task);
    }

    @Override
    public List<TaskAndTaskAttribute> queryCompositeTasks(@Nonnull TaskAttrCompositeQuery query) {
        Map<String, Object> map = createCompositeQueryMap(query);

        map.put("orderStr", query.getOrder());
        map.put("start", query.getOffset());
        map.put("limit", query.getLimit());

        return taskAndTaskAttributeMapper.getByExample(map);
    }

    @Override
    public long countCompositeTasks(@Nonnull TaskAttrCompositeQuery query) {
        Map<String, Object> map = createCompositeQueryMap(query);

        return taskAndTaskAttributeMapper.countByExample(map);
    }

    @Override
    public List<Task> queryPagingTasks(@Nonnull TaskPagingQuery query) {
        TaskCriteria taskCriteria = createPagingQueryCriteria(query);

        int limit = query.getLimit();
        if (limit > 0) {
            taskCriteria.setLimit(limit);
            int offset = Math.max(query.getOffset(), 0);
            taskCriteria.setOffset(offset);

            return taskMapper.selectPaginationByExample(taskCriteria);
        }

        return taskMapper.selectByExample(taskCriteria);
    }

    @Override
    public long countPagingTasks(@Nonnull TaskPagingQuery query) {
        TaskCriteria taskCriteria = createPagingQueryCriteria(query);

        return taskMapper.countByExample(taskCriteria);
    }

    private TaskCriteria createPagingQueryCriteria(@Nonnull TaskQuery query) {
        TaskCriteria taskCriteria = new TaskCriteria();
        TaskCriteria.Criteria criteria = taskCriteria.createCriteria();

        Long id = query.getId();
        if (id != null) {
            criteria.andIdEqualTo(id);
        }

        List<String> appIds = query.getAppIds();
        if (CollectionUtils.isNotEmpty(appIds)) {
            if (appIds.size() == 1) {
                criteria.andAppIdEqualTo(appIds.get(0));
            } else {
                criteria.andAppIdIn(appIds);
            }
        }

        List<Byte> bizTypes = query.getBizTypes();
        if (CollectionUtils.isNotEmpty(bizTypes)) {
            if (bizTypes.size() == 1) {
                criteria.andBizTypeEqualTo(bizTypes.get(0));
            } else {
                criteria.andBizTypeIn(bizTypes);
            }
        }

        String website = query.getWebsite();
        if (StringUtils.isNotEmpty(website)) {
            criteria.andWebSiteEqualTo(website);
        }

        String stepCode = query.getStepCode();
        if (StringUtils.isNotEmpty(stepCode)) {
            criteria.andStepCodeEqualTo(stepCode);
        }

        String uniqueId = query.getUniqueId();
        if (StringUtils.isNotEmpty(uniqueId)) {
            criteria.andUniqueIdEqualTo(uniqueId);
        }

        String accountNo = query.getAccountNo();
        if (StringUtils.isNotEmpty(accountNo)) {
            criteria.andAccountNoEqualTo(accountNo);
        }

        Byte saasEnv = query.getSaasEnv();
        if (saasEnv != null) {
            criteria.andSaasEnvEqualTo(saasEnv);
        }

        Byte status = query.getStatus();
        if (status != null) {
            criteria.andStatusEqualTo(status);
        }

        Date startDate = query.getStartDate();
        if (startDate != null) {
            criteria.andCreateTimeGreaterThanOrEqualTo(startDate);
        }

        Date endDate = query.getEndDate();
        if (endDate != null) {
            criteria.andCreateTimeLessThanOrEqualTo(endDate);
        }

        String order = query.getOrder();
        if (StringUtils.isNotEmpty(order)) {
            taskCriteria.setOrderByClause(order);
        }
        return taskCriteria;
    }

    @Override
    public List<Task> queryTasks(@Nonnull TaskQuery query) {
        TaskCriteria taskCriteria = createQueryCriteria(query);

        if (query instanceof TaskPagingQuery) {
            int limit = ((TaskPagingQuery)query).getLimit();
            if (limit > 0) {
                taskCriteria.setLimit(limit);

                int offset = Math.max(((TaskPagingQuery)query).getOffset(), 0);
                taskCriteria.setOffset(offset);

                return postHandle(taskMapper.selectPaginationByExample(taskCriteria));
            }
        }

        return postHandle(taskMapper.selectByExample(taskCriteria));
    }

    @Override
    public long countTasks(@Nonnull TaskQuery query) {
        TaskCriteria taskCriteria = createQueryCriteria(query);

        return taskMapper.countByExample(taskCriteria);
    }

    private TaskCriteria createQueryCriteria(@Nonnull TaskQuery query) {
        TaskCriteria taskCriteria = new TaskCriteria();
        TaskCriteria.Criteria criteria = taskCriteria.createCriteria();

        Long id = query.getId();
        if (id != null) {
            criteria.andIdEqualTo(id);
        }

        List<String> appIds = query.getAppIds();
        if (CollectionUtils.isNotEmpty(appIds)) {
            if (appIds.size() == 1) {
                criteria.andAppIdEqualTo(appIds.get(0));
            } else {
                criteria.andAppIdIn(appIds);
            }
        }

        List<Byte> bizTypes = query.getBizTypes();
        if (CollectionUtils.isNotEmpty(bizTypes)) {
            if (bizTypes.size() == 1) {
                criteria.andBizTypeEqualTo(bizTypes.get(0));
            } else {
                criteria.andBizTypeIn(bizTypes);
            }
        }

        String website = query.getWebsite();
        if (StringUtils.isNotEmpty(website)) {
            criteria.andWebSiteEqualTo(website);
        }

        String stepCode = query.getStepCode();
        if (StringUtils.isNotEmpty(stepCode)) {
            criteria.andStepCodeEqualTo(stepCode);
        }

        String uniqueId = query.getUniqueId();
        if (StringUtils.isNotEmpty(uniqueId)) {
            criteria.andUniqueIdEqualTo(uniqueId);
        }

        String accountNo = encryptNormal(query.getAccountNo());
        if (StringUtils.isNotEmpty(accountNo)) {
            criteria.andAccountNoEqualTo(accountNo);
        }

        Byte saasEnv = query.getSaasEnv();
        if (saasEnv != null) {
            criteria.andSaasEnvEqualTo(saasEnv);
        }

        Byte status = query.getStatus();
        if (status != null) {
            criteria.andStatusEqualTo(status);
        }

        Date startDate = query.getStartDate();
        if (startDate != null) {
            criteria.andCreateTimeGreaterThanOrEqualTo(startDate);
        }

        Date endDate = query.getEndDate();
        if (endDate != null) {
            criteria.andCreateTimeLessThanOrEqualTo(endDate);
        }

        String order = query.getOrder();
        if (StringUtils.isNotEmpty(order)) {
            taskCriteria.setOrderByClause(order);
        }
        return taskCriteria;
    }

    private Map<String, Object> createCompositeQueryMap(TaskAttrCompositeQuery query) {
        Map<String, Object> map = new HashMap<>(12);

        map.put("appId", query.getAppId());

        List<Byte> bizTypes = query.getBizTypes();
        if (CollectionUtils.isNotEmpty(bizTypes)) {
            if (bizTypes.size() == 1) {
                map.put("bizType", bizTypes.get(0));
            } else {
                map.put("bizTypeList", bizTypes);
            }
        }
        map.put("webSite", query.getWebsite());
        map.put("saasEnv", query.getSaasEnv());
        map.put("status", query.getStatus());
        map.put("startTime", query.getStartDate());
        map.put("endTime", query.getEndDate());
        map.put("name", query.getAttrName());
        map.put("value", query.getAttrValue());

        return map;
    }
}

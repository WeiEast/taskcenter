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

import com.treefinance.basicservice.security.crypto.facade.EncryptionIntensityEnum;
import com.treefinance.basicservice.security.crypto.facade.ISecurityCryptoService;
import com.treefinance.commonservice.uid.UidService;
import com.treefinance.saas.taskcenter.dao.domain.TaskCompositeQuery;
import com.treefinance.saas.taskcenter.dao.domain.TaskDO;
import com.treefinance.saas.taskcenter.dao.domain.TaskQuery;
import com.treefinance.saas.taskcenter.dao.entity.Task;
import com.treefinance.saas.taskcenter.dao.entity.TaskAndTaskAttribute;
import com.treefinance.saas.taskcenter.dao.entity.TaskCriteria;
import com.treefinance.saas.taskcenter.dao.mapper.TaskAndTaskAttributeMapper;
import com.treefinance.saas.taskcenter.dao.mapper.TaskMapper;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.annotation.Nonnull;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Jerry
 * @date 2018/11/19 19:54
 */
@Repository
public class TaskRepositoryImpl implements TaskRepository {

    @Autowired
    private TaskMapper taskMapper;
    @Autowired
    private UidService uidService;
    @Autowired
    private ISecurityCryptoService securityCryptoService;
    @Autowired
    private TaskAndTaskAttributeMapper taskAndTaskAttributeMapper;

    @Override
    public Task getTaskById(@Nonnull Long id) {
        return taskMapper.selectByPrimaryKey(id);
    }

    @Override
    public List<Task> listTasksByAppIdAndBizTypeAndUniqueId(@Nonnull String appId, @Nonnull Byte bizType, @Nonnull String uniqueId) {
        TaskCriteria taskCriteria = new TaskCriteria();
        taskCriteria.createCriteria().andAppIdEqualTo(appId).andBizTypeEqualTo(bizType).andUniqueIdEqualTo(uniqueId);
        return taskMapper.selectByExample(taskCriteria);
    }

    @Override
    public List<Task> listTasksByStatusAndEnvAndCreateTimeBetween(@Nonnull Byte status, @Nonnull Byte saasEnv, @Nonnull Date startDate, @Nonnull Date endDate) {
        TaskCriteria criteria = new TaskCriteria();
        criteria.createCriteria().andStatusEqualTo(status).andSaasEnvEqualTo(saasEnv).andCreateTimeGreaterThanOrEqualTo(startDate).andCreateTimeLessThan(endDate);

        return taskMapper.selectByExample(criteria);
    }

    @Override
    public List<Task> queryTasks(@Nonnull TaskQuery query) {
        TaskCriteria taskCriteria = createQueryCriteria(query);

        int limit = query.getLimit();
        if (limit > 0) {
            taskCriteria.setLimit(limit);
            int offset = query.getOffset();
            if (offset < 0) {
                offset = 0;
            }
            taskCriteria.setOffset(offset);

            return taskMapper.selectPaginationByExample(taskCriteria);
        }

        return taskMapper.selectByExample(taskCriteria);
    }

    @Override
    public long countTasks(@Nonnull TaskQuery query) {
        TaskCriteria taskCriteria = createQueryCriteria(query);

        return taskMapper.countByExample(taskCriteria);
    }

    private TaskCriteria createQueryCriteria(TaskQuery query) {
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
    public Task insertTask(@Nonnull TaskDO taskDO) {
        Task task = new Task();
        task.setId(uidService.getId());
        task.setUniqueId(taskDO.getUniqueId());
        task.setAppId(taskDO.getAppId());
        task.setBizType(taskDO.getBizType());
        if (StringUtils.isNotBlank(taskDO.getWebsite())) {
            task.setWebSite(taskDO.getWebsite());
        }
        task.setSaasEnv(taskDO.getSaasEnv());
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
    public int updateTaskByIdAndStatusNotIn(@Nonnull TaskDO taskDO, @Nonnull Byte... statuses) {
        TaskCriteria taskCriteria = new TaskCriteria();
        taskCriteria.createCriteria().andIdEqualTo(taskDO.getId()).andStatusNotIn(Arrays.asList(statuses));

        Task task = new Task();

        String appId = taskDO.getAppId();
        if (StringUtils.isNotEmpty(appId)) {
            task.setAppId(appId);
        }

        String uniqueId = taskDO.getUniqueId();
        if (StringUtils.isNotEmpty(uniqueId)) {
            task.setUniqueId(uniqueId);
        }

        String accountNo = taskDO.getAccountNo();
        if (StringUtils.isNotEmpty(accountNo)) {
            accountNo = securityCryptoService.encrypt(accountNo, EncryptionIntensityEnum.NORMAL);
            task.setAccountNo(accountNo);
        }

        String website = taskDO.getWebsite();
        if (StringUtils.isNotEmpty(website)) {
            task.setWebSite(website);
        }

        String stepCode = taskDO.getStepCode();
        if (StringUtils.isNotEmpty(stepCode)) {
            task.setStepCode(stepCode);
        }

        Byte bizType = taskDO.getBizType();
        if (bizType != null) {
            task.setBizType(bizType);
        }

        Byte status = taskDO.getStatus();
        if (status != null) {
            task.setStatus(status);
        }

        Byte saasEnv = taskDO.getSaasEnv();
        if (saasEnv != null) {
            task.setSaasEnv(saasEnv);
        }

        return taskMapper.updateByExampleSelective(task, taskCriteria);
    }

    @Override
    public void updateAccountNoById(@Nonnull Long taskId, @Nonnull String accountNo) {
        Task task = new Task();
        task.setId(taskId);
        String account = securityCryptoService.encrypt(accountNo, EncryptionIntensityEnum.NORMAL);
        task.setAccountNo(account);
        taskMapper.updateByPrimaryKeySelective(task);
    }

    @Override
    public List<TaskAndTaskAttribute> queryCompositeTasks(@Nonnull TaskCompositeQuery query) {
        Map<String, Object> map = createCompositeQueryMap(query);

        map.put("orderStr", query.getOrder());
        map.put("start", query.getOffset());
        map.put("limit", query.getLimit());

        return taskAndTaskAttributeMapper.getByExample(map);
    }

    @Override
    public long countCompositeTasks(@Nonnull TaskCompositeQuery query) {
        Map<String, Object> map = createCompositeQueryMap(query);

        return taskAndTaskAttributeMapper.countByExample(map);
    }

    private Map<String, Object> createCompositeQueryMap(TaskCompositeQuery query) {
        Map<String, Object> map = new HashMap<>();

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
        map.put("name", query.getName());
        map.put("value", query.getValue());
        map.put("startTime", query.getStartDate());
        map.put("endTime", query.getEndDate());
        return map;
    }
}

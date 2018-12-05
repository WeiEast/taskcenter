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
import com.treefinance.saas.taskcenter.biz.service.TaskAttributeService;
import com.treefinance.saas.taskcenter.biz.service.TaskLogService;
import com.treefinance.saas.taskcenter.biz.service.TaskService;
import com.treefinance.saas.taskcenter.biz.service.directive.DirectiveService;
import com.treefinance.saas.taskcenter.common.enums.EDirective;
import com.treefinance.saas.taskcenter.common.enums.ETaskAttribute;
import com.treefinance.saas.taskcenter.common.enums.ETaskStatus;
import com.treefinance.saas.taskcenter.common.enums.ETaskStep;
import com.treefinance.saas.taskcenter.common.enums.TaskStatusMsgEnum;
import com.treefinance.saas.taskcenter.common.model.dto.DirectiveDTO;
import com.treefinance.saas.taskcenter.common.model.dto.TaskDTO;
import com.treefinance.saas.taskcenter.common.util.CommonUtils;
import com.treefinance.saas.taskcenter.common.util.DataConverterUtils;
import com.treefinance.saas.taskcenter.dao.domain.TaskCompositeQuery;
import com.treefinance.saas.taskcenter.dao.domain.TaskDO;
import com.treefinance.saas.taskcenter.dao.domain.TaskQuery;
import com.treefinance.saas.taskcenter.dao.entity.Task;
import com.treefinance.saas.taskcenter.dao.entity.TaskAndTaskAttribute;
import com.treefinance.saas.taskcenter.dao.repository.TaskRepository;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.validation.ValidationException;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author haojiahong
 * @date 2018/9/21
 */
@Service
public class TaskServiceImpl implements TaskService {

    protected final Logger logger = LoggerFactory.getLogger(getClass());
    private static final Byte[] DONE_STATUSES = {ETaskStatus.CANCEL.getStatus(), ETaskStatus.SUCCESS.getStatus(), ETaskStatus.FAIL.getStatus()};

    @Autowired
    private TaskAttributeService taskAttributeService;
    @Autowired
    private TaskLogService taskLogService;
    @Autowired
    private DirectiveService directiveService;
    @Autowired
    private TaskRepository taskRepository;

    @Override
    public Task getTaskById(@Nonnull Long taskId) {
        return taskRepository.getTaskById(taskId);
    }

    @Override
    public Byte getTaskStatusById(@Nonnull Long taskId) {
        Task task = getTaskById(taskId);

        return task != null ? task.getStatus() : null;
    }

    @Override
    public List<Task> listRunningTasksByEnvAndCreateTimeBetween(@Nonnull Byte saasEnv, @Nonnull Date startDate, @Nonnull Date endDate) {
        return taskRepository.listTasksByStatusAndEnvAndCreateTimeBetween(ETaskStatus.RUNNING.getStatus(), saasEnv, startDate, endDate);
    }

    @Override
    public List<Task> queryTasks(@Nonnull TaskQuery query) {
        return taskRepository.queryTasks(query);
    }

    @Override
    public long countTasks(@Nonnull TaskQuery query) {
        return taskRepository.countTasks(query);
    }

    @Override
    public List<TaskAndTaskAttribute> queryCompositeTasks(@Nonnull TaskCompositeQuery query) {
        return taskRepository.queryCompositeTasks(query);
    }

    @Override
    public long countCompositeTasks(@Nonnull TaskCompositeQuery query) {
        return taskRepository.countCompositeTasks(query);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Long createTask(@Nonnull TaskDO taskDO, String source, String extra) {
        Task task = taskRepository.insertTask(taskDO);

        Long id = task.getId();
        if (StringUtils.isNotBlank(extra)) {
            JSONObject jsonObject = JSON.parseObject(extra);
            if (MapUtils.isNotEmpty(jsonObject)) {
                setAttribute(id, jsonObject);
            }
        }
        if (StringUtils.isNotEmpty(source)) {
            taskAttributeService.insert(id, ETaskAttribute.SOURCE_TYPE.getAttribute(), source, false);
        }
        // 记录创建日志
        taskLogService.log(id, TaskStatusMsgEnum.CREATE_MSG);
        return id;
    }


    private void setAttribute(Long taskId, Map map) {
        String mobileAttribute = ETaskAttribute.MOBILE.getAttribute();
        String nameAttribute = ETaskAttribute.NAME.getAttribute();
        String idCardAttribute = ETaskAttribute.ID_CARD.getAttribute();
        String mobile = map.get(mobileAttribute) == null ? "" : String.valueOf(map.get(mobileAttribute));
        if (StringUtils.isNotBlank(mobile)) {
            boolean b = CommonUtils.regexMatch(mobile, "^1(3|4|5|6|7|8|9)[0-9]\\d{8}$");
            if (!b) {
                throw new ValidationException(String.format("the mobile number is illegal! mobile=%s", mobile));
            }

            taskAttributeService.insert(taskId, mobileAttribute, mobile, true);
        }
        String name = map.get(nameAttribute) == null ? "" : String.valueOf(map.get(nameAttribute));
        if (StringUtils.isNotBlank(name)) {
            taskAttributeService.insert(taskId, nameAttribute, name, true);
        }
        String idCard = map.get(idCardAttribute) == null ? "" : String.valueOf(map.get(idCardAttribute));
        if (StringUtils.isNotBlank(idCard)) {
            taskAttributeService.insert(taskId, idCardAttribute, idCard, true);
        }
    }

    @Override
    public TaskDTO getById(Long taskId) {
        Task task = getTaskById(taskId);
        if (task == null) {
            return null;
        }
        return DataConverterUtils.convert(task, TaskDTO.class);
    }


    @Override
    public boolean isTaskCompleted(Long taskId) {
        Byte status = getTaskStatusById(taskId);
        if (status != null) {
            for (Byte item : DONE_STATUSES) {
                if (item.equals(status)) {
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    public List<Long> getUserTaskIdList(Long taskId) {
        Task task = getTaskById(taskId);
        Objects.requireNonNull(task);

        List<Task> tasks = taskRepository.listTasksByAppIdAndBizTypeAndUniqueId(task.getAppId(), task.getBizType(), task.getUniqueId());

        if (CollectionUtils.isNotEmpty(tasks)) {
            return tasks.stream().map(Task::getId).collect(Collectors.toList());
        }

        return Collections.emptyList();
    }

    @Override
    public void cancelTask(Long taskId) {
        logger.info("取消任务 : taskId={} ", taskId);
        Task existTask = taskRepository.getTaskById(taskId);
        if (existTask != null && existTask.getStatus() == 0) {
            logger.info("取消正在执行任务 : taskId={} ", taskId);
            DirectiveDTO cancelDirective = new DirectiveDTO();
            cancelDirective.setTaskId(taskId);
            cancelDirective.setDirective(EDirective.TASK_CANCEL.getText());
            directiveService.process(cancelDirective);
        }
    }


    @Override
    public int updateUnfinishedTask(Task task) {
        return taskRepository.updateTaskByIdAndStatusNotIn(task, DONE_STATUSES);
    }

    private int updateUnfinishedTask(TaskDO task) {
        return taskRepository.updateTaskByIdAndStatusNotIn(task, DONE_STATUSES);
    }

    @Override
    public void updateStatusInStepById(Long id, Byte status, String stepCode) {
        TaskDO task = new TaskDO();
        task.setId(id);
        task.setStatus(status);
        task.setStepCode(stepCode);

        updateUnfinishedTask(task);
    }

    @Override
    public void updateStatusById(@Nonnull Long id, @Nonnull Byte status) {
        TaskDO task = new TaskDO();
        task.setId(id);
        task.setStatus(status);

        updateUnfinishedTask(task);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public String updateStatusIfDone(Long taskId, Byte status) {
        if (status == null || ETaskStatus.RUNNING.getStatus().equals(status)) {
            return null;
        }

        if (ETaskStatus.SUCCESS.getStatus().equals(status)) {
            updateStatusById(taskId, status);
            taskLogService.log(taskId, TaskStatusMsgEnum.SUCCESS_MSG);
            return null;
        }

        String stepCode = taskLogService.getLastErrorStepCode(taskId);
        updateStatusInStepById(taskId, status, stepCode);

        if (ETaskStatus.CANCEL.getStatus().equals(status)) {
            // 取消任务
            taskLogService.log(taskId, TaskStatusMsgEnum.CANCEL_MSG);
        } else if (ETaskStatus.FAIL.getStatus().equals(status)) {
            // 如果任务是超时导致的失败,则不记录任务失败日志了
            if (!ETaskStep.TASK_TIMEOUT.getStepCode().equals(stepCode)) {
                taskLogService.log(taskId, TaskStatusMsgEnum.FAILURE_MSG);
            }
        }
        return stepCode;
    }

    @Override
    public void updateTask(Long taskId, String accountNo, String website) {
        if (taskId == null || StringUtils.isEmpty(accountNo)) {
            return;
        }

        updateAccountNoAndWebsiteById(taskId, accountNo, website);
    }

    @Override
    public void updateAccountNoAndWebsiteIfNeed(@Nonnull Long taskId, @Nullable String accountNo, @Nullable String website) {
        String account = null;
        if (StringUtils.isNotEmpty(accountNo)) {
            Task task = getTaskById(taskId);
            Objects.requireNonNull(task);
            if (StringUtils.isEmpty(task.getAccountNo())) {
                account = accountNo;
            }
        }

        updateAccountNoAndWebsiteById(taskId, account, website);
    }

    private void updateAccountNoAndWebsiteById(@Nonnull Long id, @Nullable String accountNo, @Nullable String website) {
        String account = StringUtils.trimToNull(accountNo);
        String site = StringUtils.trimToNull(website);
        if (StringUtils.isEmpty(account) && StringUtils.isEmpty(site)) {
            return;
        }

        TaskDO task = new TaskDO();
        task.setId(id);
        task.setAccountNo(account);
        task.setWebsite(site);
        updateUnfinishedTask(task);
    }

    @Override
    public void updateAccountNoById(@Nonnull Long taskId, @Nonnull String accountNo) {
        taskRepository.updateAccountNoById(taskId, accountNo);
    }

}

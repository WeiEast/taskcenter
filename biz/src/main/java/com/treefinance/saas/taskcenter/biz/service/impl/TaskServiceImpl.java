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

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.treefinance.saas.taskcenter.biz.domain.TaskUpdateResult;
import com.treefinance.saas.taskcenter.biz.param.TaskCreateObject;
import com.treefinance.saas.taskcenter.biz.param.TaskUpdateObject;
import com.treefinance.saas.taskcenter.biz.service.MonitorService;
import com.treefinance.saas.taskcenter.biz.service.TaskLogService;
import com.treefinance.saas.taskcenter.biz.service.TaskService;
import com.treefinance.saas.taskcenter.biz.service.directive.DirectiveService;
import com.treefinance.saas.taskcenter.context.enums.EDirective;
import com.treefinance.saas.taskcenter.context.enums.ETaskAttribute;
import com.treefinance.saas.taskcenter.context.enums.ETaskStatus;
import com.treefinance.saas.taskcenter.context.enums.ETaskStep;
import com.treefinance.saas.taskcenter.context.enums.TaskStatusMsgEnum;
import com.treefinance.saas.taskcenter.dao.entity.Task;
import com.treefinance.saas.taskcenter.dao.entity.TaskAndTaskAttribute;
import com.treefinance.saas.taskcenter.dao.entity.TaskAttribute;
import com.treefinance.saas.taskcenter.dao.param.TaskAttrCompositeQuery;
import com.treefinance.saas.taskcenter.dao.param.TaskPagingQuery;
import com.treefinance.saas.taskcenter.dao.param.TaskParams;
import com.treefinance.saas.taskcenter.dao.param.TaskQuery;
import com.treefinance.saas.taskcenter.dao.repository.TaskRepository;
import com.treefinance.saas.taskcenter.dto.DirectiveDTO;
import com.treefinance.saas.taskcenter.dto.TaskDTO;
import com.treefinance.saas.taskcenter.service.TaskAttributeService;
import com.treefinance.saas.taskcenter.service.TaskLifecycleService;
import com.treefinance.saas.taskcenter.service.impl.AbstractService;
import com.treefinance.saas.taskcenter.service.param.TaskStepLogObject;
import com.treefinance.saas.taskcenter.util.SystemUtils;
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

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author haojiahong
 * @date 2018/9/21
 */
@Service
public class TaskServiceImpl extends AbstractService implements TaskService {

    private static final Byte[] DONE_STATUSES = {ETaskStatus.CANCEL.getStatus(), ETaskStatus.SUCCESS.getStatus(), ETaskStatus.FAIL.getStatus()};
    protected final Logger logger = LoggerFactory.getLogger(getClass());

    // 任务附加属性sourceId
    private final String sourceId = "sourceId";
    @Autowired
    private TaskAttributeService taskAttributeService;
    @Autowired
    private TaskLogService taskLogService;
    @Autowired
    private DirectiveService directiveService;
    @Autowired
    private TaskRepository taskRepository;
    @Autowired
    private TaskLifecycleService taskLifecycleService;
    @Autowired
    private MonitorService monitorService;

    @Override
    public List<TaskAndTaskAttribute> queryCompositeTasks(@Nonnull TaskAttrCompositeQuery query) {
        return taskRepository.queryCompositeTasks(query);
    }

    @Override
    public long countCompositeTasks(@Nonnull TaskAttrCompositeQuery query) {
        return taskRepository.countCompositeTasks(query);
    }

    @Override
    public TaskDTO getById(Long taskId) {
        Task task = getTaskById(taskId);

        return convert(task, TaskDTO.class);
    }

    @Override
    public TaskDTO getTaskandAttribute(Long taskId) {

        String[] strings = new String[] {sourceId};
        Task task = getTaskById(taskId);
        List<TaskAttribute> taskAttributeList = taskAttributeService.listAttributesByTaskIdAndInNames(taskId, strings, false);
        TaskDTO taskDTO = convert(task, TaskDTO.class);
        if (!org.springframework.util.ObjectUtils.isEmpty(taskAttributeList)) {
            Map<String, Object> resultMap = new HashMap(1);
            resultMap.put(sourceId, taskAttributeList.get(0));
            taskDTO.setAttributes(resultMap);
        }

        return taskDTO;
    }

    @Override
    public int updateUnfinishedTask(Task task) {
        return taskRepository.updateTaskByIdAndStatusNotIn(task, DONE_STATUSES);
    }

    @Override
    public void updateTask(Long taskId, String accountNo, String website) {
        if (taskId == null) {
            return;
        }

        String account = StringUtils.trim(accountNo);
        String site = StringUtils.trim(website);
        if (StringUtils.isEmpty(account) && StringUtils.isEmpty(site)) {
            return;
        }

        TaskParams task = new TaskParams();
        task.setAccountNo(account);
        task.setWebsite(site);
        updateProcessingTaskById(task, taskId);
    }

    @Override
    public Task getTaskById(@Nonnull Long taskId) {
        return taskRepository.getTaskById(taskId);
    }

    @Override
    public Byte getTaskStatusById(@Nonnull Long taskId) {
        Task task = taskRepository.getTaskById(taskId);

        return task.getStatus();
    }

    @Override
    public boolean isTaskCompleted(Long taskId) {
        Byte status = getTaskStatusById(taskId);
        return isCompleted(status);
    }

    @Override
    public Task queryCompletedTaskById(@Nonnull Long taskId) {
        Task task = taskRepository.getTaskById(taskId);

        if (isCompleted(task.getStatus())) {
            return task;
        }

        return null;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public List<Long> listTaskIdsWithSameTrigger(@Nonnull Long taskId) {
        Task task = taskRepository.getTaskById(taskId);

        return taskRepository.listTaskIdsByAppIdAndBizTypeAndUniqueId(task.getAppId(), task.getBizType(), task.getUniqueId());
    }

    @Override
    public List<Task> listRunningTasks(@Nonnull Byte saasEnv, @Nonnull Date startDate, @Nonnull Date endDate) {
        return taskRepository.listTasksByStatusAndEnvAndCreateTimeBetween(ETaskStatus.RUNNING.getStatus(), saasEnv, startDate, endDate);
    }

    @Override
    public List<Task> queryPagingTasks(@Nonnull TaskPagingQuery query) {
        return taskRepository.queryPagingTasks(query);
    }

    @Override
    public long countPagingTasks(@Nonnull TaskPagingQuery query) {
        return taskRepository.countPagingTasks(query);
    }

    @Override
    public List<Task> queryTasks(@Nonnull TaskQuery query) {
        return taskRepository.queryTasks(query);
    }

    @Override
    public long countTasks(@Nonnull TaskQuery query) {
        return taskRepository.countTasks(query);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Long createTask(@Nonnull TaskCreateObject object) {
        TaskParams params = convertStrict(object, TaskParams.class);
        Task task = taskRepository.insertTask(params);

        Long id = task.getId();
        String extra = object.getExtra();
        if (StringUtils.isNotBlank(extra)) {
            JSONObject jsonObject = JSON.parseObject(extra);
            if (MapUtils.isNotEmpty(jsonObject)) {
                setAttribute(id, jsonObject);
            }
        }

        String source = object.getSource();
        if (StringUtils.isNotEmpty(source)) {
            taskAttributeService.insert(id, ETaskAttribute.SOURCE_TYPE.getAttribute(), source, false);
        }

        // 记录创建日志
        taskLogService.log(id, TaskStatusMsgEnum.CREATE_MSG);

        return id;
    }

    @Override
    public int updateProcessingTaskById(@Nonnull TaskUpdateObject object) {
        TaskParams params = convert(object, TaskParams.class);

        return updateProcessingTaskById(params, object.getId());
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public TaskUpdateResult updateAccountNoAndWebsiteIfNeedWhenProcessing(@Nonnull Long taskId, @Nullable String accountNo, @Nullable String website) {
        String account = null;
        if (StringUtils.isNotEmpty(accountNo)) {
            Task task = getTaskById(taskId);
            if (StringUtils.isEmpty(task.getAccountNo())) {
                account = accountNo;
            }
        }

        return updateAccountNoAndWebsiteWhenProcessing(taskId, account, website);
    }

    @Override
    public TaskUpdateResult updateAccountNoAndWebsiteWhenProcessing(@Nonnull Long taskId, @Nullable String accountNo, @Nullable String website) {
        String account = StringUtils.trim(accountNo);
        boolean notUpdateAccount = StringUtils.isEmpty(account);
        String site = StringUtils.trim(website);
        boolean notUpdateWebsite = StringUtils.isEmpty(site);

        if (notUpdateAccount && notUpdateWebsite) {
            return new TaskUpdateResult(false, false);
        }

        TaskParams task = new TaskParams();
        task.setAccountNo(account);
        task.setWebsite(site);
        updateProcessingTaskById(task, taskId);

        return new TaskUpdateResult(!notUpdateAccount, !notUpdateWebsite);
    }

    @Override
    public void updateStatusWhenProcessing(@Nonnull Long id, @Nonnull Byte status) {
        TaskParams task = new TaskParams();
        task.setStatus(status);

        updateProcessingTaskById(task, id);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public String updateStatusIfDone(@Nonnull Long taskId, @Nonnull Byte status) {
        if (ETaskStatus.RUNNING.getStatus().equals(status)) {
            return null;
        }

        if (ETaskStatus.SUCCESS.getStatus().equals(status)) {
            updateStatusWhenProcessing(taskId, status);
            taskLogService.log(taskId, TaskStatusMsgEnum.SUCCESS_MSG);
            return null;
        }

        String stepCode = taskLogService.getLastErrorStepCode(taskId);
        updateStatusAndStepCodeWhenProcessing(taskId, status, stepCode);

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
    public void updateAccountNoById(@Nonnull Long taskId, @Nonnull String accountNo) {
        taskRepository.updateAccountNoById(taskId, accountNo);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void cancelTask(@Nonnull Long taskId) {
        logger.info("准备取消任务，taskId={} ", taskId);
        Task task = taskRepository.getTaskById(taskId);
        if (ETaskStatus.isRunning(task.getStatus())) {
            logger.info("正在取消任务 : taskId={} ", taskId);
            DirectiveDTO cancelDirective = new DirectiveDTO();
            cancelDirective.setTaskId(taskId);
            cancelDirective.setDirective(EDirective.TASK_CANCEL.getText());
            directiveService.process(cancelDirective);
            // 删除记录的任务活跃时间
            taskLifecycleService.deleteAliveTime(taskId);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void completeTaskAndMonitoring(@Nonnull Long taskId, @Nonnull List<TaskStepLogObject> logList) {
        if (CollectionUtils.isEmpty(logList)) {
            return;
        }

        for (TaskStepLogObject log : logList) {
            String stepMsg = log.getStepMsg();
            if (ETaskStep.TASK_SUCCESS.getText().equals(stepMsg)) {
                // 任务成功
                updateStatusWhenProcessing(taskId, ETaskStatus.SUCCESS.getStatus());
            } else if (ETaskStep.TASK_FAIL.getText().equals(stepMsg)) {
                // 任务失败
                updateStatusWhenProcessing(taskId, ETaskStatus.FAIL.getStatus());
            }

            taskLogService.insertTaskLog(taskId, stepMsg, log.getOccurTime(), log.getErrorMsg());
        }
        monitorService.sendMonitorMessage(taskId);
    }

    private boolean isCompleted(Byte status) {
        if (status != null) {
            for (Byte item : DONE_STATUSES) {
                if (item.equals(status)) {
                    return true;
                }
            }
        }

        return false;
    }

    private void setAttribute(Long taskId, Map map) {
        String mobileAttribute = ETaskAttribute.MOBILE.getAttribute();
        String nameAttribute = ETaskAttribute.NAME.getAttribute();
        String idCardAttribute = ETaskAttribute.ID_CARD.getAttribute();
        String sourceIdAttribute = ETaskAttribute.SOURCE_ID.getAttribute();


        String mobile = map.get(mobileAttribute) == null ? "" : String.valueOf(map.get(mobileAttribute));
        if (StringUtils.isNotBlank(mobile)) {
            boolean b = SystemUtils.regexMatch(mobile, "^1(3|4|5|6|7|8|9)[0-9]\\d{8}$");
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

        String sourceId = map.get(sourceIdAttribute) == null ? "" : String.valueOf(map.get(sourceIdAttribute));
        if (StringUtils.isNotBlank(sourceId)) {
            taskAttributeService.insert(taskId, sourceIdAttribute, sourceId, false);
        }
    }

    private int updateProcessingTaskById(TaskParams params, Long id) {
        return taskRepository.updateTaskByIdAndStatusNotIn(params, id, DONE_STATUSES);
    }

    private void updateStatusAndStepCodeWhenProcessing(Long id, Byte status, String stepCode) {
        TaskParams task = new TaskParams();
        task.setStatus(status);
        task.setStepCode(stepCode);

        updateProcessingTaskById(task, id);
    }
}

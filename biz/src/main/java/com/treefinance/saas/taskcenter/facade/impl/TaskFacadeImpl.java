package com.treefinance.saas.taskcenter.facade.impl;

import com.google.common.collect.Lists;
import com.treefinance.saas.taskcenter.biz.service.TaskService;
import com.treefinance.saas.taskcenter.common.exception.BusinessCheckFailException;
import com.treefinance.saas.taskcenter.common.util.DataConverterUtils;
import com.treefinance.saas.taskcenter.dao.domain.TaskCompositeQuery;
import com.treefinance.saas.taskcenter.dao.domain.TaskDO;
import com.treefinance.saas.taskcenter.dao.domain.TaskQuery;
import com.treefinance.saas.taskcenter.dao.entity.Task;
import com.treefinance.saas.taskcenter.dao.entity.TaskAndTaskAttribute;
import com.treefinance.saas.taskcenter.facade.request.TaskAndAttributeRequest;
import com.treefinance.saas.taskcenter.facade.request.TaskCreateRequest;
import com.treefinance.saas.taskcenter.facade.request.TaskRequest;
import com.treefinance.saas.taskcenter.facade.request.TaskUpdateRequest;
import com.treefinance.saas.taskcenter.facade.result.TaskAndAttributeRO;
import com.treefinance.saas.taskcenter.facade.result.TaskRO;
import com.treefinance.saas.taskcenter.facade.result.common.TaskPagingResult;
import com.treefinance.saas.taskcenter.facade.result.common.TaskResult;
import com.treefinance.saas.taskcenter.facade.service.TaskFacade;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * @author:guoguoyun
 * @date:Created in 2018/9/18上午10:24
 */
@Component("taskFacade")
public class TaskFacadeImpl implements TaskFacade {
    private static final Logger logger = LoggerFactory.getLogger(TaskFacade.class);

    @Autowired
    private TaskService taskService;


    @Override
    public TaskResult<List<TaskRO>> queryTask(TaskRequest request) {
        logger.info("条件查询任务传入的参数为{}", request.toString());
        TaskQuery query = new TaskQuery();
        query.setId(request.getId());

        List<String> appIds = request.getAppIdList();
        if (CollectionUtils.isEmpty(appIds) && request.getAppId() != null) {
            appIds = Collections.singletonList(request.getAppId());
        }
        query.setAppIds(appIds);

        List<Byte> bizTypes = request.getBizTypeList();
        if (CollectionUtils.isEmpty(bizTypes) && request.getBizType() != null) {
            bizTypes = Collections.singletonList(request.getBizType());
        }
        query.setBizTypes(bizTypes);

        query.setWebsite(request.getWebSite());
        query.setUniqueId(request.getUniqueId());
        query.setAccountNo(request.getAccountNo());
        query.setStepCode(request.getStepCode());
        query.setStatus(request.getStatus());

        Date startDate = request.getCreateTimeStart();
        Date endDate = null;
        if (startDate != null) {
            endDate = request.getCreateTimeEnd();
        }

        query.setStartDate(startDate);
        query.setEndDate(endDate);

        query.setSaasEnv(request.getSaasEnv());
        query.setOrder(request.getOrderByClause());

        List<Task> taskList = taskService.queryTasks(query);

        if (CollectionUtils.isEmpty(taskList)) {
            return TaskResult.wrapErrorResult("失败", "找不到相关数据");
        }

        List<TaskRO> taskROList = DataConverterUtils.convert(taskList, TaskRO.class);

        return TaskResult.wrapSuccessfulResult(taskROList);
    }

    @Override
    public TaskResult<Long> createTask(TaskCreateRequest request) {
        if (request == null) {
            throw new BusinessCheckFailException("-1", "请求参数不能为空");
        }
        if (StringUtils.isBlank(request.getAppId())) {
            throw new BusinessCheckFailException("-1", "appId不能为空");
        }
        if (request.getBizType() == null) {
            throw new BusinessCheckFailException("-1", "业务类型不能为空");
        }

        TaskDO taskDO = DataConverterUtils.convert(request, TaskDO.class);

        Long taskId = taskService.createTask(taskDO, request.getSource(), request.getExtra());

        return TaskResult.wrapSuccessfulResult(taskId);
    }

    @Override
    public TaskResult<Void> updateTask(Long taskId, String accountNo, String webSite) {
        taskService.updateTask(taskId, accountNo, webSite);
        return TaskResult.wrapSuccessfulResult(null);
    }

    @Override
    public TaskResult<Integer> updateUnfinishedTask(TaskUpdateRequest request) {
        Task task = DataConverterUtils.convert(request, Task.class);
        int id = taskService.updateUnfinishedTask(task);
        return TaskResult.wrapSuccessfulResult(id);
    }

    @Override
    public TaskResult<String> updateTaskStatusWithStep(Long taskId, Byte status) {
        String result = taskService.updateStatusIfDone(taskId, status);
        return TaskResult.wrapSuccessfulResult(result);
    }

    @Override
    public TaskResult<Void> cancelTask(Long taskId) {
        if (taskId == null) {
            throw new BusinessCheckFailException("-1", "任务id不能为空");
        }
        taskService.cancelTask(taskId);
        return TaskResult.wrapSuccessfulResult(null);
    }

    @Override
    public TaskResult<TaskRO> getTaskByPrimaryKey(TaskRequest request) {
        Task task = taskService.getTaskById(Objects.requireNonNull(request.getId()));
        if (Objects.isNull(task)) {
            return TaskResult.wrapErrorResult("失败", "找不到相关数据");
        }
        TaskRO taskRO = new TaskRO();
        BeanUtils.copyProperties(task, taskRO);
        return TaskResult.wrapSuccessfulResult(taskRO);
    }

    @Override
    public TaskPagingResult<TaskRO> queryTaskWithPagination(TaskRequest request) {
        logger.info("分页条件查询任务传入的参数为{}", request.toString());

        TaskQuery query = new TaskQuery();
        query.setId(request.getId());

        List<String> appIds = request.getAppIdList();
        if (CollectionUtils.isEmpty(appIds) && request.getAppId() != null) {
            appIds = Collections.singletonList(request.getAppId());
        }
        query.setAppIds(appIds);

        List<Byte> bizTypes = request.getBizTypeList();
        if (CollectionUtils.isEmpty(bizTypes) && request.getBizType() != null) {
            bizTypes = Collections.singletonList(request.getBizType());
        }
        query.setBizTypes(bizTypes);

        query.setWebsite(request.getWebSite());
        query.setUniqueId(request.getUniqueId());
        query.setAccountNo(request.getAccountNo());
        query.setStepCode(request.getStepCode());
        query.setStatus(request.getStatus());

        Date startDate = request.getCreateTimeStart();
        Date endDate = null;
        if (startDate != null) {
            endDate = request.getCreateTimeEnd();
        }
        query.setStartDate(startDate);
        query.setEndDate(endDate);

        query.setSaasEnv(request.getSaasEnv());
        query.setOrder(request.getOrderByClause());
        query.setOffset(request.getOffset());
        query.setLimit(request.getPageSize());

        List<Task> taskList = taskService.queryTasks(query);

        if (CollectionUtils.isEmpty(taskList)) {
            return TaskPagingResult.wrapErrorResult("失败", "找不到相关数据");
        }

        int count = (int)taskService.countTasks(query);

        List<TaskRO> taskROList = DataConverterUtils.convert(taskList, TaskRO.class);

        return TaskPagingResult.wrapSuccessfulResult(taskROList, count);
    }

    @Override
    public TaskPagingResult<TaskRO> queryTaskListPage(TaskRequest request) {
        TaskQuery query = new TaskQuery();
        query.setId(request.getId());
        query.setAppIds(request.getAppIdList());
        query.setBizTypes(Collections.singletonList(request.getBizType()));
        query.setUniqueId(request.getUniqueId());
        query.setAccountNo(request.getAccountNo());
        query.setStartDate(request.getStartDate());
        query.setEndDate(DateUtils.addSeconds(request.getEndDate(), 24 * 60 * 60 - 1));
        query.setOrder("lastUpdateTime desc");
        query.setOffset(request.getOffset());
        query.setLimit(request.getPageSize());

        long count = taskService.countTasks(query);
        if (count <= 0) {
            return TaskPagingResult.wrapSuccessfulResult(null, 0);
        }

        List<Task> taskList = taskService.queryTasks(query);

        List<TaskRO> taskROList = DataConverterUtils.convert(taskList, TaskRO.class);

        return TaskPagingResult.wrapSuccessfulResult(taskROList, (int)count);
    }


    @Override
    public TaskResult<List<TaskRO>> queryTaskList(TaskRequest request) {
        TaskQuery query = new TaskQuery();
        query.setId(request.getId());
        query.setAppIds(request.getAppIdList());
        query.setBizTypes(Collections.singletonList(request.getBizType()));
        query.setUniqueId(request.getUniqueId());
        query.setAccountNo(request.getAccountNo());

        List<Task> taskList = taskService.queryTasks(query);

        List<TaskRO> taskROList = DataConverterUtils.convert(taskList, TaskRO.class);

        return TaskResult.wrapSuccessfulResult(taskROList);

    }

    @Override
    public TaskPagingResult<TaskAndAttributeRO> queryTaskAndTaskAttribute(TaskAndAttributeRequest request) {
        TaskCompositeQuery query = new TaskCompositeQuery();
        query.setAppId(request.getAppId());

        List<Byte> bizTypes = request.getBizTypeList();
        if (CollectionUtils.isEmpty(bizTypes) && request.getBizType() != null) {
            bizTypes = Collections.singletonList(request.getBizType());
        }
        query.setBizTypes(bizTypes);

        String webSite = request.getWebSite();
        query.setWebsite(webSite);

        if (request.getSaasEnv() != null && request.getSaasEnv() != 0) {
            query.setSaasEnv(request.getSaasEnv());
        }
        query.setStatus(request.getStatus());
        query.setStartDate(request.getStartTime());
        query.setEndDate(request.getEndTime());

        query.setName(request.getName());
        String value = null;
        if (StringUtils.isNotBlank(webSite)) {
            value = request.getValue();
        }
        query.setValue(value);

        query.setOrder("createTime desc");
        query.setOffset(request.getStart());
        query.setLimit(request.getLimit());

        long total = taskService.countCompositeTasks(query);
        if (total <= 0) {
            return TaskPagingResult.wrapSuccessfulResult(null, 0);
        }
        List<TaskAndTaskAttribute> list = taskService.queryCompositeTasks(query);

        List<TaskAndAttributeRO> taskList = DataConverterUtils.convert(list, TaskAndAttributeRO.class);

        return TaskPagingResult.wrapSuccessfulResult(taskList, (int)total);
    }

    @Override
    public TaskResult<List<Long>> getUserTaskIdList(Long taskId) {
        List<Long> taskIdList = taskService.getUserTaskIdList(taskId);
        return TaskResult.wrapSuccessfulResult(taskIdList);
    }

    @Override
    public TaskResult<List<TaskRO>> selectRecentRunningTaskList(Byte saasEnv, Date startTime, Date endTime) {
        List<Task> taskList = taskService.listRunningTasksByEnvAndCreateTimeBetween(saasEnv, endTime, startTime);
        if (CollectionUtils.isEmpty(taskList)) {
            return TaskResult.wrapSuccessfulResult(Lists.newArrayList());
        }
        List<TaskRO> taskROList = DataConverterUtils.convert(taskList, TaskRO.class);
        return TaskResult.wrapSuccessfulResult(taskROList);
    }

}

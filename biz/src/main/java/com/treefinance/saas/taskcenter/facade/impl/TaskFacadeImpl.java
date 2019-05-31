package com.treefinance.saas.taskcenter.facade.impl;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.treefinance.saas.taskcenter.biz.service.TaskService;
import com.treefinance.saas.taskcenter.dao.entity.Task;
import com.treefinance.saas.taskcenter.dao.entity.TaskAndTaskAttribute;
import com.treefinance.saas.taskcenter.dao.param.TaskAttrCompositeQuery;
import com.treefinance.saas.taskcenter.dao.param.TaskPagingQuery;
import com.treefinance.saas.taskcenter.dao.param.TaskQuery;
import com.treefinance.saas.taskcenter.facade.request.CompositeTaskAttrPagingQueryRequest;
import com.treefinance.saas.taskcenter.facade.request.TaskAndAttributeRequest;
import com.treefinance.saas.taskcenter.facade.request.TaskCreateRequest;
import com.treefinance.saas.taskcenter.facade.request.TaskPagingQueryRequest;
import com.treefinance.saas.taskcenter.facade.request.TaskQueryRequest;
import com.treefinance.saas.taskcenter.facade.request.TaskRequest;
import com.treefinance.saas.taskcenter.facade.request.TaskStepLogRequest;
import com.treefinance.saas.taskcenter.facade.request.TaskUpdateRequest;
import com.treefinance.saas.taskcenter.facade.response.TaskResponse;
import com.treefinance.saas.taskcenter.facade.result.CompositeTaskAttrDTO;
import com.treefinance.saas.taskcenter.facade.result.PagingDataSet;
import com.treefinance.saas.taskcenter.facade.result.SimpleTaskDTO;
import com.treefinance.saas.taskcenter.facade.result.TaskAndAttributeRO;
import com.treefinance.saas.taskcenter.facade.result.TaskRO;
import com.treefinance.saas.taskcenter.facade.result.TaskUpdateStatusDTO;
import com.treefinance.saas.taskcenter.facade.result.TaskingMerchantBaseDTO;
import com.treefinance.saas.taskcenter.facade.result.common.TaskPagingResult;
import com.treefinance.saas.taskcenter.facade.result.common.TaskResult;
import com.treefinance.saas.taskcenter.facade.service.TaskFacade;
import com.treefinance.saas.taskcenter.facade.validate.Preconditions;
import com.treefinance.saas.taskcenter.interation.manager.MerchantInfoManager;
import com.treefinance.saas.taskcenter.interation.manager.domain.MerchantBaseBO;
import com.treefinance.saas.taskcenter.service.domain.TaskUpdateResult;
import com.treefinance.saas.taskcenter.service.param.TaskCreateObject;
import com.treefinance.saas.taskcenter.service.param.TaskStepLogObject;
import com.treefinance.saas.taskcenter.service.param.TaskUpdateObject;
import com.treefinance.toolkit.util.DateUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author:guoguoyun
 * @date:Created in 2018/9/18上午10:24
 */
@Component("taskFacade")
public class TaskFacadeImpl extends AbstractFacade implements TaskFacade {

    @Autowired
    private TaskService taskService;
    @Autowired
    private MerchantInfoManager merchantInfoManager;

    @Override
    public TaskResult<List<TaskRO>> queryTask(TaskRequest request) {
        logger.info("条件查询任务传入的参数为{}", request);
        TaskPagingQuery query = new TaskPagingQuery();
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
        query.setStartDate(startDate);
        if (startDate != null) {
            query.setEndDate(request.getCreateTimeEnd());
        }

        query.setSaasEnv(request.getSaasEnv());
        query.setOrder(request.getOrderByClause());

        List<Task> taskList = taskService.queryPagingTasks(query);

        if (CollectionUtils.isEmpty(taskList)) {
            return TaskResult.wrapErrorResult("失败", "找不到相关数据");
        }

        List<TaskRO> taskROList = convert(taskList, TaskRO.class);

        return TaskResult.wrapSuccessfulResult(taskROList);
    }

    @Override
    public TaskPagingResult<TaskRO> queryTaskWithPagination(TaskRequest request) {
        logger.info("分页条件查询任务传入的参数为{}", request);

        TaskPagingQuery query = new TaskPagingQuery();
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
        query.setStartDate(startDate);
        if (startDate != null) {
            query.setEndDate(request.getCreateTimeEnd());
        }

        query.setSaasEnv(request.getSaasEnv());
        query.setOrder(request.getOrderByClause());
        query.setOffset(request.getOffset());
        query.setLimit(request.getPageSize());

        List<Task> taskList = taskService.queryPagingTasks(query);

        if (CollectionUtils.isEmpty(taskList)) {
            return TaskPagingResult.wrapErrorResult("失败", "找不到相关数据");
        }

        int count = (int)taskService.countPagingTasks(query);

        List<TaskRO> taskROList = convert(taskList, TaskRO.class);

        return TaskPagingResult.wrapSuccessfulResult(taskROList, count);
    }

    @Override
    public TaskResult<Void> updateTask(Long taskId, String accountNo, String webSite) {
        taskService.updateTask(taskId, accountNo, webSite);
        return TaskResult.wrapSuccessfulResult(null);
    }

    @Override
    public TaskResult<Integer> updateUnfinishedTask(TaskUpdateRequest request) {
        Task task = convert(request, Task.class);
        int id = taskService.updateUnfinishedTask(task);
        return TaskResult.wrapSuccessfulResult(id);
    }

    @Override
    public TaskResult<String> updateTaskStatusWithStep(Long taskId, Byte status) {
        String result = taskService.updateStatusIfDone(taskId, status);
        return TaskResult.wrapSuccessfulResult(result);
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
    public TaskPagingResult<TaskRO> queryTaskListPage(TaskRequest request) {
        TaskPagingQuery query = new TaskPagingQuery();
        query.setId(request.getId());
        query.setAppIds(request.getAppIdList());
        query.setBizTypes(Collections.singletonList(request.getBizType()));
        query.setUniqueId(request.getUniqueId());
        query.setAccountNo(request.getAccountNo());
        query.setStartDate(request.getStartDate());
        query.setEndDate(DateUtils.getEndTimeOfDay(request.getEndDate()));
        query.setOrder("lastUpdateTime desc");
        query.setOffset(request.getOffset());
        query.setLimit(request.getPageSize());

        long count = taskService.countPagingTasks(query);
        if (count <= 0) {
            return TaskPagingResult.wrapSuccessfulResult(null, 0);
        }

        List<Task> taskList = taskService.queryPagingTasks(query);

        List<TaskRO> taskROList = convert(taskList, TaskRO.class);

        return TaskPagingResult.wrapSuccessfulResult(taskROList, (int)count);
    }

    @Override
    public TaskResult<List<TaskRO>> queryTaskList(TaskRequest request) {
        TaskPagingQuery query = new TaskPagingQuery();
        query.setId(request.getId());
        query.setAppIds(request.getAppIdList());
        query.setBizTypes(Collections.singletonList(request.getBizType()));
        query.setUniqueId(request.getUniqueId());
        query.setAccountNo(request.getAccountNo());

        List<Task> taskList = taskService.queryPagingTasks(query);

        List<TaskRO> taskROList = convert(taskList, TaskRO.class);

        return TaskResult.wrapSuccessfulResult(taskROList);

    }

    @Override
    public TaskPagingResult<TaskAndAttributeRO> queryTaskAndTaskAttribute(TaskAndAttributeRequest request) {
        TaskAttrCompositeQuery query = new TaskAttrCompositeQuery();
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

        query.setAttrName(request.getName());
        String value = null;
        if (StringUtils.isNotBlank(webSite)) {
            value = request.getValue();
        }
        query.setAttrValue(value);

        query.setOrder("createTime desc");
        query.setOffset(request.getStart());
        query.setLimit(request.getLimit());

        long total = taskService.countCompositeTasks(query);
        if (total <= 0) {
            return TaskPagingResult.wrapSuccessfulResult(null, 0);
        }
        List<TaskAndTaskAttribute> list = taskService.queryCompositeTasks(query);

        List<TaskAndAttributeRO> taskList = convert(list, TaskAndAttributeRO.class);

        return TaskPagingResult.wrapSuccessfulResult(taskList, (int)total);
    }

    @Override
    public TaskResult<List<Long>> getUserTaskIdList(Long taskId) {
        List<Long> taskIdList = taskService.listTaskIdsWithSameTrigger(taskId);
        return TaskResult.wrapSuccessfulResult(taskIdList);
    }

    @Override
    public TaskResult<List<TaskRO>> selectRecentRunningTaskList(Byte saasEnv, Date startTime, Date endTime) {
        List<Task> taskList = taskService.listRunningTasks(saasEnv, endTime, startTime);
        if (CollectionUtils.isEmpty(taskList)) {
            return TaskResult.wrapSuccessfulResult(Lists.newArrayList());
        }
        List<TaskRO> taskROList = convert(taskList, TaskRO.class);
        return TaskResult.wrapSuccessfulResult(taskROList);
    }

    @Override
    public TaskResponse<SimpleTaskDTO> getTaskById(Long id) {
        Preconditions.notNull("id", id);
        Task task = taskService.getTaskById(id);

        SimpleTaskDTO dto = convertStrict(task, SimpleTaskDTO.class);
        return TaskResponse.success(dto);
    }

    @Override
    public TaskResponse<List<SimpleTaskDTO>> listRunningTasks(Byte saasEnv, Date startTime, Date endTime) {
        Preconditions.notNull("saasEnv", saasEnv);
        Preconditions.notNull("startTime", startTime);
        Preconditions.notNull("endTime", endTime);
        Preconditions.isFalse(startTime.after(endTime), "参数endTime必须大于startTime!");

        List<Task> taskList = taskService.listRunningTasks(saasEnv, startTime, endTime);

        List<SimpleTaskDTO> list = convert(taskList, SimpleTaskDTO.class);

        return TaskResponse.success(list);
    }

    @Override
    public TaskResponse<List<Long>> listTaskIdsWithSameTrigger(Long taskId) {
        Preconditions.notNull("taskId", taskId);

        List<Long> taskIds = taskService.listTaskIdsWithSameTrigger(taskId);

        return TaskResponse.success(taskIds);
    }

    @Override
    public TaskResponse<SimpleTaskDTO> queryCompletedTaskById(Long taskId) {
        Preconditions.notNull("taskId", taskId);

        Task task = taskService.queryCompletedTaskById(taskId);

        SimpleTaskDTO dto = convertStrict(task, SimpleTaskDTO.class);

        return TaskResponse.success(dto);
    }

    @Override
    public TaskResponse<List<SimpleTaskDTO>> queryTasks(TaskQueryRequest request) {
        logger.info("分页条件查询任务传入的参数为{}", request);
        Preconditions.notNull("request", request);

        TaskQuery query;
        if (request instanceof TaskPagingQueryRequest) {
            query = new TaskPagingQuery(((TaskPagingQueryRequest)request).getOffset(), ((TaskPagingQueryRequest)request).getPageSize());
        } else {
            query = new TaskQuery();
        }
        query.setAppIds(request.getAppIds());
        query.setBizTypes(request.getBizTypes());
        query.setWebsite(request.getWebsite());
        query.setUniqueId(request.getUniqueId());
        query.setAccountNo(request.getAccountNo());
        query.setStepCode(request.getStepCode());
        query.setStatus(request.getStatus());
        query.setStartDate(request.getStartDate());
        query.setEndDate(request.getEndDate());
        query.setSaasEnv(request.getSaasEnv());
        query.setOrder(request.getOrder());

        List<Task> tasks = taskService.queryTasks(query);
        List<SimpleTaskDTO> list = convert(tasks, SimpleTaskDTO.class);

        return TaskResponse.success(list);
    }

    @Override
    public TaskResponse<PagingDataSet<SimpleTaskDTO>> queryPagingTasks(TaskPagingQueryRequest request) {
        logger.info("分页条件查询任务传入的参数为{}", request);
        Preconditions.notNull("request", request);

        TaskPagingQuery query = new TaskPagingQuery();
        query.setAppIds(request.getAppIds());
        query.setBizTypes(request.getBizTypes());
        query.setWebsite(request.getWebsite());
        query.setUniqueId(request.getUniqueId());
        query.setAccountNo(request.getAccountNo());
        query.setStepCode(request.getStepCode());
        query.setStatus(request.getStatus());
        query.setStartDate(request.getStartDate());
        query.setEndDate(request.getEndDate());
        query.setSaasEnv(request.getSaasEnv());
        query.setOrder(request.getOrder());
        query.setOffset(request.getOffset());
        query.setLimit(request.getPageSize());

        long count = taskService.countTasks(query);
        List<SimpleTaskDTO> list;
        if (count > 0) {
            List<Task> tasks = taskService.queryTasks(query);
            list = convert(tasks, SimpleTaskDTO.class);
        } else {
            list = Collections.emptyList();
        }

        PagingDataSet<SimpleTaskDTO> pagination = new PagingDataSet<>(list, count);

        return TaskResponse.success(pagination);
    }

    @Override
    public TaskResponse<PagingDataSet<CompositeTaskAttrDTO>> queryPagingCompositeTaskAttrs(CompositeTaskAttrPagingQueryRequest request) {
        logger.info("测试request参数,request={}", JSON.toJSONString(request));
        TaskAttrCompositeQuery query = new TaskAttrCompositeQuery();
        query.setAppId(request.getAppId());
        query.setBizTypes(request.getBizTypes());
        query.setWebsite(request.getWebsite());
        if (request.getSaasEnv() != null && request.getSaasEnv() != 0) {
            query.setSaasEnv(request.getSaasEnv());
        }
        query.setStatus(request.getStatus());
        query.setStartDate(request.getStartDate());
        query.setEndDate(request.getEndDate());

        query.setAttrName(request.getAttrName());
        if (StringUtils.isNotBlank(request.getWebsite())) {
            query.setAttrValue(request.getAttrValue());
        }

        query.setOrder(request.getOrder());
        query.setOffset(request.getOffset());
        query.setLimit(request.getPageSize());

        long count = taskService.countCompositeTasks(query);
        List<CompositeTaskAttrDTO> list;
        if (count > 0) {
            List<TaskAndTaskAttribute> taskAttrs = taskService.queryCompositeTasks(query);
            if (CollectionUtils.isNotEmpty(taskAttrs)) {
                list = taskAttrs.stream().map(taskAttr -> {
                    CompositeTaskAttrDTO dto = new CompositeTaskAttrDTO();
                    dto.setId(taskAttr.getId());
                    dto.setUniqueId(taskAttr.getUniqueId());
                    dto.setAppId(taskAttr.getAppId());
                    dto.setAccountNo(taskAttr.getAccountNo());
                    dto.setWebSite(taskAttr.getWebSite());
                    dto.setBizType(taskAttr.getBizType());
                    dto.setStatus(taskAttr.getStatus());
                    dto.setStepCode(taskAttr.getStepCode());
                    dto.setCreateTime(taskAttr.getCreateTime());
                    dto.setLastUpdateTime(taskAttr.getLastUpdateTime());
                    dto.setAttrName(taskAttr.getName());
                    dto.setAttrValue(taskAttr.getValue());

                    return dto;
                }).collect(Collectors.toList());
            } else {
                list = Collections.emptyList();
            }
        } else {
            list = Collections.emptyList();
        }

        PagingDataSet<CompositeTaskAttrDTO> pagination = new PagingDataSet<>(list, count);

        return TaskResponse.success(pagination);
    }

    @Override
    public TaskResponse<Long> createTask(TaskCreateRequest request) {
        Preconditions.notNull("request", request);
        Preconditions.notBlank("request.appId", request.getAppId());
        Preconditions.notNull("request.bizType", request.getBizType());

        TaskCreateObject params = convertStrict(request, TaskCreateObject.class);

        Long taskId = taskService.createTask(params);

        return TaskResponse.success(taskId);
    }

    @Override
    public TaskResponse<Integer> updateProcessingTaskById(TaskUpdateRequest request) {
        Preconditions.notNull("request", request);
        Preconditions.notNull("request.id", request.getId());

        TaskUpdateObject params = convertStrict(request, TaskUpdateObject.class);

        int num = taskService.updateProcessingTaskById(params);

        return TaskResponse.success(num);
    }

    @Override
    public TaskResponse<TaskUpdateStatusDTO> updateAccountNoAndWebsiteIfNeedWhenProcessing(Long taskId, String accountNo, String website) {
        Preconditions.notNull("taskId", taskId);

        TaskUpdateResult result = taskService.updateAccountNoAndWebsiteIfNeedWhenProcessing(taskId, accountNo, website);

        TaskUpdateStatusDTO status = convertStrict(result, TaskUpdateStatusDTO.class);

        return TaskResponse.success(status);
    }

    @Override
    public TaskResponse<TaskUpdateStatusDTO> updateAccountNoAndWebsiteWhenProcessing(Long taskId, String accountNo, String website) {
        Preconditions.notNull("taskId", taskId);

        TaskUpdateResult result = taskService.updateAccountNoAndWebsiteWhenProcessing(taskId, accountNo, website);

        TaskUpdateStatusDTO status = convertStrict(result, TaskUpdateStatusDTO.class);

        return TaskResponse.success(status);
    }

    @Override
    public TaskResponse<String> updateStatusIfDone(Long taskId, Byte status) {
        Preconditions.notNull("taskId", taskId);
        Preconditions.notNull("status", status);

        String result = taskService.updateStatusIfDone(taskId, status);

        return TaskResponse.success(result);
    }

    @Override
    public TaskResponse<Void> cancelTask(Long taskId) {
        Preconditions.notNull("taskId", taskId);

        taskService.cancelTask(taskId);

        return TaskResponse.success(null);
    }

    @Override
    public TaskResponse<Void> completeTaskAndMonitoring(Long taskId, List<TaskStepLogRequest> logList) {
        Preconditions.notNull("taskId", taskId);
        if (CollectionUtils.isNotEmpty(logList)) {
            List<TaskStepLogObject> logs = convert(logList, TaskStepLogObject.class);

            taskService.completeTaskAndMonitoring(taskId, logs);
        }

        return TaskResponse.success(null);
    }

    @Override
    public TaskResponse<TaskingMerchantBaseDTO> queryTaskingMerchantByTaskId(@NotNull Long taskId) {
        Preconditions.notNull("taskId", taskId);
        Task task = taskService.getTaskById(taskId);

        MerchantBaseBO merchantBase = merchantInfoManager.getMerchantBaseByAppId(task.getAppId());

        TaskingMerchantBaseDTO dto = this.convertStrict(merchantBase, TaskingMerchantBaseDTO.class);
        dto.setUniqueId(task.getUniqueId());
        logger.info("通过taskId={}查询商户基本信息result={}", taskId, JSON.toJSONString(dto));

        return TaskResponse.success(dto);
    }
}

package com.treefinance.saas.taskcenter.facade.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.treefinance.saas.taskcenter.biz.service.TaskService;
import com.treefinance.saas.taskcenter.biz.utils.DataConverterUtils;
import com.treefinance.saas.taskcenter.common.enums.ETaskStatus;
import com.treefinance.saas.taskcenter.common.exception.BusinessCheckFailException;
import com.treefinance.saas.taskcenter.dao.entity.Task;
import com.treefinance.saas.taskcenter.dao.entity.TaskAndTaskAttribute;
import com.treefinance.saas.taskcenter.dao.entity.TaskCriteria;
import com.treefinance.saas.taskcenter.dao.mapper.TaskAndTaskAttributeMapper;
import com.treefinance.saas.taskcenter.dao.mapper.TaskMapper;
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
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author:guoguoyun
 * @date:Created in 2018/9/18上午10:24
 */
@Component("taskFacade")
public class TaskFacadeImpl implements TaskFacade {
    private static final Logger logger = LoggerFactory.getLogger(TaskFacade.class);

    @Autowired
    private TaskMapper taskMapper;
    @Autowired
    private TaskAndTaskAttributeMapper taskAndTaskAttributeMapper;
    @Autowired
    private TaskService taskService;


    @Override
    public TaskResult<List<TaskRO>> queryTask(TaskRequest taskRequest) {

        logger.info("条件查询任务传入的参数为{}", taskRequest.toString());
        TaskCriteria criteria = new TaskCriteria();

        if (StringUtils.isNotEmpty(taskRequest.getOrderByClause())) {
            criteria.setOrderByClause(taskRequest.getOrderByClause());

        }
        TaskCriteria.Criteria innerCriteria = criteria.createCriteria();

        if (taskRequest.getId() != null) {
            innerCriteria.andIdEqualTo(taskRequest.getId());
        }
        if (taskRequest.getBizType() != null) {
            innerCriteria.andBizTypeEqualTo(taskRequest.getBizType());
        }
        if (taskRequest.getBizTypeList() != null) {
            innerCriteria.andBizTypeIn(taskRequest.getBizTypeList());
        }
        if (taskRequest.getSaasEnv() != null) {
            innerCriteria.andSaasEnvEqualTo(taskRequest.getSaasEnv());
        }
        if (taskRequest.getStatus() != null) {
            innerCriteria.andStatusEqualTo(taskRequest.getStatus());
        }
        if (taskRequest.getCreateTimeStart() != null) {
            innerCriteria.andCreateTimeGreaterThanOrEqualTo(taskRequest.getCreateTimeStart());
            innerCriteria.andCreateTimeLessThanOrEqualTo(taskRequest.getCreateTimeEnd());
        }
        if (StringUtils.isNotEmpty(taskRequest.getAccountNo())) {
            innerCriteria.andAccountNoEqualTo(taskRequest.getAccountNo());
        }
        if (StringUtils.isNotEmpty(taskRequest.getAppId())) {
            innerCriteria.andAppIdEqualTo(taskRequest.getAppId());
        }
        if (StringUtils.isNotEmpty(taskRequest.getStepCode())) {
            innerCriteria.andStepCodeEqualTo(taskRequest.getStepCode());
        }
        if (StringUtils.isNotEmpty(taskRequest.getWebSite())) {
            innerCriteria.andWebSiteEqualTo(taskRequest.getWebSite());
        }
        if (StringUtils.isNotEmpty(taskRequest.getUniqueId())) {
            innerCriteria.andUniqueIdEqualTo(taskRequest.getUniqueId());
        }


        List<Task> taskList = taskMapper.selectByExample(criteria);
        if (CollectionUtils.isEmpty(taskList)) {

            return TaskResult.wrapErrorResult("失败", "找不到相关数据");
        }
        List<TaskRO> taskROList = DataConverterUtils.convert(taskList, TaskRO.class);

        return TaskResult.wrapSuccessfulResult(taskROList);


    }

    @Override
    public TaskResult<Long> createTask(TaskCreateRequest taskCreateRequest) {
        if (taskCreateRequest == null) {
            throw new BusinessCheckFailException("-1", "请求参数不能为空");
        }
        if (StringUtils.isBlank(taskCreateRequest.getAppId())) {
            throw new BusinessCheckFailException("-1", "appId不能为空");
        }
        if (taskCreateRequest.getBizType() == null) {
            throw new BusinessCheckFailException("-1", "业务类型不能为空");
        }
        Long taskId = taskService.createTask(taskCreateRequest);
        return TaskResult.wrapSuccessfulResult(taskId);
    }

    @Override
    public TaskResult<Void> updateTask(Long taskId, String accountNo, String webSite) {
        taskService.updateTask(taskId, accountNo, webSite);
        return TaskResult.wrapSuccessfulResult(null);
    }

    @Override
    public TaskResult<Integer> updateUnfinishedTask(TaskUpdateRequest taskRequest) {
        Task task = DataConverterUtils.convert(taskRequest, Task.class);
        int id = taskService.updateUnfinishedTask(task);
        return TaskResult.wrapSuccessfulResult(id);
    }

    @Override
    public TaskResult<String> failTaskWithStep(Long taskId) {
        String result = taskService.failTaskWithStep(taskId);
        return TaskResult.wrapSuccessfulResult(result);
    }

    @Override
    public TaskResult<String> cancelTaskWithStep(Long taskId) {
        String result = taskService.cancelTaskWithStep(taskId);
        return TaskResult.wrapSuccessfulResult(result);
    }

    @Override
    public TaskResult<String> updateTaskStatusWithStep(Long taskId, Byte status) {
        String result = taskService.updateTaskStatusWithStep(taskId, status);
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
    public TaskResult<TaskRO> getTaskByPrimaryKey(TaskRequest taskRequest) {
        if (taskRequest.getId() == null) {
            logger.error("传入的id为空");
        }

        Task task = taskMapper.selectByPrimaryKey(taskRequest.getId());
        if (Objects.isNull(task)) {

            return TaskResult.wrapErrorResult("失败", "找不到相关数据");
        }
        TaskRO taskRO = new TaskRO();
        BeanUtils.copyProperties(task, taskRO);
        return TaskResult.wrapSuccessfulResult(taskRO);
    }

    @Override
    public TaskPagingResult<TaskRO> queryTaskWithPagination(TaskRequest taskRequest) {
        logger.info("分页条件查询任务传入的参数为{}", taskRequest.toString());

        TaskCriteria criteria = new TaskCriteria();

        if (StringUtils.isNotEmpty(taskRequest.getOrderByClause())) {
            criteria.setOrderByClause(taskRequest.getOrderByClause());

        }

        criteria.setLimit(taskRequest.getPageSize());
        criteria.setOffset(taskRequest.getOffset());
        TaskCriteria.Criteria innerCriteria = criteria.createCriteria();

        if (taskRequest.getId() != null) {
            innerCriteria.andIdEqualTo(taskRequest.getId());
        }
        if (taskRequest.getBizType() != null) {
            innerCriteria.andBizTypeEqualTo(taskRequest.getBizType());
        }
        if (taskRequest.getBizTypeList() != null) {
            innerCriteria.andBizTypeIn(taskRequest.getBizTypeList());
        }
        if (taskRequest.getSaasEnv() != null) {
            innerCriteria.andSaasEnvEqualTo(taskRequest.getSaasEnv());
        }
        if (taskRequest.getStatus() != null) {
            innerCriteria.andStatusEqualTo(taskRequest.getStatus());
        }
        if (taskRequest.getCreateTimeStart() != null) {
            innerCriteria.andCreateTimeGreaterThanOrEqualTo(taskRequest.getCreateTimeStart());
            innerCriteria.andCreateTimeLessThanOrEqualTo(taskRequest.getCreateTimeEnd());
        }
        if (StringUtils.isNotEmpty(taskRequest.getAccountNo())) {
            innerCriteria.andAccountNoEqualTo(taskRequest.getAccountNo());
        }
        if (StringUtils.isNotEmpty(taskRequest.getAppId())) {
            innerCriteria.andAppIdEqualTo(taskRequest.getAppId());
        }
        if (StringUtils.isNotEmpty(taskRequest.getStepCode())) {
            innerCriteria.andStepCodeEqualTo(taskRequest.getStepCode());
        }
        if (StringUtils.isNotEmpty(taskRequest.getWebSite())) {
            innerCriteria.andWebSiteEqualTo(taskRequest.getWebSite());
        }
        if (StringUtils.isNotEmpty(taskRequest.getUniqueId())) {
            innerCriteria.andUniqueIdEqualTo(taskRequest.getUniqueId());
        }

        int count = (int) taskMapper.countByExample(criteria);

        List<Task> taskList = taskMapper.selectPaginationByExample(criteria);
        if (CollectionUtils.isEmpty(taskList)) {

            return TaskPagingResult.wrapErrorResult("失败", "找不到相关数据");
        }
        List<TaskRO> taskROList = DataConverterUtils.convert(taskList, TaskRO.class);

        return TaskPagingResult.wrapSuccessfulResult(taskROList, count);

    }

    @Override
    public TaskPagingResult<TaskRO> queryTaskListPage(TaskRequest request) {
        TaskCriteria taskCriteria = new TaskCriteria();
        taskCriteria.setOffset(request.getOffset());
        taskCriteria.setLimit(request.getPageSize());
        taskCriteria.setOrderByClause("lastUpdateTime desc");

        TaskCriteria.Criteria criteria = taskCriteria.createCriteria();
        if (request.getId() != null) {
            criteria.andIdEqualTo(request.getId());
        }
        if (StringUtils.isNotBlank(request.getUniqueId())) {
            criteria.andUniqueIdEqualTo(request.getUniqueId());
        }
        if (StringUtils.isNotBlank(request.getAccountNo())) {
            criteria.andAccountNoEqualTo(request.getAccountNo());
        }
        if (!CollectionUtils.isEmpty(request.getAppIdList())) {
            criteria.andAppIdIn(request.getAppIdList());
        }

        criteria.andCreateTimeGreaterThanOrEqualTo(request.getStartDate());
        // +23:59:59
        criteria.andCreateTimeLessThanOrEqualTo(DateUtils.addSeconds(request.getEndDate(), 24 * 60 * 60 - 1));
        criteria.andBizTypeEqualTo(request.getBizType());
        Long count = taskMapper.countByExample(taskCriteria);
        if (count <= 0) {
            return TaskPagingResult.wrapSuccessfulResult(null, 0);
        }
        List<Task> taskList = taskMapper.selectPaginationByExample(taskCriteria);

        List<TaskRO> taskROList = DataConverterUtils.convert(taskList, TaskRO.class);

        return TaskPagingResult.wrapSuccessfulResult(taskROList, count.intValue());
    }


    @Override
    public TaskResult<List<TaskRO>> queryTaskList(TaskRequest request) {
        TaskCriteria taskCriteria = new TaskCriteria();
        TaskCriteria.Criteria innerTaskCriteria = taskCriteria.createCriteria();
        if (request.getBizType() != null) {
            innerTaskCriteria.andBizTypeEqualTo(request.getBizType());
        }
        if (StringUtils.isNotBlank(request.getUniqueId())) {
            innerTaskCriteria.andUniqueIdEqualTo(request.getUniqueId());
        }
        if (StringUtils.isNotBlank(request.getAccountNo())) {
            innerTaskCriteria.andAccountNoEqualTo(request.getAccountNo());
        }
        if (request.getId() != null) {
            innerTaskCriteria.andIdEqualTo(request.getId());
        }
        if (!CollectionUtils.isEmpty(request.getAppIdList())) {
            innerTaskCriteria.andAppIdIn(request.getAppIdList());
        }
        List<Task> taskList = taskMapper.selectByExample(taskCriteria);

        List<TaskRO> taskROList = DataConverterUtils.convert(taskList, TaskRO.class);

        return TaskResult.wrapSuccessfulResult(taskROList);

    }

    @Override
    public TaskPagingResult<TaskAndAttributeRO> queryTaskAndTaskAttribute(TaskAndAttributeRequest request) {
        Map<String, Object> map = Maps.newHashMap();

        map.put("appId", request.getAppId());
        if (request.getSaasEnv() != 0) {
            map.put("saasEnv", request.getSaasEnv());
        }
        map.put("name", request.getName());
//        if (request.getStatType() == 2) {
//            //失败的任务
//            map.put("status", 3);
//        } else if (request.getStatType() == 3) {
//            //取消的任务
//            map.put("status", 1);
//        } else if (request.getStatType() == 1) {
//            //成功的任务
//            map.put("status", 2);
//        } else {
//            throw new IllegalArgumentException("statType参数有误");
//        }
        map.put("status", request.getStatus());

        if (request.getBizType() == null) {
            map.put("bizTypeList", request.getBizTypeList());
        } else {
            map.put("bizType", request.getBizType());
        }

//        if (request.getBizType() == 0) {
//            MerchantResult<List<AppBizTypeResult>> merchantResult = appBizTypeFacade.queryAllAppBizType(new BaseRequest());
//            List<AppBizType> list = DataConverterUtils.convert(merchantResult.getData(), AppBizType.class);
//            List<Byte> bizTypeList = list.stream().map(AppBizType::getBizType).collect(Collectors.toList());
//            map.put("bizTypeList", bizTypeList);
//        } else {
//            map.put("bizType", request.getBizType());
//        }


        if (StringUtils.isNotBlank(request.getWebSite())) {
            map.put("webSite", request.getWebSite());
            map.put("value", request.getValue());
        }

//        if (request.getStartTime() != null && request.getEndTime() != null) {
//            map.put("startTime", request.getStartTime());
//            map.put("endTime", request.getEndTime());
//        } else if (request.getDate() != null) {
//            map.put("startTime", DateUtils.getTodayBeginDate(request.getDate()));
//            map.put("endTime", DateUtils.getTomorrowBeginDate(request.getDate()));
//        }

        map.put("startTime", request.getStartTime());
        map.put("endTime", request.getEndTime());


        map.put("start", request.getStart());
        map.put("limit", request.getLimit());
        map.put("orderStr", "createTime desc");

        Long total = taskAndTaskAttributeMapper.countByExample(map);
        if (total <= 0) {
            return TaskPagingResult.wrapSuccessfulResult(null, 0);
        }
        List<TaskAndTaskAttribute> list = taskAndTaskAttributeMapper.getByExample(map);

        List<TaskAndAttributeRO> taskList = DataConverterUtils.convert(list, TaskAndAttributeRO.class);

        return TaskPagingResult.wrapSuccessfulResult(taskList, total.intValue());

    }

    @Override
    public TaskResult<List<Long>> getUserTaskIdList(Long taskId) {
        List<Long> taskIdList = taskService.getUserTaskIdList(taskId);
        return TaskResult.wrapSuccessfulResult(taskIdList);
    }

    @Override
    public TaskResult<List<TaskRO>> selectRecentRunningTaskList(Byte saasEnv, Date startTime, Date endTime) {
        TaskCriteria criteria = new TaskCriteria();
        criteria.createCriteria().andStatusEqualTo(ETaskStatus.RUNNING.getStatus())
                .andSaasEnvEqualTo(saasEnv)
                .andCreateTimeGreaterThanOrEqualTo(endTime)
                .andCreateTimeLessThan(startTime);
        List<Task> taskList = taskMapper.selectByExample(criteria);
        if (CollectionUtils.isEmpty(taskList)) {
            return TaskResult.wrapSuccessfulResult(Lists.newArrayList());
        }
        List<TaskRO> taskROList = DataConverterUtils.convert(taskList, TaskRO.class);
        return TaskResult.wrapSuccessfulResult(taskROList);
    }

}

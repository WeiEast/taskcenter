package com.treefinance.saas.taskcenter.biz.facade.impl;

import com.treefinance.saas.taskcenter.biz.utils.DataConverterUtils;
import com.treefinance.saas.taskcenter.common.exception.BusinessCheckFailException;
import com.treefinance.saas.taskcenter.dao.entity.Task;
import com.treefinance.saas.taskcenter.dao.entity.TaskCriteria;
import com.treefinance.saas.taskcenter.dao.mapper.TaskMapper;
import com.treefinance.saas.taskcenter.facade.request.TaskRequest;
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
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

/**
 * @author:guoguoyun
 * @date:Created in 2018/9/18上午10:24
 */
@Service("taskFacade")
public class TaskFacadeImpl implements TaskFacade {
    private static final Logger logger = LoggerFactory.getLogger(TaskFacade.class);

    @Autowired
    TaskMapper taskMapper;

    @Override
    public TaskResult<Object> testAop(String a, String b) {
        if ("hao".equals(a)) {
            throw new BusinessCheckFailException("-1", "参数异常");
        }
        System.out.println("a=" + a);
        return TaskResult.wrapSuccessfulResult(a);
    }

    @Override
    public TaskResult<List<TaskRO>> queryTask(TaskRequest taskRequest) {

        logger.info("条件查询任务传入的参数为{}", taskRequest.toString());
        TaskCriteria criteria = new TaskCriteria();

        if (StringUtils.isNotEmpty(taskRequest.getOrderByClause())) {
            criteria.setOrderByClause(taskRequest.getOrderByClause());

        }
        TaskCriteria.Criteria innerCriteria = criteria.createCriteria();

        if (taskRequest.getId() == null) {
            innerCriteria.andIdEqualTo(taskRequest.getId());
        }
        if (taskRequest.getBizType() == null) {
            innerCriteria.andBizTypeEqualTo(taskRequest.getBizType());
        }
        if (taskRequest.getSaasEnv() == null) {
            innerCriteria.andSaasEnvEqualTo(taskRequest.getSaasEnv());
        }
        if (taskRequest.getStatus() == null) {
            innerCriteria.andStatusEqualTo(taskRequest.getStatus());
        }
        if (StringUtils.isEmpty(taskRequest.getAccountNo())) {
            innerCriteria.andAccountNoEqualTo(taskRequest.getAccountNo());
        }
        if (StringUtils.isEmpty(taskRequest.getAppId())) {
            innerCriteria.andAppIdEqualTo(taskRequest.getAppId());
        }
        if (StringUtils.isEmpty(taskRequest.getStepCode())) {
            innerCriteria.andStepCodeEqualTo(taskRequest.getStepCode());
        }
        if (StringUtils.isEmpty(taskRequest.getWebSite())) {
            innerCriteria.andWebSiteEqualTo(taskRequest.getWebSite());
        }
        if (StringUtils.isEmpty(taskRequest.getUniqueId())) {
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
    public TaskResult<List<TaskRO>> queryTaskWithPagination(TaskRequest taskRequest) {
        logger.info("分页条件查询任务传入的参数为{}", taskRequest.toString());

        TaskCriteria criteria = new TaskCriteria();

        if (StringUtils.isNotEmpty(taskRequest.getOrderByClause())) {
            criteria.setOrderByClause(taskRequest.getOrderByClause());

        }

        criteria.setLimit(taskRequest.getPageSize());
        criteria.setOffset(taskRequest.getOffset());
        TaskCriteria.Criteria innerCriteria = criteria.createCriteria();

        if (taskRequest.getId() == null) {
            innerCriteria.andIdEqualTo(taskRequest.getId());
        }
        if (taskRequest.getBizType() == null) {
            innerCriteria.andBizTypeEqualTo(taskRequest.getBizType());
        }
        if (taskRequest.getSaasEnv() == null) {
            innerCriteria.andSaasEnvEqualTo(taskRequest.getSaasEnv());
        }
        if (taskRequest.getStatus() == null) {
            innerCriteria.andStatusEqualTo(taskRequest.getStatus());
        }
        if (StringUtils.isEmpty(taskRequest.getAccountNo())) {
            innerCriteria.andAccountNoEqualTo(taskRequest.getAccountNo());
        }
        if (StringUtils.isEmpty(taskRequest.getAppId())) {
            innerCriteria.andAppIdEqualTo(taskRequest.getAppId());
        }
        if (StringUtils.isEmpty(taskRequest.getStepCode())) {
            innerCriteria.andStepCodeEqualTo(taskRequest.getStepCode());
        }
        if (StringUtils.isEmpty(taskRequest.getWebSite())) {
            innerCriteria.andWebSiteEqualTo(taskRequest.getWebSite());
        }
        if (StringUtils.isEmpty(taskRequest.getUniqueId())) {
            innerCriteria.andUniqueIdEqualTo(taskRequest.getUniqueId());
        }


        List<Task> taskList = taskMapper.selectPaginationByExample(criteria);
        if (CollectionUtils.isEmpty(taskList)) {

            return TaskResult.wrapErrorResult("失败", "找不到相关数据");
        }
        List<TaskRO> taskROList = DataConverterUtils.convert(taskList, TaskRO.class);

        return TaskResult.wrapSuccessfulResult(taskROList);

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


}

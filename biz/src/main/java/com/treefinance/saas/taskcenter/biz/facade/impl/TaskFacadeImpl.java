package com.treefinance.saas.taskcenter.biz.facade.impl;

import com.treefinance.saas.knife.common.CommonStateCode;
import com.treefinance.saas.taskcenter.biz.utils.DataConverterUtils;
import com.treefinance.saas.taskcenter.common.exception.BusinessCheckFailException;
import com.treefinance.saas.taskcenter.dao.entity.Task;
import com.treefinance.saas.taskcenter.dao.entity.TaskCriteria;
import com.treefinance.saas.taskcenter.dao.mapper.TaskMapper;
import com.treefinance.saas.taskcenter.facade.request.TaskBuryPointLogRequest;
import com.treefinance.saas.taskcenter.facade.request.TaskRequest;
import com.treefinance.saas.taskcenter.facade.result.TaskRO;
import com.treefinance.saas.taskcenter.facade.result.common.TaskResult;
import com.treefinance.saas.taskcenter.facade.service.TaskFacade;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
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

        logger.info("条件查询任务传入的参数为{}",taskRequest.toString());
        TaskCriteria criteria = new TaskCriteria();

        if(StringUtils.isNotEmpty(taskRequest.getOrderByClause()))
        {
            criteria.setOrderByClause(taskRequest.getOrderByClause());

        }
        TaskCriteria.Criteria innerCriteria =criteria.createCriteria();

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

            return TaskResult.wrapErrorResult("失败","找不到相关数据");
        }
        List<TaskRO> taskROList = DataConverterUtils.convert(taskList,TaskRO.class);

        return TaskResult.wrapSuccessfulResult(taskROList);


    }

    @Override
    public TaskResult<TaskRO> getTaskByPrimaryKey(TaskRequest taskRequest) {
        if (taskRequest.getId() == null) {
            logger.error("传入的id为空");
        }

        Task task = taskMapper.selectByPrimaryKey(taskRequest.getId());
        if (Objects.isNull(task)) {

            return TaskResult.wrapErrorResult("失败","找不到相关数据");
        }
        TaskRO taskRO = new TaskRO();
        BeanUtils.copyProperties(task, taskRO);
        return TaskResult.wrapSuccessfulResult(taskRO);
    }

    @Override
    public TaskResult<List<TaskRO>> queryTaskWithPagination(TaskRequest taskRequest) {
        logger.info("分页条件查询任务传入的参数为{}",taskRequest.toString());

        TaskCriteria criteria = new TaskCriteria();

        if(StringUtils.isNotEmpty(taskRequest.getOrderByClause()))
        {
            criteria.setOrderByClause(taskRequest.getOrderByClause());

        }

        criteria.setLimit(taskRequest.getPageSize());
        criteria.setOffset(taskRequest.getOffset());
        TaskCriteria.Criteria innerCriteria =criteria.createCriteria();

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

            return TaskResult.wrapErrorResult("失败","找不到相关数据");
        }
        List<TaskRO> taskROList = DataConverterUtils.convert(taskList,TaskRO.class);

        return TaskResult.wrapSuccessfulResult(taskROList);

    }


}

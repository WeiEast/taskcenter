package com.treefinance.saas.taskcenter.facade.impl;

import com.treefinance.saas.taskcenter.biz.service.TaskNextDirectiveService;
import com.treefinance.saas.taskcenter.common.util.DataConverterUtils;
import com.treefinance.saas.taskcenter.common.model.dto.DirectiveDTO;
import com.treefinance.saas.taskcenter.dao.entity.TaskNextDirective;
import com.treefinance.saas.taskcenter.dao.entity.TaskNextDirectiveCriteria;
import com.treefinance.saas.taskcenter.dao.mapper.TaskNextDirectiveMapper;
import com.treefinance.saas.taskcenter.facade.request.TaskDirectiveRequest;
import com.treefinance.saas.taskcenter.facade.request.TaskNextDirectiveRequest;
import com.treefinance.saas.taskcenter.facade.result.TaskNextDirectiveRO;
import com.treefinance.saas.taskcenter.facade.result.common.TaskResult;
import com.treefinance.saas.taskcenter.facade.service.TaskNextDirectiveFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author chengtong
 * @date 18/9/19 18:03
 */
@Component("taskNextDirectiveFacade")
public class TaskNextDirectiveFacadeImpl implements TaskNextDirectiveFacade {


    @Autowired
    TaskNextDirectiveMapper taskNextDirectiveMapper;
    @Autowired
    private TaskNextDirectiveService taskNextDirectiveService;


    @Override
    public TaskResult<List<TaskNextDirectiveRO>> queryTaskNextDirectiveByTaskId(TaskNextDirectiveRequest request) {
        TaskNextDirectiveCriteria taskNextDirectiveCriteria = new TaskNextDirectiveCriteria();
        taskNextDirectiveCriteria.createCriteria().andTaskIdEqualTo(request.getTaskId());
        taskNextDirectiveCriteria.setOrderByClause("createTime desc, Id desc");
        List<TaskNextDirective> list = taskNextDirectiveMapper.selectByExample(taskNextDirectiveCriteria);

        List<TaskNextDirectiveRO> taskNextDirectiveROList = DataConverterUtils.convert(list, TaskNextDirectiveRO.class);

        return TaskResult.wrapSuccessfulResult(taskNextDirectiveROList);
    }


    @Override
    public TaskResult<Long> insert(Long taskId, String directive, String remark) {
        Long id = taskNextDirectiveService.insert(taskId, directive, remark);
        return TaskResult.wrapSuccessfulResult(id);
    }

    @Override
    public TaskResult<TaskNextDirectiveRO> queryRecentDirective(Long taskId) {
        TaskNextDirective taskNextDirective = taskNextDirectiveService.queryRecentDirective(taskId);
        TaskNextDirectiveRO taskNextDirectiveRO = DataConverterUtils.convert(taskNextDirective, TaskNextDirectiveRO.class);
        return TaskResult.wrapSuccessfulResult(taskNextDirectiveRO);
    }

    @Override
    public TaskResult<Void> insertAndCacheNextDirective(Long taskId, TaskDirectiveRequest directive) {
        DirectiveDTO directiveDTO = DataConverterUtils.convert(directive, DirectiveDTO.class);
        taskNextDirectiveService.insertAndCacheNextDirective(taskId, directiveDTO);
        return TaskResult.wrapSuccessfulResult(null);
    }

    @Override
    public TaskResult<String> getNextDirective(Long taskId) {
        String directive = taskNextDirectiveService.getNextDirective(taskId);
        return TaskResult.wrapSuccessfulResult(directive);
    }

    @Override
    public TaskResult<Void> deleteNextDirective(Long taskId) {
        taskNextDirectiveService.deleteNextDirective(taskId);
        return TaskResult.wrapSuccessfulResult(null);
    }

    @Override
    public TaskResult<Void> deleteNextDirective(Long taskId, String directive) {
        taskNextDirectiveService.deleteNextDirective(taskId, directive);
        return TaskResult.wrapSuccessfulResult(null);
    }
}

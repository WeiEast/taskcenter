package com.treefinance.saas.taskcenter.facade.impl;

import com.treefinance.saas.taskcenter.service.TaskNextDirectiveService;
import com.treefinance.saas.taskcenter.dao.entity.TaskNextDirective;
import com.treefinance.saas.taskcenter.facade.request.TaskNextDirectiveRequest;
import com.treefinance.saas.taskcenter.facade.response.TaskResponse;
import com.treefinance.saas.taskcenter.facade.result.DirectiveDTO;
import com.treefinance.saas.taskcenter.facade.result.SimpleDirectiveDTO;
import com.treefinance.saas.taskcenter.facade.result.TaskNextDirectiveRO;
import com.treefinance.saas.taskcenter.facade.result.common.TaskResult;
import com.treefinance.saas.taskcenter.facade.service.TaskNextDirectiveFacade;
import com.treefinance.saas.taskcenter.facade.validate.Preconditions;
import com.treefinance.saas.taskcenter.service.domain.DirectiveEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author chengtong
 * @date 18/9/19 18:03
 */
@Component("taskNextDirectiveFacade")
public class TaskNextDirectiveFacadeImpl extends AbstractFacade implements TaskNextDirectiveFacade {

    @Autowired
    private TaskNextDirectiveService taskNextDirectiveService;

    @Override
    public TaskResult<List<TaskNextDirectiveRO>> queryTaskNextDirectiveByTaskId(TaskNextDirectiveRequest request) {
        List<TaskNextDirective> list = taskNextDirectiveService.listDirectivesDescWithCreateTimeByTaskId(request.getTaskId());

        List<TaskNextDirectiveRO> taskNextDirectiveROList = convert(list, TaskNextDirectiveRO.class);

        return TaskResult.wrapSuccessfulResult(taskNextDirectiveROList);
    }

    @Override
    public TaskResult<String> getNextDirective(Long taskId) {
        String directive = taskNextDirectiveService.queryPresentDirectiveAsJson(taskId);
        return TaskResult.wrapSuccessfulResult(directive);
    }

    @Override
    public TaskResult<Void> deleteNextDirective(Long taskId) {
        taskNextDirectiveService.awaitNext(taskId);
        return TaskResult.wrapSuccessfulResult(null);
    }

    @Override
    public TaskResult<Void> deleteNextDirective(Long taskId, String directive) {
        taskNextDirectiveService.compareAndEnd(taskId, directive);
        return TaskResult.wrapSuccessfulResult(null);
    }

    @Override
    public TaskResponse<List<SimpleDirectiveDTO>> queryDirectivesByTaskId(Long taskId) {
        Preconditions.notNull("taskId", taskId);
        List<TaskNextDirective> list = taskNextDirectiveService.listDirectivesDescWithCreateTimeByTaskId(taskId);

        List<SimpleDirectiveDTO> directives = convert(list, SimpleDirectiveDTO.class);

        return TaskResponse.success(directives);
    }

    @Override
    public TaskResponse<String> queryPresentDirectiveAsJson(Long taskId) {
        Preconditions.notNull("taskId", taskId);
        final String json = taskNextDirectiveService.queryPresentDirectiveAsJson(taskId);
        return TaskResponse.success(json);
    }

    @Override
    public TaskResponse<DirectiveDTO> queryPresentDirective(Long taskId) {
        Preconditions.notNull("taskId", taskId);
        final DirectiveEntity directiveEntity = taskNextDirectiveService.queryPresentDirective(taskId);
        final DirectiveDTO directive = convert(directiveEntity, DirectiveDTO.class);
        return TaskResponse.success(directive);
    }

    @Override
    public TaskResponse<Void> awaitNext(Long taskId) {
        Preconditions.notNull("taskId", taskId);
        taskNextDirectiveService.awaitNext(taskId);
        return TaskResponse.success(null);
    }

    @Override
    public TaskResponse<Void> compareAndEnd(Long taskId, String directive) {
        Preconditions.notNull("taskId", taskId);
        taskNextDirectiveService.compareAndEnd(taskId, directive);
        return TaskResponse.success(null);
    }
}

package com.treefinance.saas.taskcenter.facade.impl;

import com.treefinance.saas.taskcenter.biz.service.TaskNextDirectiveService;
import com.treefinance.saas.taskcenter.dao.entity.TaskNextDirective;
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
}

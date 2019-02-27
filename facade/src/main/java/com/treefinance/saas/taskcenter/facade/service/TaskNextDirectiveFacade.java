package com.treefinance.saas.taskcenter.facade.service;

import com.treefinance.saas.taskcenter.facade.request.TaskDirectiveRequest;
import com.treefinance.saas.taskcenter.facade.request.TaskNextDirectiveRequest;
import com.treefinance.saas.taskcenter.facade.result.TaskNextDirectiveRO;
import com.treefinance.saas.taskcenter.facade.result.common.TaskResult;

import java.util.List;

/**
 * @author chengtong
 * @date 18/9/19 17:43
 */
public interface TaskNextDirectiveFacade {

    TaskResult<List<TaskNextDirectiveRO>> queryTaskNextDirectiveByTaskId(TaskNextDirectiveRequest request);

    TaskResult<Long> insert(Long taskId, String directive, String remark);

    TaskResult<TaskNextDirectiveRO> queryRecentDirective(Long taskId);

    TaskResult<Void> insertAndCacheNextDirective(Long taskId, TaskDirectiveRequest directive);

    TaskResult<String> getNextDirective(Long taskId);

    TaskResult<Void> deleteNextDirective(Long taskId);

    TaskResult<Void> deleteNextDirective(Long taskId, String directive);

}

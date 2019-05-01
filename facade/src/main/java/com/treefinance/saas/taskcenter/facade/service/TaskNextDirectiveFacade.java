package com.treefinance.saas.taskcenter.facade.service;

import com.treefinance.saas.taskcenter.facade.request.TaskNextDirectiveRequest;
import com.treefinance.saas.taskcenter.facade.response.TaskResponse;
import com.treefinance.saas.taskcenter.facade.result.DirectiveDTO;
import com.treefinance.saas.taskcenter.facade.result.SimpleDirectiveDTO;
import com.treefinance.saas.taskcenter.facade.result.TaskNextDirectiveRO;
import com.treefinance.saas.taskcenter.facade.result.common.TaskResult;

import java.util.List;

/**
 * @author chengtong
 * @date 18/9/19 17:43
 */
public interface TaskNextDirectiveFacade {

    TaskResult<List<TaskNextDirectiveRO>> queryTaskNextDirectiveByTaskId(TaskNextDirectiveRequest request);

    /**
     * @deprecated use {@link #queryPresentDirectiveAsJson(Long)} instead
     */
    TaskResult<String> getNextDirective(Long taskId);

    TaskResult<Void> deleteNextDirective(Long taskId);

    TaskResult<Void> deleteNextDirective(Long taskId, String directive);

    /**
     * 获取全部的指令信息
     * 
     * @param taskId 任务ID
     * @return a list of {@link SimpleDirectiveDTO}
     */
    TaskResponse<List<SimpleDirectiveDTO>> queryDirectivesByTaskId(Long taskId);

    /**
     * 获取当前最新的指令信息，并以json格式返回
     * 
     * @param taskId 任务ID
     * @return json格式序列化的指令信息
     * @see #queryPresentDirective(Long)
     */
    TaskResponse<String> queryPresentDirectiveAsJson(Long taskId);

    /**
     * 获取当前最新的指令信息
     *
     * @param taskId 任务ID
     * @return 指令信息
     */
    TaskResponse<DirectiveDTO> queryPresentDirective(Long taskId);

    /**
     * 结束当前指令并等待下一个
     * 
     * @param taskId 任务ID
     * @return Void
     */
    TaskResponse<Void> awaitNext(Long taskId);

    /**
     * 对比并结束给定的指令，等待下一个
     *
     * @param taskId 任务ID
     * @return Void
     */
    TaskResponse<Void> compareAndEnd(Long taskId, String directive);
}

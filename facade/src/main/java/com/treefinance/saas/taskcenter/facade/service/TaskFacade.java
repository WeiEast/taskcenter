package com.treefinance.saas.taskcenter.facade.service;

import com.treefinance.saas.taskcenter.facade.request.TaskAndAttributeRequest;
import com.treefinance.saas.taskcenter.facade.request.TaskCreateRequest;
import com.treefinance.saas.taskcenter.facade.request.TaskRequest;
import com.treefinance.saas.taskcenter.facade.result.TaskAndAttributeRO;
import com.treefinance.saas.taskcenter.facade.result.TaskRO;
import com.treefinance.saas.taskcenter.facade.result.common.TaskPagingResult;
import com.treefinance.saas.taskcenter.facade.result.common.TaskResult;

import java.util.List;

/**
 * @author:guoguoyun
 * @date:Created in 2018/9/18上午10:20
 */
public interface TaskFacade {

    /**
     * 条件查询任务列表
     *
     * @param taskRequest
     * @return
     */
    TaskResult<List<TaskRO>> queryTask(TaskRequest taskRequest);

    /**
     * 创建任务
     *
     * @param taskCreateRequest
     * @return
     */
    TaskResult<Long> createTask(TaskCreateRequest taskCreateRequest);

    /**
     * 更新任务
     *
     * @param taskId    必填
     * @param accountNo 必填
     * @param webSite
     * @return
     */
    TaskResult<Void> updateTask(Long taskId, String accountNo, String webSite);

    TaskResult<Integer> updateUnfinishedTask(TaskCreateRequest taskRequest);

    TaskResult<String> failTaskWithStep(Long taskId);

    TaskResult<String> cancelTaskWithStep(Long taskId);

    TaskResult<String> updateTaskStatusWithStep(Long taskId, Byte status);


    /**
     * 取消任务
     *
     * @param taskId
     * @return
     */
    TaskResult<Void> cancelTask(Long taskId);

    /**
     * 根据id获得任务详情
     *
     * @param taskRequest
     * @return
     */
    TaskResult<TaskRO> getTaskByPrimaryKey(TaskRequest taskRequest);

    /**
     * 条件查询任务列表（分页）
     *
     * @param taskRequest
     * @return
     */
    TaskPagingResult<TaskRO> queryTaskWithPagination(TaskRequest taskRequest);

    /**
     * console -- 查询任务列表
     * /saas/console/tasks
     *
     * @param request
     * @return
     */
    TaskPagingResult<TaskRO> queryTaskListPage(TaskRequest request);

    /**
     * console -- 查询任务列表数据
     *
     * @param taskRequest
     * @return
     */
    TaskResult<List<TaskRO>> queryTaskList(TaskRequest taskRequest);


    TaskPagingResult<TaskAndAttributeRO> queryTaskAndTaskAttribute(TaskAndAttributeRequest request);

}

package com.treefinance.saas.taskcenter.facade.service;

import com.treefinance.saas.taskcenter.facade.request.TaskRequest;
import com.treefinance.saas.taskcenter.facade.result.TaskRO;
import com.treefinance.saas.taskcenter.facade.result.common.TaskResult;

import java.util.List;

/**
 * @author:guoguoyun
 * @date:Created in 2018/9/18上午10:20
 */
public interface TaskFacade {

    TaskResult<Object> testAop(String a, String b);

    /**
     * 条件查询任务列表（分页）
     * @param taskRequest
     * @return
     */
    TaskResult<List<TaskRO>> queryTask(TaskRequest taskRequest);

    /**
     * 根据id获得任务详情
     * @param taskRequest
     * @return
     */
    TaskResult<TaskRO> getTaskByPrimaryKey(TaskRequest taskRequest);

    /**
     * 条件查询任务列表（分页）
     * @param taskRequest
     * @return
     */
    TaskResult<List<TaskRO>> queryTaskWithPagination(TaskRequest taskRequest);


}

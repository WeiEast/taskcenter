package com.treefinance.saas.taskcenter.facade.service;

import com.treefinance.saas.taskcenter.facade.request.TaskBuryPointLogRequest;
import com.treefinance.saas.taskcenter.facade.result.TaskBuryPointLogRO;
import com.treefinance.saas.taskcenter.facade.result.common.TaskResult;

import java.util.List;

/**
 * @author:guoguoyun
 * @date:Created in 2018/9/18上午10:22
 */
public interface TaskBuryPointLogFacade {

    /**
     * 条件查询埋点日志列表信息
     *
     * @param taskBuryPointLogRequest
     * @return
     */
    TaskResult<List<TaskBuryPointLogRO>> queryTaskBuryPointLog(TaskBuryPointLogRequest taskBuryPointLogRequest);

    /**
     * console -- 查询某个任务的埋点信息
     */
    TaskResult<List<TaskBuryPointLogRO>> queryTaskBuryPointLogById(TaskBuryPointLogRequest taskBuryPointLogRequest);

    TaskResult<Void> pushTaskBuryPointLog(Long taskId, String appId, String code);

    TaskResult<Void> logTaskOperatorMaintainUser(Long taskId, String appId, String extra);

}

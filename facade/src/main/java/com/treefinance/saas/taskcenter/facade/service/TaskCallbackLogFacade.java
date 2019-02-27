package com.treefinance.saas.taskcenter.facade.service;

import com.treefinance.saas.taskcenter.facade.request.TaskCallbackLogPageRequest;
import com.treefinance.saas.taskcenter.facade.request.TaskCallbackLogRequest;
import com.treefinance.saas.taskcenter.facade.result.TaskCallbackLogRO;
import com.treefinance.saas.taskcenter.facade.result.common.TaskPagingResult;
import com.treefinance.saas.taskcenter.facade.result.common.TaskResult;

import java.util.List;

/**
 * @author:guoguoyun
 * @date:Created in 2018/9/18上午10:22
 */
public interface TaskCallbackLogFacade {

    TaskResult<List<TaskCallbackLogRO>> queryTaskCallbackLog(TaskCallbackLogRequest taskCallbackLogRequest);

    /**
     * console dubbo接口
     *
     * @param taskIdList 任务编号的列表
     * @return 任务编号对应的所有TaskCallbackLogRO
     */
    TaskResult<List<TaskCallbackLogRO>> queryTaskCallbackLog(List<Long> taskIdList);

    /**
     * 分页的返回taskCallBackLog数据
     *
     * @param request 任务编号的列表
     */
    TaskPagingResult<TaskCallbackLogRO> queryTaskCallbackLogPage(TaskCallbackLogPageRequest request);

    TaskResult<List<TaskCallbackLogRO>> getTaskCallbackLogs(Long taskId, List<Long> configIds);

}

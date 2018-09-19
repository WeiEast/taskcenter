package com.treefinance.saas.taskcenter.facade.service;

import com.treefinance.saas.knife.result.SaasResult;
import com.treefinance.saas.taskcenter.facade.request.TaskLogRequest;
import com.treefinance.saas.taskcenter.facade.result.TaskLogRO;
import com.treefinance.saas.taskcenter.facade.result.common.TaskResult;

import java.util.List;

/**
 * @author:guoguoyun
 * @date:Created in 2018/9/18上午10:21
 */
public interface TaskLogFacade {

    TaskResult<List<TaskLogRO>> queryTaskLog(TaskLogRequest taskLogRequest);

    /**
     * console 请求的任务日志数据
     * */
    TaskResult<List<TaskLogRO>> queryTaskLogById(TaskLogRequest taskLogRequest);

}

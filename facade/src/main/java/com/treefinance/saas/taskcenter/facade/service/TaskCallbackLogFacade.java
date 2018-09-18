package com.treefinance.saas.taskcenter.facade.service;

import com.treefinance.saas.knife.result.SaasResult;
import com.treefinance.saas.taskcenter.facade.request.TaskCallbackLogRequest;
import com.treefinance.saas.taskcenter.facade.result.TaskCallbackLogRO;
import com.treefinance.saas.taskcenter.facade.result.common.TaskResult;

import java.util.List;

/**
 * @author:guoguoyun
 * @date:Created in 2018/9/18上午10:22
 */
public interface TaskCallbackLogFacade {

     TaskResult<List<TaskCallbackLogRO>> queryTaskCallbackLog(TaskCallbackLogRequest taskCallbackLogRequest);
}

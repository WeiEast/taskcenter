package com.treefinance.saas.taskcenter.facade.service;

import com.treefinance.saas.taskcenter.facade.request.CarInfoCollectTaskLogRequest;
import com.treefinance.saas.taskcenter.facade.result.common.TaskResult;

import java.util.List;

/**
 * @author haojiahong
 * @date 2018/9/26
 */
public interface CarInfoFacade {

    /**
     * @deprecated use {@link TaskFacade#completeTaskAndMonitoring(Long, List)} instead.
     */
    @Deprecated
    TaskResult<Void> updateCollectTaskStatusAndTaskLogAndSendMonitor(Long taskId, List<CarInfoCollectTaskLogRequest> logList);

}

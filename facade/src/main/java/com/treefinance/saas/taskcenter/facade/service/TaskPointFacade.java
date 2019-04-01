package com.treefinance.saas.taskcenter.facade.service;

import com.treefinance.saas.taskcenter.facade.request.TaskPointRequest;
import com.treefinance.saas.taskcenter.facade.result.common.TaskResult;

/**
 * @author 张琰佳
 * @since 2:43 PM 2019/1/22
 */
public interface TaskPointFacade {
    /**
     * 记录埋点并通知功夫贷
     * 
     * @param taskPointRequest taskPointRequest
     * @return
     */
    TaskResult<Void> addTaskPoint(TaskPointRequest taskPointRequest);
}

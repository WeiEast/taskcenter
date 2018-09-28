package com.treefinance.saas.taskcenter.facade.service;

import com.treefinance.saas.taskcenter.facade.request.AcquisitionRequest;
import com.treefinance.saas.taskcenter.facade.result.common.TaskResult;

/**
 * @author haojiahong
 * @date 2018/9/27
 */
public interface AcquisitionFacade {
    TaskResult<Void> acquisition(AcquisitionRequest request);
}

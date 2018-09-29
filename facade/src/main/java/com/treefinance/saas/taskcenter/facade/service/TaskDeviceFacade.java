package com.treefinance.saas.taskcenter.facade.service;

import com.treefinance.saas.taskcenter.facade.request.TaskDeviceRequest;
import com.treefinance.saas.taskcenter.facade.result.common.TaskResult;

/**
 * @author haojiahong
 * @date 2018/9/29
 */
public interface TaskDeviceFacade {

    TaskResult<Void> insertSelective(TaskDeviceRequest request);
}

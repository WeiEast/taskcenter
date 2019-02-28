package com.treefinance.saas.taskcenter.facade.impl;

import com.treefinance.saas.taskcenter.dao.entity.TaskDevice;
import com.treefinance.saas.taskcenter.dao.repository.TaskDeviceRepository;
import com.treefinance.saas.taskcenter.facade.request.TaskDeviceRequest;
import com.treefinance.saas.taskcenter.facade.result.common.TaskResult;
import com.treefinance.saas.taskcenter.facade.service.TaskDeviceFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author haojiahong
 * @date 2018/9/29
 */
@Component("taskDeviceFacade")
public class TaskDeviceFacadeImpl extends AbstractFacade implements TaskDeviceFacade {

    @Autowired
    private TaskDeviceRepository taskDeviceRepository;

    @Override
    public TaskResult<Void> insertSelective(TaskDeviceRequest request) {
        TaskDevice taskDevice = convert(request, TaskDevice.class);
        taskDeviceRepository.insert(taskDevice);
        return TaskResult.wrapSuccessfulResult(null);
    }

}

package com.treefinance.saas.taskcenter.biz.facade.impl;

import com.treefinance.saas.taskcenter.biz.utils.DataConverterUtils;
import com.treefinance.saas.taskcenter.dao.entity.TaskDevice;
import com.treefinance.saas.taskcenter.dao.mapper.TaskDeviceMapper;
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
public class TaskDeviceFacadeImpl implements TaskDeviceFacade {

    @Autowired
    private TaskDeviceMapper taskDeviceMapper;


    @Override
    public TaskResult<Void> insertSelective(TaskDeviceRequest request) {
        TaskDevice taskDevice = DataConverterUtils.convert(request, TaskDevice.class);
        taskDeviceMapper.insertSelective(taskDevice);
        return TaskResult.wrapSuccessfulResult(null);
    }

}

package com.treefinance.saas.taskcenter.biz.facade.impl;

import com.treefinance.commonservice.uid.UidGenerator;
import com.treefinance.commonservice.uid.UidService;
import com.treefinance.saas.taskcenter.dao.entity.TaskPoint;
import com.treefinance.saas.taskcenter.dao.mapper.TaskPointMapper;
import com.treefinance.saas.taskcenter.facade.request.TaskPointRequest;
import com.treefinance.saas.taskcenter.facade.result.common.TaskResult;
import com.treefinance.saas.taskcenter.facade.service.TaskPointFacade;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author 张琰佳
 * @since 2:45 PM 2019/1/22
 */
@Service
public class TaskPointFacadeImpl implements TaskPointFacade {
    @Autowired
    private TaskPointMapper taskPointMapper;

    @Override
    public TaskResult<Void> addTaskPoint(TaskPointRequest taskPointRequest) {
        TaskPoint taskPoint = new TaskPoint();
        BeanUtils.copyProperties(taskPointRequest, taskPoint);
        taskPoint.setId(UidGenerator.getId());
        int i=taskPointMapper.insertSelective(taskPoint);
        if (i == 1) {
            //TODO 调功夫贷接口返回
        }
        return new TaskResult<>();
    }
}

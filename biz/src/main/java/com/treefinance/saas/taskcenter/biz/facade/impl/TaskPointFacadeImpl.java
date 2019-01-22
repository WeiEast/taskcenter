package com.treefinance.saas.taskcenter.biz.facade.impl;

import com.treefinance.commonservice.uid.UidGenerator;
import com.treefinance.saas.taskcenter.biz.cache.redis.RedisDao;
import com.treefinance.saas.taskcenter.dao.entity.Task;
import com.treefinance.saas.taskcenter.dao.entity.TaskPoint;
import com.treefinance.saas.taskcenter.dao.mapper.TaskMapper;
import com.treefinance.saas.taskcenter.dao.mapper.TaskPointMapper;
import com.treefinance.saas.taskcenter.facade.request.TaskPointRequest;
import com.treefinance.saas.taskcenter.facade.result.common.TaskResult;
import com.treefinance.saas.taskcenter.facade.service.TaskPointFacade;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * @author 张琰佳
 * @since 2:45 PM 2019/1/22
 */
@Service
public class TaskPointFacadeImpl implements TaskPointFacade {
    @Autowired
    private TaskPointMapper taskPointMapper;
    @Autowired
    private TaskMapper taskMapper;
    @Autowired
    private RedisDao redisDao;

    @Override
    public TaskResult<Void> addTaskPoint(TaskPointRequest taskPointRequest) {
        TaskPoint taskPoint = new TaskPoint();
        BeanUtils.copyProperties(taskPointRequest, taskPoint);
        String str=redisDao.get("UniqueId_"+taskPointRequest.getTaskId());
        if (str==null) {
            Task task = taskMapper.selectByPrimaryKey(taskPointRequest.getTaskId());
            String taskId=task.getUniqueId();
            redisDao.setEx("UniqueId_"+taskPointRequest.getTaskId(),task.getUniqueId(),10, TimeUnit.MINUTES);
            taskPoint.setUniqueId(Long.parseLong(taskId));
        }else{
            taskPoint.setUniqueId(Long.parseLong(str));
        }
        taskPoint.setId(UidGenerator.getId());
        int i=taskPointMapper.insertSelective(taskPoint);
        if (i == 1) {
            //TODO 调功夫贷接口返回
        }
        return new TaskResult<>();
    }
}

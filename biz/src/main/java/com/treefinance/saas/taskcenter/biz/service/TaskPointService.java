package com.treefinance.saas.taskcenter.biz.service;

import com.treefinance.commonservice.uid.UidGenerator;
import com.treefinance.saas.taskcenter.biz.cache.redis.RedisDao;
import com.treefinance.saas.taskcenter.dao.entity.Task;
import com.treefinance.saas.taskcenter.dao.entity.TaskPoint;
import com.treefinance.saas.taskcenter.dao.mapper.TaskMapper;
import com.treefinance.saas.taskcenter.dao.mapper.TaskPointMapper;
import com.treefinance.saas.taskcenter.facade.request.TaskPointRequest;
import com.treefinance.saas.taskcenter.facade.result.common.TaskResult;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author 张琰佳
 * @since 1:52 PM 2019/1/23
 */
@Service
public class TaskPointService {

    @Autowired
    private TaskPointMapper taskPointMapper;
    @Autowired
    private TaskMapper taskMapper;
    @Autowired
    private RedisDao redisDao;

    public void addTaskPoint(TaskPointRequest taskPointRequest) {
        TaskPoint taskPoint = new TaskPoint();
        BeanUtils.copyProperties(taskPointRequest, taskPoint);
        if (taskPoint.getType() == null) {
            taskPoint.setType((byte)0);
        }
        String str = redisDao.get("UniqueId_bizType_" + taskPointRequest.getTaskId());
        if (str == null) {
            Task task = taskMapper.selectByPrimaryKey(taskPointRequest.getTaskId());
            String taskId = task.getUniqueId();
            redisDao.setEx("UniqueId_bizType_" + taskPointRequest.getTaskId(), task.getUniqueId() + "," + task.getBizType(), 10, TimeUnit.MINUTES);
            taskPoint.setUniqueId(Long.parseLong(taskId));
            taskPoint.setBizType(task.getBizType());
        } else {
            List<String> list = Arrays.asList(str.split(","));
            taskPoint.setUniqueId(Long.parseLong(list.get(0)));
            taskPoint.setBizType((Byte.valueOf(list.get(1))));
        }
        if (taskPoint.getType() == 1) {
            if (taskPoint.getBizType() == 1) {
                taskPoint.setCode("2" + taskPoint.getCode());
            } else if (taskPoint.getBizType() == 2) {
                taskPoint.setCode("3" + taskPoint.getCode());
            } else if (taskPoint.getBizType() == 3) {
                taskPoint.setCode("1" + taskPoint.getCode());
            } else {
                taskPoint.setCode("0" + taskPoint.getCode());
            }
        }
        taskPoint.setId(UidGenerator.getId());
        int i = taskPointMapper.insertSelective(taskPoint);
        if (i == 1) {
            // TODO 调功夫贷接口返回
        }
    }
}

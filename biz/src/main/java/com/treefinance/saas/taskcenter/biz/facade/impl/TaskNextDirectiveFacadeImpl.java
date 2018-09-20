package com.treefinance.saas.taskcenter.biz.facade.impl;

import com.treefinance.saas.taskcenter.biz.utils.DataConverterUtils;
import com.treefinance.saas.taskcenter.dao.entity.TaskNextDirective;
import com.treefinance.saas.taskcenter.dao.entity.TaskNextDirectiveCriteria;
import com.treefinance.saas.taskcenter.dao.mapper.TaskNextDirectiveMapper;
import com.treefinance.saas.taskcenter.facade.request.TaskNextDirectiveRequest;
import com.treefinance.saas.taskcenter.facade.result.TaskNextDirectiveRO;
import com.treefinance.saas.taskcenter.facade.result.common.TaskResult;
import com.treefinance.saas.taskcenter.facade.service.TaskNextDirectiveFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author chengtong
 * @date 18/9/19 18:03
 */
@Service("taskNextDirectiveFacade")
public class TaskNextDirectiveFacadeImpl implements TaskNextDirectiveFacade {


    @Autowired
    TaskNextDirectiveMapper taskNextDirectiveMapper;


    @Override
    public TaskResult<List<TaskNextDirectiveRO>> queryTaskNextDirectiveByTaskId(TaskNextDirectiveRequest request) {
        TaskNextDirectiveCriteria taskNextDirectiveCriteria = new TaskNextDirectiveCriteria();
        taskNextDirectiveCriteria.createCriteria().andTaskIdEqualTo(request.getTaskId());
        taskNextDirectiveCriteria.setOrderByClause("createTime desc, Id desc");
        List<TaskNextDirective> list = taskNextDirectiveMapper.selectByExample(taskNextDirectiveCriteria);

        List<TaskNextDirectiveRO> taskNextDirectiveROList = DataConverterUtils.convert(list, TaskNextDirectiveRO.class);

        return TaskResult.wrapSuccessfulResult(taskNextDirectiveROList);
    }
}

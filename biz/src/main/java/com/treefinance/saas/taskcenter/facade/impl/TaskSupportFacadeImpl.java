package com.treefinance.saas.taskcenter.facade.impl;

import com.google.common.collect.Lists;
import com.treefinance.saas.taskcenter.service.TaskSupportService;
import com.treefinance.saas.taskcenter.dao.entity.TaskSupport;
import com.treefinance.saas.taskcenter.facade.result.TaskSupportRO;
import com.treefinance.saas.taskcenter.facade.result.common.TaskResult;
import com.treefinance.saas.taskcenter.facade.service.TaskSupportFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * @author haojiahong
 * @date 2018/9/29
 */
@Component("taskSupportFacade")
public class TaskSupportFacadeImpl extends AbstractFacade implements TaskSupportFacade {

    @Autowired
    private TaskSupportService taskSupportService;

    @Override
    public TaskResult<List<TaskSupportRO>> getSupportedList(String supportType, Integer id, String name) {
        List<TaskSupport> taskSupportList = taskSupportService.getSupportedList(supportType, id, name);
        if (CollectionUtils.isEmpty(taskSupportList)) {
            return TaskResult.wrapSuccessfulResult(Lists.newArrayList());
        }
        List<TaskSupportRO> taskSupportROList = convert(taskSupportList, TaskSupportRO.class);
        return TaskResult.wrapSuccessfulResult(taskSupportROList);
    }

}

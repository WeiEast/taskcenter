package com.treefinance.saas.taskcenter.facade.service;

import com.treefinance.saas.taskcenter.facade.request.TaskAttributeRequest;
import com.treefinance.saas.taskcenter.facade.result.TaskAttributeRO;
import com.treefinance.saas.taskcenter.facade.result.common.TaskResult;

import java.util.List;

/**
 * @author:guoguoyun
 * @date:Created in 2018/9/18上午11:01
 */
public interface TaskAttributeFacade {

    public TaskResult<List<TaskAttributeRO>> queryTaskAttribute(TaskAttributeRequest taskAttributeRequest);

}

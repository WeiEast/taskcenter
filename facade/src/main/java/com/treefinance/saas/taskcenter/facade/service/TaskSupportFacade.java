package com.treefinance.saas.taskcenter.facade.service;

import com.treefinance.saas.taskcenter.facade.request.TaskSupportQueryRequest;
import com.treefinance.saas.taskcenter.facade.response.TaskResponse;
import com.treefinance.saas.taskcenter.facade.result.TaskSupportDTO;
import com.treefinance.saas.taskcenter.facade.result.TaskSupportRO;
import com.treefinance.saas.taskcenter.facade.result.common.TaskResult;

import java.util.List;

/**
 * @author haojiahong
 * @date 2018/9/29
 */
public interface TaskSupportFacade {
    TaskResult<List<TaskSupportRO>> getSupportedList(String supportType, Integer id, String name);

    TaskResponse<List<TaskSupportDTO>> querySupportedList(TaskSupportQueryRequest request);
}

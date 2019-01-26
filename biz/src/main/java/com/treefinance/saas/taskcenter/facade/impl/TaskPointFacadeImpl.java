package com.treefinance.saas.taskcenter.facade.impl;

import com.alibaba.fastjson.JSON;
import com.treefinance.saas.taskcenter.biz.service.TaskPointService;
import com.treefinance.saas.taskcenter.context.component.AbstractFacade;
import com.treefinance.saas.taskcenter.facade.request.TaskPointRequest;
import com.treefinance.saas.taskcenter.facade.result.common.TaskResult;
import com.treefinance.saas.taskcenter.facade.service.TaskPointFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author 张琰佳
 * @since 2:45 PM 2019/1/22
 */
@Service("taskPointFacade")
public class TaskPointFacadeImpl extends AbstractFacade implements TaskPointFacade {
    @Autowired
    private TaskPointService taskPointService;

    @Override
    public TaskResult<Void> addTaskPoint(TaskPointRequest taskPointRequest) {
        logger.info("增加埋点请求参数 request={}", JSON.toJSONString(taskPointRequest));
        taskPointService.addTaskPoint(taskPointRequest);
        return new TaskResult<>();
    }
}

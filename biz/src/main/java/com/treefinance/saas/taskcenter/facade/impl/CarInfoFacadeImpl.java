package com.treefinance.saas.taskcenter.facade.impl;

import com.treefinance.saas.taskcenter.biz.service.TaskService;
import com.treefinance.saas.taskcenter.facade.request.CarInfoCollectTaskLogRequest;
import com.treefinance.saas.taskcenter.facade.result.common.TaskResult;
import com.treefinance.saas.taskcenter.facade.service.CarInfoFacade;
import com.treefinance.saas.taskcenter.facade.validate.Preconditions;
import com.treefinance.saas.taskcenter.service.param.TaskStepLogObject;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author haojiahong
 * @date 2018/9/26
 */
@Component("carInfoFacade")
public class CarInfoFacadeImpl extends AbstractFacade implements CarInfoFacade {

    @Autowired
    private TaskService taskService;

    @Override
    public TaskResult<Void> updateCollectTaskStatusAndTaskLogAndSendMonitor(Long taskId, List<CarInfoCollectTaskLogRequest> logList) {
        Preconditions.notNull("taskId", taskId);

        if(CollectionUtils.isNotEmpty(logList)){
            List<TaskStepLogObject> logs = logList.stream().map(dto -> {
                TaskStepLogObject obj = new TaskStepLogObject();
                obj.setStepMsg(dto.getMsg());
                obj.setErrorMsg(dto.getErrorMsg());
                obj.setOccurTime(dto.getOccurTime());

                return obj;

            }).collect(Collectors.toList());

            taskService.completeTaskAndMonitoring(taskId, logs);
        }

        return TaskResult.wrapSuccessfulResult(null);
    }
}

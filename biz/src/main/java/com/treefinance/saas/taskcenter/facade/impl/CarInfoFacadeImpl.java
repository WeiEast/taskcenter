package com.treefinance.saas.taskcenter.facade.impl;

import com.treefinance.saas.taskcenter.biz.service.CarInfoService;
import com.treefinance.saas.taskcenter.context.component.AbstractFacade;
import com.treefinance.saas.taskcenter.dto.CarInfoCollectTaskLogDTO;
import com.treefinance.saas.taskcenter.exception.BusinessCheckFailException;
import com.treefinance.saas.taskcenter.facade.request.CarInfoCollectTaskLogRequest;
import com.treefinance.saas.taskcenter.facade.result.common.TaskResult;
import com.treefinance.saas.taskcenter.facade.service.CarInfoFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author haojiahong
 * @date 2018/9/26
 */
@Component("carInfoFacade")
public class CarInfoFacadeImpl extends AbstractFacade implements CarInfoFacade {

    @Autowired
    private CarInfoService carInfoService;

    @Override
    public TaskResult<Void> updateCollectTaskStatusAndTaskLogAndSendMonitor(Long taskId, List<CarInfoCollectTaskLogRequest> logList) {
        if (taskId == null) {
            throw new BusinessCheckFailException("-1", "taskId不能为空");
        }
        List<CarInfoCollectTaskLogDTO> carInfoCollectTaskLogDTOList = convert(logList, CarInfoCollectTaskLogDTO.class);
        carInfoService.updateCollectTaskStatusAndTaskLogAndSendMonitor(taskId, carInfoCollectTaskLogDTOList);
        return TaskResult.wrapSuccessfulResult(null);
    }
}

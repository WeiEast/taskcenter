package com.treefinance.saas.taskcenter.facade.impl;

import com.treefinance.saas.taskcenter.biz.service.AcquisitionService;
import com.treefinance.saas.taskcenter.exception.BusinessCheckFailException;
import com.treefinance.saas.taskcenter.facade.request.AcquisitionRequest;
import com.treefinance.saas.taskcenter.facade.result.common.TaskResult;
import com.treefinance.saas.taskcenter.facade.service.AcquisitionFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author haojiahong
 * @date 2018/9/27
 */
@Component("acquisitionFacade")
public class AcquisitionFacadeImpl implements AcquisitionFacade {

    @Autowired
    private AcquisitionService acquisitionService;

    @Override
    public TaskResult<Void> acquisition(AcquisitionRequest request) {
        if (request == null) {
            throw new BusinessCheckFailException("-1", "请求参数不能为空");
        }
        acquisitionService.acquisition(request.getTaskId(), request.getHeader(), request.getCookie(), request.getUrl(),
                request.getWebsite(), request.getAccountNo(), request.getTopic());
        return TaskResult.wrapSuccessfulResult(null);
    }
}

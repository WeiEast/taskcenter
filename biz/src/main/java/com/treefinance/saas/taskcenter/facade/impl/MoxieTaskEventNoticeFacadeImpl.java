package com.treefinance.saas.taskcenter.facade.impl;

import com.treefinance.saas.taskcenter.biz.service.MoxieBusinessService;
import com.treefinance.saas.taskcenter.dto.moxie.MoxieTaskEventNoticeDTO;
import com.treefinance.saas.taskcenter.facade.request.MoxieTaskEventNoticeRequest;
import com.treefinance.saas.taskcenter.facade.result.common.TaskResult;
import com.treefinance.saas.taskcenter.facade.service.MoxieTaskEventNoticeFacade;
import com.treefinance.saas.taskcenter.facade.validate.Preconditions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author haojiahong
 * @date 2018/9/28
 */
@Component("moxieTaskEventNoticeFacade")
public class MoxieTaskEventNoticeFacadeImpl extends AbstractFacade implements MoxieTaskEventNoticeFacade {

    @Autowired
    private MoxieBusinessService moxieBusinessService;

    @Override
    public TaskResult<Void> loginSuccess(MoxieTaskEventNoticeRequest request) {
        Preconditions.notNull("request", request);
        MoxieTaskEventNoticeDTO moxieTaskEventNoticeDTO = convertStrict(request, MoxieTaskEventNoticeDTO.class);
        moxieBusinessService.loginSuccess(moxieTaskEventNoticeDTO);
        return TaskResult.wrapSuccessfulResult(null);
    }

    @Override
    public TaskResult<Void> loginFail(MoxieTaskEventNoticeRequest request) {
        Preconditions.notNull("request", request);
        MoxieTaskEventNoticeDTO moxieTaskEventNoticeDTO = convertStrict(request, MoxieTaskEventNoticeDTO.class);
        moxieBusinessService.loginFail(moxieTaskEventNoticeDTO);
        return TaskResult.wrapSuccessfulResult(null);
    }

    @Override
    public TaskResult<Void> grabFail(MoxieTaskEventNoticeRequest request) {
        Preconditions.notNull("request", request);
        MoxieTaskEventNoticeDTO moxieTaskEventNoticeDTO = convertStrict(request, MoxieTaskEventNoticeDTO.class);
        moxieBusinessService.grabFail(moxieTaskEventNoticeDTO);
        return TaskResult.wrapSuccessfulResult(null);
    }

    @Override
    public TaskResult<Void> bill(MoxieTaskEventNoticeRequest request) {
        Preconditions.notNull("request", request);
        MoxieTaskEventNoticeDTO moxieTaskEventNoticeDTO = convertStrict(request, MoxieTaskEventNoticeDTO.class);
        moxieBusinessService.bill(moxieTaskEventNoticeDTO);
        return TaskResult.wrapSuccessfulResult(null);
    }

}

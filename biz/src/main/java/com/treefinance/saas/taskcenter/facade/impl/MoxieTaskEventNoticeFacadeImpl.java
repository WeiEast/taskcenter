package com.treefinance.saas.taskcenter.facade.impl;

import com.treefinance.saas.taskcenter.biz.service.MoxieBusinessService;
import com.treefinance.saas.taskcenter.dto.moxie.MoxieTaskEventNoticeMessage;
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
        MoxieTaskEventNoticeMessage moxieTaskEventNoticeMessage = convertStrict(request, MoxieTaskEventNoticeMessage.class);
        moxieBusinessService.loginSuccess(moxieTaskEventNoticeMessage);
        return TaskResult.wrapSuccessfulResult(null);
    }

    @Override
    public TaskResult<Void> loginFail(MoxieTaskEventNoticeRequest request) {
        Preconditions.notNull("request", request);
        MoxieTaskEventNoticeMessage moxieTaskEventNoticeMessage = convertStrict(request, MoxieTaskEventNoticeMessage.class);
        moxieBusinessService.loginFail(moxieTaskEventNoticeMessage);
        return TaskResult.wrapSuccessfulResult(null);
    }

    @Override
    public TaskResult<Void> grabFail(MoxieTaskEventNoticeRequest request) {
        Preconditions.notNull("request", request);
        MoxieTaskEventNoticeMessage moxieTaskEventNoticeMessage = convertStrict(request, MoxieTaskEventNoticeMessage.class);
        moxieBusinessService.grabFail(moxieTaskEventNoticeMessage);
        return TaskResult.wrapSuccessfulResult(null);
    }

    @Override
    public TaskResult<Void> bill(MoxieTaskEventNoticeRequest request) {
        Preconditions.notNull("request", request);
        MoxieTaskEventNoticeMessage moxieTaskEventNoticeMessage = convertStrict(request, MoxieTaskEventNoticeMessage.class);
        moxieBusinessService.bill(moxieTaskEventNoticeMessage);
        return TaskResult.wrapSuccessfulResult(null);
    }

}

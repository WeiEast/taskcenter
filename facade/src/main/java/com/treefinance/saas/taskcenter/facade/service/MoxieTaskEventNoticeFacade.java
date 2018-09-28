package com.treefinance.saas.taskcenter.facade.service;

import com.treefinance.saas.taskcenter.facade.request.MoxieTaskEventNoticeRequest;
import com.treefinance.saas.taskcenter.facade.result.common.TaskResult;

/**
 * @author haojiahong
 * @date 2018/9/28
 */
public interface MoxieTaskEventNoticeFacade {

    TaskResult<Void> loginSuccess(MoxieTaskEventNoticeRequest eventNoticeRequest);

    TaskResult<Void> loginFail(MoxieTaskEventNoticeRequest eventNoticeRequest);

    TaskResult<Void> grabFail(MoxieTaskEventNoticeRequest eventNoticeRequest);

    TaskResult<Void> bill(MoxieTaskEventNoticeRequest eventNoticeRequest);
}

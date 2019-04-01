/*
 * Copyright © 2015 - 2017 杭州大树网络技术有限公司. All Rights Reserved
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package com.treefinance.saas.taskcenter.interation.manager.dubbo;

import com.alibaba.fastjson.JSON;
import com.treefinance.saas.merchant.facade.request.grapserver.GetAppLicenseRequest;
import com.treefinance.saas.merchant.facade.request.grapserver.GetCallbackLicenseRequest;
import com.treefinance.saas.merchant.facade.result.console.MerchantResult;
import com.treefinance.saas.merchant.facade.result.grapsever.AppLicenseResult;
import com.treefinance.saas.merchant.facade.result.grapsever.CallbackLicenseResult;
import com.treefinance.saas.merchant.facade.service.AppLicenseFacade;
import com.treefinance.saas.taskcenter.interation.manager.RpcActionEnum;
import com.treefinance.saas.taskcenter.interation.manager.LicenseManager;
import com.treefinance.saas.taskcenter.interation.manager.domain.AppLicense;
import com.treefinance.saas.taskcenter.interation.manager.domain.CallbackLicense;
import com.treefinance.toolkit.util.Preconditions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Nonnull;

/**
 * @author Jerry
 * @date 2018/12/19 11:36
 */
@Service
public class LicenseServiceAdapter extends AbstractMerchantServiceAdapter implements LicenseManager {

    private final AppLicenseFacade appLicenseFacade;

    @Autowired
    public LicenseServiceAdapter(AppLicenseFacade appLicenseFacade) {
        this.appLicenseFacade = appLicenseFacade;
    }

    @Override
    public AppLicense getAppLicenseByAppId(@Nonnull String appId) {
        Preconditions.notEmpty("appId", appId);

        GetAppLicenseRequest request = new GetAppLicenseRequest();
        request.setAppId(appId);
        MerchantResult<AppLicenseResult> result = appLicenseFacade.getAppLicense(request);
        logger.info("queryAppLicenseByAppId : request={}, result={}", JSON.toJSONString(request), JSON.toJSONString(result));

        validateResponseEntity(result, RpcActionEnum.QUERY_APP_LICENSE, request);

        return convert(result.getData(), AppLicense.class);
    }

    @Override
    public CallbackLicense getCallbackLicenseByCallbackId(Integer callbackId) {
        Preconditions.notNull("callbackId", callbackId);

        GetCallbackLicenseRequest request = new GetCallbackLicenseRequest();
        request.setCallbackId(callbackId);

        MerchantResult<CallbackLicenseResult> result = appLicenseFacade.getCallbackLicense(request);
        logger.info("queryCallbackLicenseByCallbackId : request={}, result={}", JSON.toJSONString(request), JSON.toJSONString(result));

        validateResponseEntity(result, RpcActionEnum.QUERY_CALLBACK_LICENSE, request);

        return convert(result.getData(), CallbackLicense.class);
    }
}

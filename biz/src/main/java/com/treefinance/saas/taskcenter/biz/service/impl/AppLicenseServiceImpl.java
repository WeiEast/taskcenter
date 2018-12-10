/*
 * Copyright © 2015 - 2017 杭州大树网络技术有限公司. All Rights Reserved
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.treefinance.saas.taskcenter.biz.service.impl;

import com.alibaba.fastjson.JSON;
import com.treefinance.saas.merchant.facade.request.grapserver.GetAppLicenseRequest;
import com.treefinance.saas.merchant.facade.request.grapserver.GetCallbackLicenseRequest;
import com.treefinance.saas.merchant.facade.result.console.MerchantResult;
import com.treefinance.saas.merchant.facade.result.grapsever.AppLicenseResult;
import com.treefinance.saas.merchant.facade.result.grapsever.CallbackLicenseResult;
import com.treefinance.saas.merchant.facade.service.AppLicenseFacade;
import com.treefinance.saas.taskcenter.biz.service.AppLicenseService;
import com.treefinance.saas.taskcenter.context.component.AbstractService;
import com.treefinance.saas.taskcenter.dto.AppLicenseDTO;
import com.treefinance.saas.taskcenter.dto.CallBackLicenseDTO;
import com.treefinance.saas.taskcenter.exception.RpcServiceException;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import javax.annotation.Resource;

/**
 * @author Jerry
 * @since 19:14 25/04/2017
 */
@Component
public class AppLicenseServiceImpl extends AbstractService implements AppLicenseService {

    @Resource
    private AppLicenseFacade appLicenseFacade;


    @Override
    public AppLicenseDTO getAppLicense(@Nonnull String appId) {
        GetAppLicenseRequest request = new GetAppLicenseRequest();
        request.setAppId(appId);
        MerchantResult<AppLicenseResult> result = appLicenseFacade.getAppLicense(request);
        if (!result.isSuccess()) {
            throw new RpcServiceException("获取app的license失败！错误：" + result.getRetMsg());
        }

        AppLicenseResult appLicenseResult = result.getData();
        AppLicenseDTO appLicense = new AppLicenseDTO();
        BeanUtils.copyProperties(appLicenseResult, appLicense);

        logger.info(JSON.toJSONString(appLicense));

        return appLicense;
    }

    @Override
    public CallBackLicenseDTO getCallbackLicense(Integer callbackId) {
        GetCallbackLicenseRequest request = new GetCallbackLicenseRequest();
        request.setCallbackId(callbackId);

        MerchantResult<CallbackLicenseResult> result = appLicenseFacade.getCallbackLicense(request);
        if (!result.isSuccess()) {
            throw new RpcServiceException("获取callback的license失败！错误：" + result.getRetMsg());
        }

        CallbackLicenseResult appLicenseResult = result.getData();
        CallBackLicenseDTO dto = new CallBackLicenseDTO();
        BeanUtils.copyProperties(appLicenseResult, dto);

        logger.info(JSON.toJSONString(dto));

        return dto;
    }
}

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

package com.treefinance.saas.taskcenter.interation.manager;

import com.treefinance.saas.taskcenter.interation.manager.domain.AppLicense;
import com.treefinance.saas.taskcenter.interation.manager.domain.CallbackLicense;

import javax.annotation.Nonnull;

/**
 * @author Jerry
 * @date 2018/12/19 00:13
 */
public interface LicenseManager {

    /**
     * 根据appId查询license
     * 
     * @param appId 商户ID
     * @return license信息 {@link AppLicense}
     */
    AppLicense getAppLicenseByAppId(@Nonnull String appId);

    /**
     * 获取新版的callback数据加密license信息。
     * 
     * @param callbackId callbackConfigId
     * @return 新版的callback数据加密license信息
     */
    CallbackLicense getCallbackLicenseByCallbackId(Integer callbackId);
}

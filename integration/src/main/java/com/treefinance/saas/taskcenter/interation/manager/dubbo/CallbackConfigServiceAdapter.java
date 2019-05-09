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

import com.google.common.collect.ImmutableMap;
import com.treefinance.saas.merchant.facade.response.MerchantResponse;
import com.treefinance.saas.merchant.facade.result.CallbackConfigDTO;
import com.treefinance.saas.merchant.facade.service.AppCallbackConfigFacade;
import com.treefinance.saas.taskcenter.interation.manager.CallbackConfigManager;
import com.treefinance.saas.taskcenter.interation.manager.RpcActionEnum;
import com.treefinance.saas.taskcenter.interation.manager.domain.CallbackConfigBO;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Nonnull;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Jerry
 * @date 2018/12/11 01:20
 */
public class CallbackConfigServiceAdapter extends AbstractMerchantServiceAdapter implements CallbackConfigManager {

    private final AppCallbackConfigFacade appCallbackConfigFacade;

    @Autowired
    public CallbackConfigServiceAdapter(AppCallbackConfigFacade appCallbackConfigFacade) {
        this.appCallbackConfigFacade = appCallbackConfigFacade;
    }

    @Override
    public List<CallbackConfigBO> queryCallbackConfigsByAppId(@Nonnull String appId) {
        MerchantResponse<List<CallbackConfigDTO>> response = appCallbackConfigFacade.queryCallbackConfigsByAppId(appId);

        logger.debug("请求callback-config远程服务，appId: {}，结果: {}", appId, response);

        validateResponse(response, RpcActionEnum.QUERY_APP_CALLBACK_CONFIG_BY_APP_ID, ImmutableMap.of("appId", appId));

        return transform(response);
    }

    @Override
    public List<CallbackConfigBO> getAllCallbackConfigs() {
        MerchantResponse<List<CallbackConfigDTO>> response = appCallbackConfigFacade.getAllCallbackConfigs();

        logger.debug("请求callback-config远程服务，查询全部，结果: {}", response);

        validateResponse(response, RpcActionEnum.QUERY_APP_CALLBACK_CONFIG_ALL);

        return transform(response);
    }

    private List<CallbackConfigBO> transform(MerchantResponse<List<CallbackConfigDTO>> response) {
        final List<CallbackConfigDTO> list = response.getEntity();
        if (CollectionUtils.isEmpty(list)) {
            return Collections.emptyList();
        }
        return list.stream().map(dto -> {
            CallbackConfigBO bo = convertStrict(dto, CallbackConfigBO.class);
            final Byte[] bizTypes = dto.getBizTypes();
            if (ArrayUtils.isNotEmpty(bizTypes)) {
                bo.setBizTypes(Arrays.asList(bizTypes));
            }
            return bo;
        }).collect(Collectors.toList());
    }
}

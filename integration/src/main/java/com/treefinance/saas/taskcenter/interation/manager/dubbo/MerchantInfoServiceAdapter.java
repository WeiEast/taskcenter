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

package com.treefinance.saas.taskcenter.interation.manager.dubbo;

import com.google.common.collect.ImmutableMap;
import com.treefinance.saas.merchant.facade.response.MerchantResponse;
import com.treefinance.saas.merchant.facade.result.SimpleMerchantBaseDTO;
import com.treefinance.saas.merchant.facade.service.MerchantBaseFacade;
import com.treefinance.saas.taskcenter.interation.manager.MerchantInfoManager;
import com.treefinance.saas.taskcenter.interation.manager.RpcActionEnum;
import com.treefinance.saas.taskcenter.interation.manager.domain.MerchantBaseBO;
import com.treefinance.toolkit.util.Preconditions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Nonnull;

/**
 * @author Jerry
 * @date 2018/12/11 01:05
 */
@Service
public class MerchantInfoServiceAdapter extends AbstractMerchantServiceAdapter implements MerchantInfoManager {

    private final MerchantBaseFacade merchantBaseFacade;

    @Autowired
    public MerchantInfoServiceAdapter(MerchantBaseFacade merchantBaseFacade) {
        this.merchantBaseFacade = merchantBaseFacade;
    }

    @Override
    public MerchantBaseBO getMerchantBaseByAppId(@Nonnull String appId) {
        Preconditions.notEmpty("appId", appId);
        final MerchantResponse<SimpleMerchantBaseDTO> response = merchantBaseFacade.getActiveMerchantBaseByAppId(appId);

        validateResponseEntity(response, RpcActionEnum.QUERY_ACTIVE_MERCHANT_BASE_BY_APP_ID, ImmutableMap.of("appId", appId));

        return convert(response.getEntity(), MerchantBaseBO.class);
    }

}

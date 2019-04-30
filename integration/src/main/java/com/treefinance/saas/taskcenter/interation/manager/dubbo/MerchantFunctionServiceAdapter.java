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

import com.treefinance.saas.merchant.facade.request.console.MerchantFunctionRequest;
import com.treefinance.saas.merchant.facade.result.console.MerchantFunctionResult;
import com.treefinance.saas.merchant.facade.result.console.MerchantResult;
import com.treefinance.saas.merchant.facade.service.MerchantFunctionFacade;
import com.treefinance.saas.taskcenter.interation.manager.MerchantFunctionManager;
import com.treefinance.saas.taskcenter.interation.manager.RpcActionEnum;
import com.treefinance.saas.taskcenter.interation.manager.domain.MerchantFunctionBO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Nonnull;

/**
 * @author Jerry
 * @date 2019-04-17 17:01
 */
@Service
public class MerchantFunctionServiceAdapter extends AbstractMerchantServiceAdapter implements MerchantFunctionManager {

    @Autowired
    private MerchantFunctionFacade merchantFunctionFacade;

    @Override
    public MerchantFunctionBO getMerchantFunctionByAppId(@Nonnull String appId) {
        MerchantFunctionRequest request = new MerchantFunctionRequest();
        request.setAppId(appId);
        MerchantResult<MerchantFunctionResult> response = merchantFunctionFacade.getMerchantFunctionByAppId(request);

        validateResponseEntity(response, RpcActionEnum.QUERY_MERCHANT_FUNCTION_BY_APP_ID, request);

        return convertStrict(response.getData(), MerchantFunctionBO.class);
    }
}

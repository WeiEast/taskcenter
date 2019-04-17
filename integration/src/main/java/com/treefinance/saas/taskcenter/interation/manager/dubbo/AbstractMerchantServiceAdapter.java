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

import com.treefinance.saas.merchant.facade.response.MerchantResponse;
import com.treefinance.saas.merchant.facade.result.console.MerchantResult;
import com.treefinance.saas.taskcenter.exception.RpcServiceException;
import com.treefinance.saas.taskcenter.interation.manager.RpcActionEnum;

/**
 * @author Jerry
 * @date 2018/11/26 22:16
 */
public abstract class AbstractMerchantServiceAdapter extends AbstractDubboServiceAdapter {

    protected <T> void validateResponse(MerchantResult<T> result, RpcActionEnum action, Object... args) {
        super.validateResponse(result, action, args);

        if (!result.isSuccess()) {
            throw new RpcServiceException(
                "[MERCHANT] Error server! errorCode: " + result.getRetCode() + ", errorMsg: " + result.getRetMsg() + " - action: " + action + appendArgs(args));
        }
    }

    /**
     * 检查响应是否正常的基础上进一步检查响应的数据实体是否为空
     */
    protected <T> void validateResponseEntity(MerchantResult<T> result, RpcActionEnum action, Object... args) {
        validateResponse(result, action, args);

        if (result.getData() == null) {
            throw new RpcServiceException("[MERCHANT] Invalid response entity! - action:" + " " + action + appendArgs(args));
        }
    }

    protected <T> void validateResponse(MerchantResponse<T> response, RpcActionEnum action, Object... args) {
        super.validateResponse(response, action, args);

        if (!response.isSuccess()) {
            throw new RpcServiceException(
                "[MERCHANT] Error server! errorCode: " + response.getCode() + ", errorMsg: " + response.getMessage() + " - action: " + action + appendArgs(args));
        }
    }

    /**
     * 检查响应是否正常的基础上进一步检查响应的数据实体是否为空
     */
    protected <T> void validateResponseEntity(MerchantResponse<T> response, RpcActionEnum action, Object... args) {
        validateResponse(response, action, args);

        if (response.getEntity() == null) {
            throw new RpcServiceException("[MERCHANT] Invalid response entity! - action:" + " " + action + appendArgs(args));
        }
    }
}

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

import com.alibaba.fastjson.JSON;
import com.treefinance.saas.merchant.facade.request.common.BaseRequest;
import com.treefinance.saas.merchant.facade.request.grapserver.GetAppBizTypeRequest;
import com.treefinance.saas.merchant.facade.result.console.AppBizTypeResult;
import com.treefinance.saas.merchant.facade.result.console.MerchantResult;
import com.treefinance.saas.merchant.facade.service.AppBizTypeFacade;
import com.treefinance.saas.taskcenter.exception.IllegalBusinessDataException;
import com.treefinance.saas.taskcenter.interation.manager.BizTypeManager;
import com.treefinance.saas.taskcenter.interation.manager.RpcActionEnum;
import com.treefinance.saas.taskcenter.interation.manager.domain.BizTypeInfoBO;
import org.apache.commons.collections4.CollectionUtils;

import javax.annotation.Nonnull;

import java.util.List;

/**
 * @author Jerry
 * @date 2018/12/11 01:20
 */
public class BizTypeServiceAdapter extends AbstractMerchantServiceAdapter implements BizTypeManager {

    private final AppBizTypeFacade appBizTypeFacade;

    public BizTypeServiceAdapter(AppBizTypeFacade appBizTypeFacade) {
        this.appBizTypeFacade = appBizTypeFacade;
    }


    @Override
    public BizTypeInfoBO getBizTypeInfoByBizType(@Nonnull Byte bizType) {
        GetAppBizTypeRequest request = new GetAppBizTypeRequest();
        request.setBizType(bizType);
        MerchantResult<List<AppBizTypeResult>> result = appBizTypeFacade.queryAppBizTypeByBizType(request);

        if (logger.isDebugEnabled()) {
            logger.debug("请求biz-type远程服务，bizType: {}，结果: {}", bizType, JSON.toJSONString(result));
        }

        validateResponse(result, RpcActionEnum.QUERY_BIZ_TYPE_LIST_ASSIGNED, request);

        List<AppBizTypeResult> list = result.getData();
        AppBizTypeResult data = CollectionUtils.isNotEmpty(list) ? list.get(0) : null;

        if (data == null) {
            throw new IllegalBusinessDataException("Can not find any biz-type information! bizType: " + bizType);
        }

        return convert(data, BizTypeInfoBO.class);
    }

    @Override
    public List<BizTypeInfoBO> listBizTypes() {
        MerchantResult<List<AppBizTypeResult>> result = appBizTypeFacade.queryAllAppBizType(new BaseRequest());

        if (logger.isDebugEnabled()) {
            logger.debug("请求biz-type远程服务，查询全部，结果: {}", JSON.toJSONString(result));
        }

        validateResponse(result, RpcActionEnum.QUERY_BIZ_TYPE_LIST);

        return convert(result.getData(), BizTypeInfoBO.class);
    }

}

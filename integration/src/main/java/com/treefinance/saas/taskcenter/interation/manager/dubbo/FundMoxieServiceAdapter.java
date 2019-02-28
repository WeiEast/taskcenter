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
import com.google.common.collect.ImmutableMap;
import com.treefinance.saas.grapserver.facade.service.FundMoxieFacade;
import com.treefinance.saas.knife.result.SaasResult;
import com.treefinance.saas.taskcenter.interation.manager.FundMoxieManager;
import com.treefinance.saas.taskcenter.interation.manager.RpcActionEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Nonnull;

/**
 * @author Jerry
 * @date 2019-02-28 20:02
 */
@Service
public class FundMoxieServiceAdapter extends AbstractGrabServiceAdapter implements FundMoxieManager {
    private final FundMoxieFacade fundMoxieFacade;

    @Autowired
    public FundMoxieServiceAdapter(FundMoxieFacade fundMoxieFacade) {
        this.fundMoxieFacade = fundMoxieFacade;
    }

    @Override
    public String queryFundsEx(@Nonnull String moxieTaskId) {
        SaasResult<String> result = fundMoxieFacade.queryFundsEx(moxieTaskId);
        logger.info("queryFundsEx : moxieTaskId={}, result={}", moxieTaskId, JSON.toJSONString(result));

        validateResponseEntity(result, RpcActionEnum.QUERY_FUNDS_EX, ImmutableMap.of("moxieTaskId", moxieTaskId));

        return result.getData();
    }
}

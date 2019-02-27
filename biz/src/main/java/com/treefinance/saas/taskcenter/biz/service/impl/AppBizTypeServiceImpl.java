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

package com.treefinance.saas.taskcenter.biz.service.impl;

import com.alibaba.fastjson.JSON;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.treefinance.saas.merchant.facade.request.common.BaseRequest;
import com.treefinance.saas.merchant.facade.request.grapserver.GetAppBizTypeRequest;
import com.treefinance.saas.merchant.facade.result.console.AppBizTypeResult;
import com.treefinance.saas.merchant.facade.result.console.MerchantResult;
import com.treefinance.saas.merchant.facade.service.AppBizTypeFacade;
import com.treefinance.saas.taskcenter.biz.service.AppBizTypeService;
import com.treefinance.saas.taskcenter.context.component.AbstractService;
import com.treefinance.saas.taskcenter.dto.AppBizType;
import com.treefinance.saas.taskcenter.exception.RpcServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;

import javax.annotation.Nonnull;
import javax.annotation.Resource;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Created by yh-treefinance on 2017/8/2.
 */
@Service
public class AppBizTypeServiceImpl extends AbstractService implements AppBizTypeService, InitializingBean {
    /**
     * logger
     */
    private static final Logger logger = LoggerFactory.getLogger(AppBizTypeServiceImpl.class);

    @Resource
    private AppBizTypeFacade appBizTypeFacade;

    /**
     * 本地缓存
     */
    private final LoadingCache<Byte, AppBizType> cache =
        CacheBuilder.newBuilder().refreshAfterWrite(5, TimeUnit.MINUTES).expireAfterWrite(5, TimeUnit.MINUTES).build(new CacheLoader<Byte, AppBizType>() {
            @Override
            public AppBizType load(Byte bizType) throws Exception {
                GetAppBizTypeRequest getAppBizTypeRequest = new GetAppBizTypeRequest();
                getAppBizTypeRequest.setBizType(bizType);
                MerchantResult<List<AppBizTypeResult>> merchantResult = appBizTypeFacade.queryAppBizTypeByBizType(getAppBizTypeRequest);
                if (merchantResult.isSuccess()) {
                    List<AppBizType> list = convert(merchantResult.getData(), AppBizType.class);
                    logger.info("load biz-type list into local cache from remote service. bizType={},data={}", bizType, JSON.toJSONString(list));
                    return list.get(0);
                } else {
                    throw new RpcServiceException("Failed querying biz-type list! bizType: " + bizType + ", errorMsg:" + " " + merchantResult.getRetMsg());
                }
            }
        });

    @Override
    public AppBizType getAppBizType(@Nonnull Byte bizType) {
        return cache.getUnchecked(bizType);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        BaseRequest getAppBizTypeRequest = new BaseRequest();

        MerchantResult<List<AppBizTypeResult>> merchantResult = appBizTypeFacade.queryAllAppBizType(getAppBizTypeRequest);
        if (merchantResult.isSuccess()) {
            List<AppBizTypeResult> results = merchantResult.getData();

            List<AppBizType> list = convert(results, AppBizType.class);

            this.cache.putAll(list.stream().collect(Collectors.toMap(AppBizType::getBizType, appBizType -> appBizType)));

            logger.info("加载业务类型数据：list={}", list);
        } else {
            logger.warn("Error invoking app's BizType load service! >>> {}", merchantResult.getRetMsg());
        }
    }
}

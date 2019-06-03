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

package com.treefinance.saas.taskcenter.interation.manager.cacheable;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.treefinance.saas.merchant.facade.service.AppBizTypeFacade;
import com.treefinance.saas.taskcenter.interation.manager.domain.BizTypeInfoBO;
import com.treefinance.saas.taskcenter.interation.manager.dubbo.BizTypeServiceAdapter;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Nonnull;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author Jerry
 * @date 2019-02-28 20:29
 */
@Service
public class CachedBizTypeManagerImpl extends BizTypeServiceAdapter implements InitializingBean {

    /**
     * 本地缓存
     */
    private final LoadingCache<Byte, BizTypeInfoBO> cache =
        CacheBuilder.newBuilder().refreshAfterWrite(5, TimeUnit.MINUTES).expireAfterWrite(5, TimeUnit.MINUTES).build(new CacheLoader<Byte, BizTypeInfoBO>() {
            @Override
            public BizTypeInfoBO load(@Nonnull Byte bizType) {
                return CachedBizTypeManagerImpl.super.getBizTypeInfoByBizType(bizType);
            }
        });

    @Autowired
    public CachedBizTypeManagerImpl(AppBizTypeFacade appBizTypeFacade) {
        super(appBizTypeFacade);
    }

    @Override
    public void afterPropertiesSet() {
        try {
            List<BizTypeInfoBO> list = listBizTypes();
            logger.info("加载业务类型数据：list={}", list);
            if (CollectionUtils.isNotEmpty(list)) {
                this.cache.putAll(list.stream().collect(Collectors.toMap(BizTypeInfoBO::getBizType, Function.identity(), (a, b) -> b)));
            }
        } catch (Exception e) {
            logger.error("Error invoking app's BizType loading service!", e);
        }
    }

    @Override
    public BizTypeInfoBO getBizTypeInfoByBizType(@Nonnull Byte bizType) {
        return cache.getUnchecked(bizType);
    }

}

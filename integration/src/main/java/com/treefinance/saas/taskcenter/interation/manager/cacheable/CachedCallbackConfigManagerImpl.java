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

import com.alibaba.fastjson.JSON;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.treefinance.saas.merchant.facade.service.AppCallbackConfigFacade;
import com.treefinance.saas.taskcenter.interation.manager.domain.CallbackConfigBO;
import com.treefinance.saas.taskcenter.interation.manager.dubbo.CallbackConfigServiceAdapter;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;

import javax.annotation.Nonnull;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author Jerry
 * @date 2019-04-17 16:01
 */
@Service
public class CachedCallbackConfigManagerImpl extends CallbackConfigServiceAdapter implements CachedCallbackConfigManager, InitializingBean {

    /**
     * 本地缓存<appId,callbackConfig>
     */
    private final LoadingCache<String, List<CallbackConfigBO>> callbackCache =
        CacheBuilder.newBuilder().refreshAfterWrite(5, TimeUnit.MINUTES).expireAfterAccess(5, TimeUnit.MINUTES).build(new CacheLoader<String, List<CallbackConfigBO>>() {
            @Override
            public List<CallbackConfigBO> load(@Nonnull String appid) {
                List<CallbackConfigBO> list = CachedCallbackConfigManagerImpl.super.queryCallbackConfigsByAppId(appid);

                logger.info("load local cache of callback-configs : appId={}, license={}", appid, JSON.toJSONString(list));

                return list;
            }
        });

    public CachedCallbackConfigManagerImpl(AppCallbackConfigFacade appCallbackConfigFacade) {
        super(appCallbackConfigFacade);
    }

    @Override
    public void afterPropertiesSet() {
        // 初始化app_callback
        try {
            List<CallbackConfigBO> configs = super.getAllCallbackConfigs();
            logger.info("加载callback信息 >> {}", JSON.toJSONString(configs));
            if (CollectionUtils.isNotEmpty(configs)) {
                this.callbackCache.putAll(configs.stream().collect(Collectors.groupingBy(CallbackConfigBO::getAppId)));
            }
        } catch (Exception e) {
            logger.error("加载callback信息发生错误！", e);
        }
    }

    @Override
    public void refreshCache(@Nonnull String appId) {
        this.callbackCache.refresh(appId);
    }

    @Override
    public List<CallbackConfigBO> queryCallbackConfigsByAppId(@Nonnull String appId) {
        return this.callbackCache.getUnchecked(appId);
    }
}

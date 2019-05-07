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

package com.treefinance.saas.taskcenter.service.impl;

import com.alibaba.fastjson.JSON;
import com.treefinance.saas.assistant.variable.notify.client.VariableMessageHandler;
import com.treefinance.saas.assistant.variable.notify.model.VariableMessage;
import com.treefinance.saas.merchant.facade.constance.AppConsts;
import com.treefinance.saas.taskcenter.context.enums.EDataType;
import com.treefinance.saas.taskcenter.interation.manager.cacheable.CachedCallbackConfigManager;
import com.treefinance.saas.taskcenter.interation.manager.domain.CallbackConfigBO;
import com.treefinance.saas.taskcenter.service.AppCallbackConfigService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Nonnull;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Created by luoyihua on 2017/5/11.
 */
@Service
public class AppCallbackConfigServiceImpl extends AbstractService implements AppCallbackConfigService, VariableMessageHandler {

    @Autowired
    private CachedCallbackConfigManager callbackConfigManager;

    @Override
    public String getVariableName() {
        return "merchant-callback";
    }

    @Override
    public void handleMessage(VariableMessage variableMessage) {
        logger.info("收到配置更新消息：config={}", JSON.toJSONString(variableMessage));
        String appId = variableMessage.getVariableId();
        if (StringUtils.isEmpty(appId)) {
            logger.error("处理配置更新消息失败：VariableId非法，config={}", JSON.toJSONString(variableMessage));
            return;
        }
        callbackConfigManager.refreshCache(appId);
    }

    @Override
    public List<CallbackConfigBO> queryConfigsByAppIdAndBizType(String appId, @Nonnull Byte bizType, @Nonnull EDataType dataType) {
        // 查询所有回调
        if (StringUtils.isEmpty(appId)) {
            return Collections.emptyList();
        }

        List<CallbackConfigBO> configs = callbackConfigManager.queryCallbackConfigsByAppId(appId);
        logger.info("从本地缓存中获取appId={}的回调配置为list={}", appId, JSON.toJSONString(configs));

        if (CollectionUtils.isEmpty(configs)) {
            return Collections.emptyList();
        }

        // 过滤data_type
        configs = configs.stream().filter(Objects::nonNull).filter(config -> dataType.getType().equals(config.getDataType())).collect(Collectors.toList());

        // 根据业务类型匹配：如果存在此类型回调则使用，不存在则使用默认
        List<CallbackConfigBO> list = configs.stream().filter(config -> config.getBizTypes().contains(bizType)).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(list)) {
            list = configs.stream().filter(config -> config.getBizTypes().contains(AppConsts.DEFAULT_BIZ_TYPE)).collect(Collectors.toList());
        }

        logger.info("根据业务类型匹配回调配置结果:bizConfigs={}", JSON.toJSONString(list));

        return list;
    }

}

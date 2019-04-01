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
import com.google.common.collect.Lists;
import com.google.common.util.concurrent.UncheckedExecutionException;
import com.treefinance.saas.assistant.variable.notify.client.VariableMessageHandler;
import com.treefinance.saas.assistant.variable.notify.model.VariableMessage;
import com.treefinance.saas.merchant.facade.request.common.BaseRequest;
import com.treefinance.saas.merchant.facade.request.grapserver.GetAppCallBackBizByCallbackIdRequest;
import com.treefinance.saas.merchant.facade.request.grapserver.GetAppCallBackConfigByIdRequest;
import com.treefinance.saas.merchant.facade.result.console.MerchantResult;
import com.treefinance.saas.merchant.facade.result.grapsever.AppCallbackBizResult;
import com.treefinance.saas.merchant.facade.result.grapsever.AppCallbackResult;
import com.treefinance.saas.merchant.facade.service.AppCallBackBizFacade;
import com.treefinance.saas.merchant.facade.service.AppCallbackConfigFacade;
import com.treefinance.saas.taskcenter.biz.service.AppCallbackConfigService;
import com.treefinance.saas.taskcenter.biz.service.AbstractService;
import com.treefinance.saas.taskcenter.context.enums.EDataType;
import com.treefinance.saas.taskcenter.dto.AppCallbackBizDTO;
import com.treefinance.saas.taskcenter.dto.AppCallbackConfigDTO;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Created by luoyihua on 2017/5/11.
 */
@Service
public class AppCallbackConfigServiceImpl extends AbstractService implements AppCallbackConfigService, InitializingBean, VariableMessageHandler {

    @Autowired
    private AppCallbackConfigFacade appCallbackConfigFacade;

    @Autowired
    private AppCallBackBizFacade appCallBackBizFacade;
    /**
     * 本地缓存<callbackId,callbackType>
     */
    private final LoadingCache<Integer, List<AppCallbackBizDTO>> callbackTypeCache =
        CacheBuilder.newBuilder().refreshAfterWrite(5, TimeUnit.MINUTES).expireAfterAccess(5, TimeUnit.MINUTES).build(new CacheLoader<Integer, List<AppCallbackBizDTO>>() {
            @Override
            public List<AppCallbackBizDTO> load(Integer callbackId) throws Exception {
                GetAppCallBackBizByCallbackIdRequest getAppCallBackBizByCallbackIdRequest = new GetAppCallBackBizByCallbackIdRequest();
                getAppCallBackBizByCallbackIdRequest.setCallbackId(callbackId);
                MerchantResult<List<AppCallbackBizResult>> listMerchantResult = appCallBackBizFacade.queryAppCallBackByCallbackId(getAppCallBackBizByCallbackIdRequest);
                List<AppCallbackBizDTO> list = convert(listMerchantResult.getData(), AppCallbackBizDTO.class);
                if (!listMerchantResult.isSuccess()) {
                    logger.info("load local cache of callback-types  false: error message={}", listMerchantResult.getRetMsg());
                    list = Lists.newArrayList();
                }
                logger.info("load local cache of callback-types : callbackId={}, callbackType={}", callbackId, JSON.toJSONString(list));
                return list;
            }
        });
    /**
     * 本地缓存<appId,callbackConfig>
     */
    private final LoadingCache<String, List<AppCallbackConfigDTO>> callbackCache =
        CacheBuilder.newBuilder().refreshAfterWrite(5, TimeUnit.MINUTES).expireAfterAccess(5, TimeUnit.MINUTES).build(new CacheLoader<String, List<AppCallbackConfigDTO>>() {
            @Override
            public List<AppCallbackConfigDTO> load(String appid) throws Exception {
                GetAppCallBackConfigByIdRequest getAppCallBackConfigByIdRequest = new GetAppCallBackConfigByIdRequest();
                getAppCallBackConfigByIdRequest.setAppId(appid);
                MerchantResult<List<AppCallbackResult>> listMerchantResult = appCallbackConfigFacade.queryAppCallBackConfigByAppId(getAppCallBackConfigByIdRequest);
                List<AppCallbackConfigDTO> list = convert(listMerchantResult.getData(), AppCallbackConfigDTO.class);

                if (!listMerchantResult.isSuccess()) {
                    logger.info("load local cache of callback-configs  false: error message={}", listMerchantResult.getRetMsg());
                    list = Lists.newArrayList();
                }
                logger.info("load local cache of callback-configs : appid={}, license={}", appid, JSON.toJSONString(list));
                // 刷新类型
                list.forEach(appCallbackConfig -> callbackTypeCache.refresh(appCallbackConfig.getId()));
                return list;
            }
        });

    @Override
    public void afterPropertiesSet() throws Exception {
        // 1. 初始化appCallback
        BaseRequest request = new BaseRequest();
        MerchantResult<List<AppCallbackResult>> listMerchantResult = appCallbackConfigFacade.queryAllAppCallBackConfig(request);
        List<AppCallbackConfigDTO> list = convert(listMerchantResult.getData(), AppCallbackConfigDTO.class);

        logger.info("加载callback信息: callback={}", JSON.toJSONString(list));
        if (CollectionUtils.isEmpty(list)) {
            return;
        }

        this.callbackCache.putAll(list.stream().collect(Collectors.groupingBy(AppCallbackConfigDTO::getAppId)));

        // 2. 初始化callBackType
        BaseRequest baseRequest = new BaseRequest();
        MerchantResult<List<AppCallbackBizResult>> listMerchantResult1 = appCallBackBizFacade.queryAllAppCallBack(baseRequest);
        List<AppCallbackBizDTO> appCallbackBizList = convert(listMerchantResult1.getData(), AppCallbackBizDTO.class);

        logger.info("加载callbackType信息: callbackType={}", JSON.toJSONString(appCallbackBizList));
        if (CollectionUtils.isEmpty(appCallbackBizList)) {
            return;
        }

        this.callbackTypeCache.putAll(appCallbackBizList.stream().collect(Collectors.groupingBy(AppCallbackBizDTO::getCallbackId)));
    }

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
        this.callbackCache.refresh(appId);
    }

    /**
     * 获取指定业务类型的回调配置： 如果有配置该业务类型，则使用该业务类型；没有则使用全局配置
     *
     * @param appId
     * @param bizType
     * @return
     */
    @Override
    public List<AppCallbackConfigDTO> queryConfigsByAppIdAndBizType(String appId, Byte bizType, EDataType dataType) {
        // 查询所有回调
        List<AppCallbackConfigDTO> list = getByAppId(appId);
        if (CollectionUtils.isEmpty(list)) {
            return Collections.emptyList();
        }

        List<Integer> bizCallbackIds = getCallbackIdsInBizList(list, bizType, dataType);
        if (CollectionUtils.isEmpty(bizCallbackIds)) {
            return Collections.emptyList();
        }

        List<AppCallbackConfigDTO> bizConfigs = list.stream().filter(config -> bizCallbackIds.contains(config.getId())).collect(Collectors.toList());

        logger.info("根据业务类型匹配回调配置结果:bizConfigs={}", JSON.toJSONString(bizConfigs));

        return bizConfigs;
    }

    private List<Integer> getCallbackIdsInBizList(List<AppCallbackConfigDTO> list, Byte bizType, EDataType dataType) {
        // 查询回调类型
        List<AppCallbackBizDTO> callbackBizs = queryCallbackBizList(list, bizType, dataType);
        if (CollectionUtils.isEmpty(callbackBizs)) {
            return Collections.emptyList();
        }

        Map<Byte, List<Integer>> map =
            callbackBizs.stream().collect(Collectors.groupingBy(AppCallbackBizDTO::getBizType, Collectors.mapping(AppCallbackBizDTO::getCallbackId, Collectors.toList())));

        List<Integer> bizCallbackIds = map.get(bizType);
        if (bizCallbackIds == null) {
            // 根据业务类型匹配：如果存在此类型回调则使用，不存在则使用默认
            Byte defaultType = Byte.valueOf("0");
            bizCallbackIds = map.get(defaultType);
        }

        return bizCallbackIds;
    }

    private List<AppCallbackBizDTO> queryCallbackBizList(List<AppCallbackConfigDTO> configs, Byte bizType, EDataType dataType) {
        Byte defaultType = Byte.valueOf("0");
        // 2.查询回调类型
        List<Integer> callbackIds =
            configs.stream().filter(config -> config != null && dataType.getType().equals(config.getDataType())).map(AppCallbackConfigDTO::getId).collect(Collectors.toList());

        return getCallbackTypeList(callbackIds).stream().filter(appCallbackBiz -> bizType.equals(appCallbackBiz.getBizType()) || defaultType.equals(appCallbackBiz.getBizType()))
            .collect(Collectors.toList());
    }

    /**
     * 根据ID获取
     *
     * @param callbackIds
     * @return
     */
    private List<AppCallbackBizDTO> getCallbackTypeList(List<Integer> callbackIds) {
        if (CollectionUtils.isEmpty(callbackIds)) {
            return Collections.emptyList();
        }

        Map<Integer, List<AppCallbackBizDTO>> map;
        try {
            map = this.callbackTypeCache.getAll(callbackIds);
        } catch (ExecutionException e) {
            throw new UncheckedExecutionException(e.getCause());
        }
        logger.info("从本地缓存中获取callbackIds={}的回调业务类型数据map={}", JSON.toJSONString(callbackIds), JSON.toJSONString(map));
        List<AppCallbackBizDTO> list = Lists.newArrayList();
        map.forEach((callbackId, typeList) -> {
            if (!CollectionUtils.isEmpty(typeList)) {
                list.addAll(typeList);
            }
        });

        return list;
    }

    /**
     * 根据appId获取
     *
     * @param appId
     * @return
     */
    private List<AppCallbackConfigDTO> getByAppId(String appId) {
        if (StringUtils.isEmpty(appId)) {
            return Collections.emptyList();
        }

        List<AppCallbackConfigDTO> list = this.callbackCache.getUnchecked(appId);
        logger.info("从本地缓存中获取appId={}的回调配置为list={}", appId, JSON.toJSONString(list));
        return list;
    }
}

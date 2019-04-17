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
import com.google.common.collect.Maps;
import com.treefinance.b2b.saas.util.DataUtils;
import com.treefinance.saas.taskcenter.service.AppCallbackConfigService;
import com.treefinance.saas.taskcenter.biz.service.GrapDataCallbackService;
import com.treefinance.saas.taskcenter.service.TaskCallbackLogService;
import com.treefinance.saas.taskcenter.biz.service.TaskLogService;
import com.treefinance.saas.taskcenter.biz.service.TaskService;
import com.treefinance.saas.taskcenter.context.enums.EDataType;
import com.treefinance.saas.taskcenter.context.enums.EGrapStatus;
import com.treefinance.saas.taskcenter.dto.AsycGrapDTO;
import com.treefinance.saas.taskcenter.dto.TaskDTO;
import com.treefinance.saas.taskcenter.exception.RequestFailedException;
import com.treefinance.saas.taskcenter.interation.manager.LicenseManager;
import com.treefinance.saas.taskcenter.interation.manager.domain.AppLicense;
import com.treefinance.saas.taskcenter.interation.manager.domain.CallbackConfigBO;
import com.treefinance.saas.taskcenter.util.HttpClientUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.net.URLEncoder;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 爬取数据回调Service Created by yh-treefinance on 2017/12/25.
 */
@Service
public class GrapDataCallbackServiceImpl implements GrapDataCallbackService {
    /**
     * logger
     */
    protected final Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    protected TaskCallbackLogService taskCallbackLogService;
    @Autowired
    private TaskService taskService;
    @Autowired
    private TaskLogService taskLogService;
    @Autowired
    private AppCallbackConfigService appCallbackConfigService;
    @Autowired
    private LicenseManager licenseManager;

    /**
     * 异步爬取数据处理
     */
    @Override
    public void handleAyscData(AsycGrapDTO asycGrapDTO) {
        Assert.notNull(asycGrapDTO, "taskId can't be null : json=" + JSON.toJSONString(asycGrapDTO));
        Assert.notNull(asycGrapDTO.getTaskId(), "taskId can't be null : json=" + JSON.toJSONString(asycGrapDTO));
        Assert.notNull(asycGrapDTO.getDataType(), "dataType can't be null : json=" + JSON.toJSONString(asycGrapDTO));

        EDataType dataType = EDataType.typeOf(asycGrapDTO.getDataType().byteValue());
        Assert.notNull(dataType, "dataType is illegal : json=" + JSON.toJSONString(asycGrapDTO));

        Long taskId = asycGrapDTO.getTaskId();
        // 1.获取任务
        TaskDTO taskDTO = taskService.getById(taskId);
        if (taskDTO == null) {
            logger.info("{} callback failed : task {} not exists, message={}...", dataType.name(), taskId, JSON.toJSONString(asycGrapDTO));
            return;
        }
        String appId = taskDTO.getAppId();
        // 3.获取商户密钥
        AppLicense appLicense = licenseManager.getAppLicenseByAppId(appId);

        List<CallbackConfigBO> callbackConfigs = getCallbackConfigs(taskDTO, dataType);
        if (CollectionUtils.isEmpty(callbackConfigs)) {
            logger.info("{} callback failed :taskId={}, callbackConfigs of {} is null, message={}...", dataType.name(), taskId, appId, JSON.toJSONString(asycGrapDTO));
            return;
        }
        Map<String, Object> dataMap = Maps.newHashMap();
        // 填充uniqueId、taskId、taskStatus
        dataMap.put("taskId", taskDTO.getId());
        dataMap.put("taskStatus", EGrapStatus.SUCCESS.getCode());
        dataMap.put("taskErrorMsg", "");
        dataMap.put("uniqueId", taskDTO.getUniqueId());
        dataMap.put("dataUrl", asycGrapDTO.getDataUrl());
        dataMap.put("dataType", asycGrapDTO.getDataType());
        dataMap.put("dataSize", asycGrapDTO.getDataSize());
        dataMap.put("expiredTime", asycGrapDTO.getExpiredTime());
        dataMap.put("timestamp", asycGrapDTO.getTimestamp());

        // 4.爬取成功，下载数据
        boolean isSuccess = Integer.valueOf(1).equals(asycGrapDTO.getStatus());
        if (isSuccess) {
            if (StringUtils.isEmpty(asycGrapDTO.getDataUrl())) {
                dataMap.put("taskStatus", EGrapStatus.RESULT_EMPTY.getCode());
                dataMap.put("taskErrorMsg", EGrapStatus.RESULT_EMPTY.getName());
            }
        } else {
            dataMap.put("taskStatus", EGrapStatus.FAIL.getCode());
            dataMap.put("taskErrorMsg", EGrapStatus.FAIL.getName());
        }
        if (logger.isDebugEnabled()) {
            logger.debug("{} callback : generate dataMap  {} ", dataType.name(), JSON.toJSONString(dataMap));
        }
        // 5.回调 aes加密
        String key = appLicense.getServerPublicKey();
        for (CallbackConfigBO configDTO : callbackConfigs) {
            // 任务成功但是成功不通知
            if (isSuccess && !Byte.valueOf("1").equals(configDTO.getIsNotifySuccess())) {
                logger.info("{} callback : 任务成功，但是成功不通知...taskId={}, config={}", dataType.name(), taskId, JSON.toJSONString(configDTO));
                continue;
            }
            // 任务失败但是失败不通知
            else if (!isSuccess && !Byte.valueOf("1").equals(configDTO.getIsNotifyFailure())) {
                logger.info("{} callback : 任务失败，但是成功不通知...taskId={}, config={}", dataType.name(), taskId, JSON.toJSONString(configDTO));
                continue;
            }

            String result = "";
            Map<String, Object> paramMap = Maps.newHashMap();
            String callbackUrl = configDTO.getUrl();
            long startTime = System.currentTimeMillis();
            try {
                String params = DataUtils.encryptBeanAsBase64StringByRsa(dataMap, key);
                params = URLEncoder.encode(params, "utf-8");
                paramMap.put("params", params);
                // 超时时间（秒）
                Byte timeOut = configDTO.getTimeOut();
                // 重试次数，3次
                Byte retryTimes = configDTO.getRetryTimes();
                result = HttpClientUtils.doPostWithTimeoutAndRetryTimes(callbackUrl, timeOut, retryTimes, paramMap);
                taskLogService.insertTaskLog(taskId, dataType.getName() + "回调通知成功", new Date(), "");
                logger.info("{} callback : callback success : taskId={},callbackUrl={}, params={}", dataType.name(), taskId, callbackUrl, params);
            } catch (RequestFailedException e) {
                logger.error("{} callback : 回调通知失败 : data={}", dataType.name(), dataMap, e);
                result = e.getResult();
                taskLogService.insertTaskLog(taskId, dataType.getName() + "回调通知失败", new Date(), result);
            } catch (Exception e) {
                logger.error("{} callback : encry data failed : data={},key={}", dataType.name(), dataMap, key, e);
                result = e.getMessage();
            } finally {
                long consumeTime = System.currentTimeMillis() - startTime;
                taskCallbackLogService.insert(configDTO, taskId, (byte)1, JSON.toJSONString(dataMap), result, consumeTime, 0);
                logger.info("{} callback ：{}回调通知完成：config={},data={},paramMap={},result={}", dataType.name(), dataType.getName(), JSON.toJSONString(configDTO), dataMap, paramMap,
                    result);
            }
        }
    }

    /**
     * 获取回调配置
     *
     * @return
     */
    @Override
    public List<CallbackConfigBO> getCallbackConfigs(TaskDTO taskDTO, EDataType dataType) {
        String appId = taskDTO.getAppId();
        Byte bizType = taskDTO.getBizType();
        List<CallbackConfigBO> configList = appCallbackConfigService.queryConfigsByAppIdAndBizType(appId, bizType, dataType);
        logger.info("根据业务类型匹配回调配置结果:taskId={},configList={}", taskDTO.getId(), JSON.toJSONString(configList));

        return configList;
    }

}

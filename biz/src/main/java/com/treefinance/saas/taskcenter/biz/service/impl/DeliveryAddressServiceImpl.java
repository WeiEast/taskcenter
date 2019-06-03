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
import com.treefinance.b2b.saas.util.RemoteDataUtils;
import com.treefinance.saas.taskcenter.biz.mq.model.DeliveryAddressMessage;
import com.treefinance.saas.taskcenter.biz.service.DeliveryAddressService;
import com.treefinance.saas.taskcenter.biz.service.TaskLogService;
import com.treefinance.saas.taskcenter.biz.service.TaskService;
import com.treefinance.saas.taskcenter.biz.service.directive.process.CallbackEntity;
import com.treefinance.saas.taskcenter.context.Constants;
import com.treefinance.saas.taskcenter.context.enums.EDataType;
import com.treefinance.saas.taskcenter.exception.CallbackEncryptException;
import com.treefinance.saas.taskcenter.exception.RequestFailedException;
import com.treefinance.saas.taskcenter.interation.manager.LicenseManager;
import com.treefinance.saas.taskcenter.interation.manager.domain.AppLicense;
import com.treefinance.saas.taskcenter.interation.manager.domain.CallbackConfigBO;
import com.treefinance.saas.taskcenter.service.AppCallbackConfigService;
import com.treefinance.saas.taskcenter.service.TaskCallbackLogService;
import com.treefinance.saas.taskcenter.service.domain.TaskInfo;
import com.treefinance.saas.taskcenter.util.CallbackDataUtils;
import com.treefinance.saas.taskcenter.util.HttpClientUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Nonnull;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by yh-treefinance on 2017/9/28.
 */
@Service
public class DeliveryAddressServiceImpl implements DeliveryAddressService {
    private static final Logger logger = LoggerFactory.getLogger(DeliveryAddressServiceImpl.class);
    @Autowired
    private TaskCallbackLogService taskCallbackLogService;
    @Autowired
    private TaskService taskService;
    @Autowired
    private TaskLogService taskLogService;
    @Autowired
    private AppCallbackConfigService appCallbackConfigService;
    @Autowired
    private LicenseManager licenseManager;

    @Override
    public void callback(@Nonnull DeliveryAddressMessage message) {
        Long taskId = message.getTaskId();
        if (taskId == null) {
            return;
        }
        // 1. 记录日志
        taskLogService.insertTaskLog(taskId, "收货地址爬取完成", new Date(), "");

        // 2.获取任务
        TaskInfo task = taskService.getTaskInfoById(taskId);
        if (task == null) {
            logger.warn("delivery address callback failed : task {} not exists, message={}...", taskId, message);
            return;
        }

        String appId = task.getAppId();
        List<CallbackConfigBO> callbackConfigs = appCallbackConfigService.queryConfigsByAppIdAndBizType(task.getAppId(), task.getBizType(), EDataType.DELIVERY_ADDRESS);
        logger.info("根据业务类型匹配回调配置结果:taskId={},configList={}", task.getId(), callbackConfigs);
        if (CollectionUtils.isEmpty(callbackConfigs)) {
            logger.info("delivery address callback failed :taskId={}, callbackConfigs of {} is null, message={}...", taskId, appId, message);
            return;
        }
        CallbackEntity entity = this.buildCallbackEntity(message, task);

        Map<String, Object> originalDataMap = Maps.newHashMap(entity);

        // 3.获取商户密钥
        AppLicense appLicense = licenseManager.getAppLicenseByAppId(appId);

        // 4.爬取成功，下载数据
        boolean isSuccess = Integer.valueOf(1).equals(message.getStatus());
        if (isSuccess) {
            entity.success();

            prepareCallbackData(entity, appLicense);
        } else {
            entity.failure();
        }
        logger.debug("delivery address callback : generate callback entity >>> {} ", entity);

        // 5.回调 aes加密
        String aesKey = appLicense.getDataSecretKey();
        for (CallbackConfigBO configDTO : callbackConfigs) {
            if (isSuccess) {
                // 任务成功但是成功不通知
                if (!Constants.YES.equals(configDTO.getIsNotifySuccess())) {
                    logger.info("delivery address callback : 任务成功，但是成功不通知...taskId={}, config={}", taskId, JSON.toJSONString(configDTO));
                    continue;
                }
            } else {
                // 任务失败但是失败不通知
                if (!Constants.YES.equals(configDTO.getIsNotifyFailure())) {
                    logger.info("delivery address callback : 任务失败，但是成功不通知...taskId={}, config={}", taskId, JSON.toJSONString(configDTO));
                    continue;
                }
            }

            long startTime = System.currentTimeMillis();
            String result = "";
            Map<String, Object> paramMap = new HashMap<>(1);
            try {
                String params = CallbackDataUtils.encryptByAES(entity, aesKey);
                params = URLEncoder.encode(params, "utf-8");
                logger.info("delivery address callback : encrypt data : taskId={}, params={}", taskId, params);
                paramMap.put("params", params);
                // 超时时间（秒）
                Byte timeOut = configDTO.getTimeOut();
                // 重试次数，3次
                Byte retryTimes = configDTO.getRetryTimes();
                String callbackUrl = configDTO.getUrl();
                result = HttpClientUtils.doPostWithTimeoutAndRetryTimes(callbackUrl, timeOut, retryTimes, paramMap);
                taskLogService.insertTaskLog(taskId, "收货地址回调通知成功", new Date(), result);
                logger.info("delivery address callback : callback success : taskId={},callbackUrl={}, params={}", taskId, callbackUrl, params);
            } catch (CallbackEncryptException | UnsupportedEncodingException e) {
                logger.error("delivery address callback : encrypt data failed : data={},key={}", entity, aesKey, e);
                result = e.getMessage();
            } catch (RequestFailedException e) {
                logger.error("delivery address callback : 收货地址回调通知失败 : data={}", entity, e);
                result = e.getResult();
                taskLogService.insertTaskLog(taskId, "收货地址回调通知失败", new Date(), result);
            } finally {
                long consumeTime = System.currentTimeMillis() - startTime;
                taskCallbackLogService.insert(configDTO, taskId, (byte)1, JSON.toJSONString(originalDataMap), result, consumeTime, 0);
                logger.info("delivery address callback ：收货地址回调通知完成：config={},data={},paramMap={},result={}", JSON.toJSONString(configDTO), entity, paramMap, result);
            }
        }

    }

    private void prepareCallbackData(CallbackEntity entity, AppLicense appLicense) {
        Object dataUrlObj = entity.remove("dataUrl");
        if (dataUrlObj != null) {
            boolean success = true;
            Map<String, Object> downloadDataMap = null;
            try {
                // oss 下载数据
                byte[] result = RemoteDataUtils.download(dataUrlObj.toString(), byte[].class);
                if (ArrayUtils.isNotEmpty(result)) {
                    // 数据体默认使用商户密钥加密
                    downloadDataMap = CallbackDataUtils.decryptAsMapByAES(result, appLicense.getDataSecretKey());
                }
                logger.debug("delivery address callback : download data success : {} ", downloadDataMap);
            } catch (Exception e) {
                logger.error("delivery address callback : download data failed : dataUrl={}", dataUrlObj);
                success = false;
            }

            if (success) {
                if (MapUtils.isNotEmpty(downloadDataMap)) {
                    entity.setData(downloadDataMap);
                } else {
                    entity.emptyData();
                }
            } else {
                entity.put("dataUrl", dataUrlObj);
                entity.failure("下载数据失败");
            }
        } else {
            entity.setData(StringUtils.EMPTY);
        }
    }

    private CallbackEntity buildCallbackEntity(DeliveryAddressMessage message, TaskInfo task) {
        Map<String, Object> dataMap = JSON.parseObject(message.getData());

        CallbackEntity entity = dataMap == null ? new CallbackEntity() : new CallbackEntity(dataMap);
        // 填充uniqueId、taskId、taskStatus
        entity.setTaskIdIfAbsent(task.getId());
        entity.setUniqueIdIfAbsent(task.getUniqueId());
        return entity;
    }

}

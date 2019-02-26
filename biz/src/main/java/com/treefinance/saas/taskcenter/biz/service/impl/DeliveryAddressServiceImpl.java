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

package com.treefinance.saas.taskcenter.biz.service.impl;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.treefinance.b2b.saas.util.RemoteDataUtils;
import com.treefinance.saas.taskcenter.biz.mq.model.DeliveryAddressMessage;
import com.treefinance.saas.taskcenter.biz.service.AppCallbackConfigService;
import com.treefinance.saas.taskcenter.biz.service.AppLicenseService;
import com.treefinance.saas.taskcenter.biz.service.DeliveryAddressService;
import com.treefinance.saas.taskcenter.biz.service.TaskCallbackLogService;
import com.treefinance.saas.taskcenter.biz.service.TaskLogService;
import com.treefinance.saas.taskcenter.biz.service.TaskService;
import com.treefinance.saas.taskcenter.context.enums.EDataType;
import com.treefinance.saas.taskcenter.dto.AppCallbackConfigDTO;
import com.treefinance.saas.taskcenter.dto.AppLicenseDTO;
import com.treefinance.saas.taskcenter.dto.TaskDTO;
import com.treefinance.saas.taskcenter.exception.CallbackEncryptException;
import com.treefinance.saas.taskcenter.exception.RequestFailedException;
import com.treefinance.saas.taskcenter.util.CallbackDataUtils;
import com.treefinance.saas.taskcenter.util.HttpClientUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by yh-treefinance on 2017/9/28.
 */
@Service
public class DeliveryAddressServiceImpl implements DeliveryAddressService {
    private static final Logger logger = LoggerFactory.getLogger(DeliveryAddressServiceImpl.class);

    @Autowired
    private TaskService taskService;
    @Autowired
    private TaskLogService taskLogService;
    @Autowired
    private AppCallbackConfigService appCallbackConfigService;
    @Autowired
    private AppLicenseService appLicenseService;
    @Autowired
    protected TaskCallbackLogService taskCallbackLogService;

    @Override
    public void callback(DeliveryAddressMessage message) {
        if (message == null || message.getTaskId() == null) {
            return;
        }
        Long taskId = message.getTaskId();
        // 1. 记录日志
        taskLogService.insertTaskLog(taskId, "收货地址爬取完成", new Date(), "");
        // 2.获取任务
        TaskDTO taskDTO = taskService.getById(taskId);
        if (taskDTO == null) {
            logger.info("delivery address callback failed : task {} not exists, message={}...", taskId, JSON.toJSONString(message));
            return;
        }
        String appId = taskDTO.getAppId();
        // 3.获取商户密钥
        AppLicenseDTO appLicense = appLicenseService.getAppLicense(appId);
        if (appLicense == null) {
            logger.info("delivery address callback failed : taskId={} appLicense of {} is null, message={}...", taskId, appId, JSON.toJSONString(message));
            return;
        }

        List<AppCallbackConfigDTO> callbackConfigs = getCallbackConfigs(taskDTO);
        if (CollectionUtils.isEmpty(callbackConfigs)) {
            logger.info("delivery address callback failed :taskId={}, callbackConfigs of {} is null, message={}...", taskId, appId, JSON.toJSONString(message));
            return;
        }
        Map<String, Object> dataMap = JSON.parseObject(message.getData());
        if (MapUtils.isEmpty(dataMap)) {
            dataMap = Maps.newHashMap();
        }
        Map<String, Object> originalDataMap = Maps.newHashMap(dataMap);
        // 填充uniqueId、taskId、taskStatus
        dataMap.put("uniqueId", taskDTO.getUniqueId());
        dataMap.put("taskId", taskDTO.getId());
        // 4.爬取成功，下载数据
        boolean isSuccess = Integer.valueOf(1).equals(message.getStatus());
        if (isSuccess) {
            dataMap.put("taskStatus", "001");
            dataMap.put("taskErrorMsg", StringUtils.EMPTY);
            Object dataUrlObj = dataMap.remove("dataUrl");
            if (dataUrlObj != null) {
                try {
                    String dataUrl = dataUrlObj.toString();
                    String appDataKey = appLicense.getDataSecretKey();
                    // oss 下载数据
                    byte[] result = RemoteDataUtils.download(dataUrl, byte[].class);
                    // 数据体默认使用商户密钥加密
                    Map<String, Object> downloadDataMap = CallbackDataUtils.decryptAsMapByAES(result, appDataKey);
                    dataMap.put("data", downloadDataMap);
                    if (MapUtils.isEmpty(downloadDataMap)) {
                        dataMap.put("taskErrorMsg", "抓取结果为空");
                        dataMap.put("taskStatus", "003");
                    }

                    logger.debug("delivery address callback : download data success : {} ", downloadDataMap);
                } catch (Exception e) {
                    logger.error("delivery address callback :  download data failed : data={}", JSON.toJSONString(dataMap));
                    dataMap.put("taskErrorMsg", "下载数据失败");
                    dataMap.put("taskStatus", "004");
                    dataMap.put("dataUrl", dataUrlObj);
                }
            }
        } else {
            dataMap.put("taskStatus", "002");
            dataMap.put("taskErrorMsg", "抓取失败");
        }
        if (logger.isDebugEnabled()) {
            logger.debug("delivery address callback : generate dataMap  {} ", JSON.toJSONString(dataMap));
        }
        // 5.回调 aes加密
        String aesKey = appLicense.getDataSecretKey();
        for (AppCallbackConfigDTO configDTO : callbackConfigs) {
            // 任务成功但是成功不通知
            if (isSuccess && !Byte.valueOf("1").equals(configDTO.getIsNotifySuccess())) {
                logger.info("delivery address callback : 任务成功，但是成功不通知...taskId={}, config={}", taskId, JSON.toJSONString(configDTO));
                continue;
            }
            // 任务失败但是失败不通知
            else if (!isSuccess && !Byte.valueOf("1").equals(configDTO.getIsNotifyFailure())) {
                logger.info("delivery address callback : 任务失败，但是成功不通知...taskId={}, config={}", taskId, JSON.toJSONString(configDTO));
                continue;
            }

            String result = "";
            Map<String, Object> paramMap = Maps.newHashMap();
            String callbackUrl = configDTO.getUrl();
            Long startTime = System.currentTimeMillis();
            try {
                String params = CallbackDataUtils.encryptByAES(dataMap, aesKey);
                params = URLEncoder.encode(params, "utf-8");
                logger.info("delivery address callback : encrypt data : taskId={}, params={}", taskId, params);
                paramMap.put("params", params);
                // 超时时间（秒）
                Byte timeOut = configDTO.getTimeOut();
                // 重试次数，3次
                Byte retryTimes = configDTO.getRetryTimes();
                result = HttpClientUtils.doPostWithTimeoutAndRetryTimes(callbackUrl, timeOut, retryTimes, paramMap);
                taskLogService.insertTaskLog(taskId, "收货地址回调通知成功", new Date(), result);
                logger.info("delivery address callback : callback success : taskId={},callbackUrl={}, params={}", taskId, callbackUrl, params);
            } catch (CallbackEncryptException | UnsupportedEncodingException e) {
                logger.error("delivery address callback : encry data failed : data={},key={}", dataMap, aesKey, e);
                result = e.getMessage();
            } catch (RequestFailedException e) {
                logger.error("delivery address callback : 收货地址回调通知失败 : data={}", dataMap, e);
                result = e.getResult();
                taskLogService.insertTaskLog(taskId, "收货地址回调通知失败", new Date(), result);
            } finally {
                long consumeTime = System.currentTimeMillis() - startTime;
                taskCallbackLogService.insert(configDTO, taskId, (byte)1, JSON.toJSONString(originalDataMap), result, consumeTime, 0);
                logger.info("delivery address callback ：收货地址回调通知完成：config={},data={},paramMap={},result={}", JSON.toJSONString(configDTO), dataMap, paramMap, result);
            }
        }

    }

    /**
     * 获取回调配置
     *
     * @return
     */
    private List<AppCallbackConfigDTO> getCallbackConfigs(TaskDTO taskDTO) {
        String appId = taskDTO.getAppId();
        Byte bizType = taskDTO.getBizType();
        List<AppCallbackConfigDTO> configList = appCallbackConfigService.queryConfigsByAppIdAndBizType(appId, bizType, EDataType.DELIVERY_ADDRESS);
        logger.info("根据业务类型匹配回调配置结果:configList={}", JSON.toJSONString(configList));
        if (CollectionUtils.isEmpty(configList)) {
            return Lists.newArrayList();
        }
        // 剔除非主流程数据
        return configList.stream().filter(config -> config != null && EDataType.DELIVERY_ADDRESS.getType().equals(config.getDataType())).collect(Collectors.toList());
    }
}

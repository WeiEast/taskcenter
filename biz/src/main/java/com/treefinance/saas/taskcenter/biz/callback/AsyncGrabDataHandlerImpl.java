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

package com.treefinance.saas.taskcenter.biz.callback;

import com.alibaba.fastjson.JSON;
import com.treefinance.b2b.saas.util.DataUtils;
import com.treefinance.saas.taskcenter.biz.service.TaskLogService;
import com.treefinance.saas.taskcenter.biz.service.TaskService;
import com.treefinance.saas.taskcenter.biz.service.directive.process.CallbackEntity;
import com.treefinance.saas.taskcenter.context.enums.EDataType;
import com.treefinance.saas.taskcenter.context.enums.EGrabStatus;
import com.treefinance.saas.taskcenter.exception.RequestFailedException;
import com.treefinance.saas.taskcenter.interation.manager.LicenseManager;
import com.treefinance.saas.taskcenter.interation.manager.domain.AppLicense;
import com.treefinance.saas.taskcenter.interation.manager.domain.CallbackConfigBO;
import com.treefinance.saas.taskcenter.service.AppCallbackConfigService;
import com.treefinance.saas.taskcenter.service.TaskCallbackLogService;
import com.treefinance.saas.taskcenter.service.domain.TaskInfo;
import com.treefinance.saas.taskcenter.util.HttpClientUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Nonnull;

import java.net.URLEncoder;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 爬取数据回调Service
 * 
 * @author yh-treefinance
 * @date 2017/12/25.
 */
@Service
public class AsyncGrabDataHandlerImpl implements AsyncGrabDataHandler {
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

    @Override
    public void handle(@Nonnull AsyncGrabMessage message) {
        Assert.notNull(message.getTaskId(), "taskId can't be null : json=" + JSON.toJSONString(message));
        Assert.notNull(message.getDataType(), "dataType can't be null : json=" + JSON.toJSONString(message));

        EDataType dataType = EDataType.typeOf(message.getDataType().byteValue());
        Assert.notNull(dataType, "dataType is illegal : json=" + JSON.toJSONString(message));

        Long taskId = message.getTaskId();
        // 获取任务
        TaskInfo task = taskService.getTaskInfoById(taskId);
        if (task == null) {
            logger.info("{} callback failed : task {} not exists, message={}...", dataType.name(), taskId, JSON.toJSONString(message));
            return;
        }

        String appId = task.getAppId();
        List<CallbackConfigBO> callbackConfigs = appCallbackConfigService.queryConfigsByAppIdAndBizType(appId, task.getBizType(), dataType);
        logger.info("根据业务类型匹配回调配置结果:taskId={},configList={}", task.getId(), JSON.toJSONString(callbackConfigs));
        if (CollectionUtils.isEmpty(callbackConfigs)) {
            logger.info("{} callback failed :taskId={}, callbackConfigs of {} is null, message={}...", dataType.name(), taskId, appId, JSON.toJSONString(message));
            return;
        }

        CallbackEntity callbackEntity = new CallbackEntity();
        callbackEntity.setTaskIdIfAbsent(task.getId());
        callbackEntity.setUniqueIdIfAbsent(task.getUniqueId());
        callbackEntity.put("dataUrl", message.getDataUrl());
        callbackEntity.put("dataType", message.getDataType());
        callbackEntity.put("dataSize", message.getDataSize());
        callbackEntity.put("expiredTime", message.getExpiredTime());
        callbackEntity.put("timestamp", message.getTimestamp());

        boolean isSuccess = Integer.valueOf(1).equals(message.getStatus());
        if (isSuccess) {
            if (StringUtils.isEmpty(message.getDataUrl())) {
                callbackEntity.setStatus(EGrabStatus.RESULT_EMPTY);
            } else {
                callbackEntity.success();
            }
        } else {
            callbackEntity.failure();
        }
        logger.debug("{} callback : generate dataMap  {} ", dataType.name(), callbackEntity);

        // 回调 aes加密
        AppLicense appLicense = licenseManager.getAppLicenseByAppId(appId);
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

            long startTime = System.currentTimeMillis();
            String result = "";
            Map<String, Object> paramMap = new HashMap<>(1);
            try {
                String params = DataUtils.encryptBeanAsBase64StringByRsa(callbackEntity, key);
                params = URLEncoder.encode(params, "utf-8");
                paramMap.put("params", params);

                // 超时时间（秒）
                Byte timeOut = configDTO.getTimeOut();
                // 重试次数，3次
                Byte retryTimes = configDTO.getRetryTimes();
                String callbackUrl = configDTO.getUrl();
                result = HttpClientUtils.doPostWithTimeoutAndRetryTimes(callbackUrl, timeOut, retryTimes, paramMap);
                taskLogService.insertTaskLog(taskId, dataType.getName() + "回调通知成功", new Date(), "");
                logger.info("{} callback : callback success : taskId={},callbackUrl={}, params={}", dataType.name(), taskId, callbackUrl, params);
            } catch (RequestFailedException e) {
                logger.error("{} callback : 回调通知失败 : data={}", dataType.name(), callbackEntity, e);
                result = e.getResult();
                taskLogService.insertTaskLog(taskId, dataType.getName() + "回调通知失败", new Date(), result);
            } catch (Exception e) {
                logger.error("{} callback : encry data failed : data={},key={}", dataType.name(), callbackEntity, key, e);
                result = e.getMessage();
            } finally {
                long consumeTime = System.currentTimeMillis() - startTime;
                taskCallbackLogService.insert(configDTO, taskId, (byte)1, JSON.toJSONString(callbackEntity), result, consumeTime, 0);
                logger.info("{} callback ：{}回调通知完成：config={},data={},paramMap={},result={}", dataType.name(), dataType.getName(), configDTO, callbackEntity, paramMap,
                    result);
            }
        }
    }

}

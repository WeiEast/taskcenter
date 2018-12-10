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
import com.google.common.collect.Maps;
import com.treefinance.saas.knife.result.SimpleResult;
import com.treefinance.saas.taskcenter.share.mq.MessageProducer;
import com.treefinance.saas.taskcenter.biz.service.CallbackResultService;
import com.treefinance.saas.taskcenter.context.Constants;
import com.treefinance.saas.taskcenter.dto.AppCallbackConfigDTO;
import com.treefinance.saas.taskcenter.dto.TaskDTO;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * 回调结果处理
 * Created by yh-treefinance on 2017/11/7.
 */
@Service
public class CallbackResultServiceImpl implements CallbackResultService {
    /**
     * logger
     */
    private final Logger logger = LoggerFactory.getLogger(getClass());
    /**
     * topic
     */
    private static final String TOPIC = "crawler_monitor";
    /**
     * tag
     */
    private static final String TAG = "callback_info";

    @Autowired
    private MessageProducer messageProducer;

    @Override
    public void handleResult(TaskDTO task, String result, AppCallbackConfigDTO config, int httpCode) {
        Map<String, Object> messageMap = Maps.newHashMap();
        try {
            messageMap.put("taskId", task.getId());
            messageMap.put("taskid", task.getId());
            messageMap.put("callbackConfigId", config.getId());
            messageMap.put("appId", task.getAppId());
            messageMap.put("uniqueId", task.getUniqueId());
            messageMap.put("httpCode", httpCode);
            messageMap.put("result", result);

            addErrorMsgIfPresent(messageMap, StringUtils.trim(result));

            String jsonBody = JSON.toJSONString(messageMap);
            messageProducer.send(jsonBody, TOPIC, TAG, "");
            logger.info("send callback result message: message={}, result={}", JSON.toJSONString(messageMap), result);
        } catch (Exception e) {
            logger.error("send callback result message: httpCode={}, message={}，task={}, result={}",
                    httpCode, JSON.toJSONString(messageMap), JSON.toJSONString(task), result, e);
        }
    }

    private void addErrorMsgIfPresent(Map<String, Object> messageMap, String result) {
        if (StringUtils.isNotEmpty(result) && result.startsWith("{") && result.endsWith("}")) {
            SimpleResult simpleResult = JSON.parseObject(result, SimpleResult.class);
            if (simpleResult != null && StringUtils.isNotEmpty(simpleResult.getErrorMsg())) {
                messageMap.put(Constants.ERROR_MSG_NAME, simpleResult.getErrorMsg());
            }
        }
    }
}

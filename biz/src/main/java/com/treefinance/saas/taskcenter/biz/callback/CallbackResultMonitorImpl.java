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
import com.treefinance.saas.taskcenter.context.Constants;
import com.treefinance.saas.taskcenter.interation.manager.domain.CallbackConfigBO;
import com.treefinance.saas.taskcenter.service.domain.TaskInfo;
import com.treefinance.saas.taskcenter.service.param.CallbackRecordObject;
import com.treefinance.saas.taskcenter.service.param.CallbackRecordObject.CallbackResult;
import com.treefinance.saas.taskcenter.share.mq.MessageProducer;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * 回调结果处理 Created by yh-treefinance on 2017/11/7.
 */
@Service
public class CallbackResultMonitorImpl implements CallbackResultMonitor {
    /**
     * topic
     */
    private static final String TOPIC = "crawler_monitor";
    /**
     * tag
     */
    private static final String TAG = "callback_info";
    /**
     * logger
     */
    private final Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    private MessageProducer messageProducer;

    @Override
    public void sendMessage(TaskInfo task, CallbackRecordObject callbackRecord) {
        Map<String, Object> messageMap = new HashMap<>(8);
        try {
            messageMap.put("taskId", task.getId());
            messageMap.put("taskid", task.getId());
            CallbackConfigBO config = callbackRecord.getConfig();
            messageMap.put("callbackConfigId", config.getId());
            messageMap.put("appId", task.getAppId());
            messageMap.put("uniqueId", task.getUniqueId());
            messageMap.put("httpCode", callbackRecord.getResponseStatusCode());
            messageMap.put("result", callbackRecord.getResponseData());

            final CallbackResult callbackResult = callbackRecord.getCallbackResult();
            if (callbackResult != null) {
                final String errorMsg = callbackResult.getErrorMsg();
                if (StringUtils.isNotEmpty(errorMsg)) {
                    messageMap.put(Constants.ERROR_MSG_NAME, errorMsg);
                }
            }

            String jsonBody = JSON.toJSONString(messageMap);
            messageProducer.send(jsonBody, TOPIC, TAG, "");
            logger.info("send callback result message: message={}, result={}", JSON.toJSONString(messageMap), callbackRecord.getResponseData());
        } catch (Exception e) {
            logger.error("send callback result message: httpCode={}, message={}，task={}, result={}", callbackRecord.getResponseStatusCode(), JSON.toJSONString(messageMap),
                JSON.toJSONString(task), callbackRecord.getResponseData(), e);
        }
    }

}

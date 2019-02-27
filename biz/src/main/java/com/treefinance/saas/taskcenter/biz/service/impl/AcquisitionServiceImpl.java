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

import com.google.gson.reflect.TypeToken;
import com.treefinance.saas.taskcenter.biz.mq.model.LoginMessage;
import com.treefinance.saas.taskcenter.biz.service.AcquisitionService;
import com.treefinance.saas.taskcenter.biz.service.TaskService;
import com.treefinance.saas.taskcenter.biz.service.TaskTimeService;
import com.treefinance.saas.taskcenter.share.mq.MessageProducer;
import com.treefinance.toolkit.util.json.GsonUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;

/**
 * Created by luoyihua on 2017/5/10.
 */
@Service
public class AcquisitionServiceImpl implements AcquisitionService {
    private static final Logger logger = LoggerFactory.getLogger(AcquisitionServiceImpl.class);

    @Autowired
    private MessageProducer messageProducer;
    @Autowired
    private TaskService taskService;
    @Autowired
    private TaskTimeService taskTimeService;

    @Override
    public void acquisition(Long taskid, String header, String cookie, String url, String website, String accountNo, String topic, String extra) {
        logger.info("acquisition : taskid={},header={},cookie={},url={},website={},accountNo={}", taskid, header, cookie, url, website, accountNo);
        LoginMessage loginMessage = new LoginMessage();
        loginMessage.setCookie(cookie);
        loginMessage.setEndUrl(url);
        loginMessage.setTaskId(taskid);
        loginMessage.setWebsiteName(website);
        loginMessage.setAccountNo(accountNo);
        if (StringUtils.isNotEmpty(header)) {
            Map<String, Object> map = GsonUtils.fromJson(header, new TypeToken<Map<String, Object>>() {}.getType());
            Object setCookieObj = map.get("Set-Cookie");
            if (setCookieObj != null) {
                loginMessage.setSetCookie((String)setCookieObj);
            }
        }
        if (StringUtils.isNotBlank(extra)) {
            Map<String, Object> map = GsonUtils.fromJson(extra, new TypeToken<Map<String, Object>>() {}.getType());
            loginMessage.setExtra(map);
        }
        try {
            messageProducer.send(GsonUtils.toJson(loginMessage), topic, "login_info", taskid.toString());
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

        taskService.updateAccountNoAndWebsiteIfNeedWhenProcessing(taskid, accountNo, website);

        taskTimeService.updateLoginTime(taskid, new Date());
    }

}

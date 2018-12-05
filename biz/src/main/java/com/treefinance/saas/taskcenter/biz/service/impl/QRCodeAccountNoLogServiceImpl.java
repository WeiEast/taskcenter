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

import com.datatrees.spider.share.api.SpiderTaskApi;
import com.treefinance.saas.taskcenter.biz.service.QRCodeAccountNoLogService;
import com.treefinance.saas.taskcenter.biz.service.TaskService;
import com.treefinance.saas.taskcenter.common.model.dto.TaskDTO;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 记录二维码登录爬数回传的账号信息
 * Created by haojiahong on 2018/1/18.
 */
@Service
public class QRCodeAccountNoLogServiceImpl implements QRCodeAccountNoLogService {

    private final static Logger logger = LoggerFactory.getLogger(QRCodeAccountNoLogServiceImpl.class);

    @Autowired
    private TaskService taskService;
    @Autowired
    private SpiderTaskApi spiderTaskApi;

    @Override
    public void logQRCodeAccountNo(Long taskId) {
        TaskDTO taskDTO = taskService.getById(taskId);
        if (taskDTO != null && StringUtils.isNotBlank(taskDTO.getAccountNo())) {
            return;
        }

        try {
            String accountNo = spiderTaskApi.getTaskAccountNo(taskId);
            logger.info("记录任务accountNo:调用爬数查询任务账号信息,taskId={},accountNo={}", taskId, accountNo);
            if (StringUtils.isNotBlank(accountNo)) {
                logger.info("记录任务accountNo:taskId={},accountNo={}", taskId, accountNo);
                taskService.updateAccountNoById(taskId, accountNo);
            }
        } catch (Exception e) {
            logger.error("记录任务accountNo:调用爬数查询任务账号信息异常,taskId={}", taskId, e);
        }
    }
}

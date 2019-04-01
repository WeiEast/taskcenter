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
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.treefinance.saas.taskcenter.biz.service.TaskBuryPointLogService;
import com.treefinance.saas.taskcenter.dao.entity.TaskBuryPointLog;
import com.treefinance.saas.taskcenter.dao.repository.TaskBuryPointRepository;
import com.treefinance.saas.taskcenter.dao.repository.TaskOperatorMaintainUserLogRepository;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.Nonnull;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by haojiahong on 2017/8/17.
 */
@Service
public class TaskBuryPointLogServiceImpl implements TaskBuryPointLogService {

    private static final Logger logger = LoggerFactory.getLogger(TaskBuryPointLogServiceImpl.class);

    private final ConcurrentLinkedQueue<TaskBuryPointLog> logQueue = new ConcurrentLinkedQueue<>();

    @Autowired
    private TaskBuryPointRepository taskBuryPointRepository;
    @Autowired
    private TaskOperatorMaintainUserLogRepository taskOperatorMaintainUserLogRepository;

    @Scheduled(fixedRate = 1000)
    public void insert() {
        if (logQueue.isEmpty()) {
            return;
        }

        List<TaskBuryPointLog> list = Lists.newArrayList();
        TaskBuryPointLog log;
        while ((log = logQueue.poll()) != null) {
            list.add(log);
        }

        try {
            for (List<TaskBuryPointLog> logList : Lists.partition(list, 100)) {
                taskBuryPointRepository.insert(logList);
            }
            logger.debug("batchInsert taskBuryPointLog: list={}", JSON.toJSONString(list));
        } catch (Exception e) {
            logger.error("batchInsert taskBuryPointLog error: list={}", JSON.toJSONString(list), e);
            // 失败重新放入队列,埋点信息并不是很重要,去掉重试.
            // logQueue.addAll(list);
        }
    }

    @Override
    public void pushTaskBuryPointLog(Long taskId, String appId, String code) {
        TaskBuryPointLog log = new TaskBuryPointLog();
        log.setTaskId(taskId);
        log.setAppId(appId);
        log.setCode(code);
        logQueue.offer(log);
    }

    @Override
    public List<TaskBuryPointLog> queryTaskBuryPointLogByCode(Long taskId, String... codes) {
        return taskBuryPointRepository.queryTaskBuryPointLogsByTaskIdAndInCodes(taskId, codes == null ? null : Arrays.asList(codes));
    }

    @Override
    public List<TaskBuryPointLog> queryTaskBuryPointLogs(Long id, String appId, Long taskId, String code, String order) {
        return taskBuryPointRepository.queryTaskBuryPointLogs(id, appId, taskId, code, order);
    }

    @Override
    public List<TaskBuryPointLog> listTaskBuryPointLogsDescWithCreateTimeByTaskId(@Nonnull Long taskId) {
        return taskBuryPointRepository.listTaskBuryPointLogsDescWithCreateTimeByTaskId(taskId);
    }

    @Override
    public void logTaskOperatorMaintainUser(Long taskId, String appId, String extra) {
        if (StringUtils.isBlank(extra)) {
            logger.error("运营商正在维护,记录用户信息,传入信息为空extra={}", extra);
            return;
        }
        ObjectMapper objectMapper = new ObjectMapper();
        Map map = null;
        try {
            map = objectMapper.readValue(extra, Map.class);
        } catch (IOException e) {
            logger.error("运营商正在维护,记录用户信息,extra={}解析出错", extra, e);
        }
        if (MapUtils.isEmpty(map)) {
            return;
        }
        Object mobileObj = map.get("mobile");
        String mobile = mobileObj == null ? StringUtils.EMPTY : String.valueOf(mobileObj);
        Object operatorNameObj = map.get("operatorName");
        String operatorName = operatorNameObj == null ? StringUtils.EMPTY : String.valueOf(operatorNameObj);
        if (StringUtils.isBlank(mobile)) {
            logger.error("运营商正在维护,记录用户信息,extra={}中未传入mobile信息", extra);
            return;
        }

        taskOperatorMaintainUserLogRepository.insertLog(taskId, appId, mobile, operatorName);
    }
}

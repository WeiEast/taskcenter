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

package com.treefinance.saas.taskcenter.biz.service;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.ImmutableMap;
import com.treefinance.saas.taskcenter.biz.service.directive.DirectiveService;
import com.treefinance.saas.taskcenter.biz.service.directive.MoxieDirectivePacket;
import com.treefinance.saas.taskcenter.biz.service.impl.TaskServiceImpl;
import com.treefinance.saas.taskcenter.common.enums.EDirective;
import com.treefinance.saas.taskcenter.common.enums.ETaskStep;
import com.treefinance.saas.taskcenter.dto.moxie.MoxieTaskEventNoticeDTO;
import com.treefinance.saas.taskcenter.interation.manager.FundManager;
import com.treefinance.saas.taskcenter.interation.manager.FundMoxieManager;
import com.treefinance.saas.taskcenter.service.TaskAttributeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * Created by haojiahong on 2017/9/15.
 */
@Service
public class MoxieBusinessService {
    protected final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private TaskLogService taskLogService;
    @Autowired
    private DirectiveService directiveService;
    @Autowired
    private TaskAttributeService taskAttributeService;
    @Autowired
    private FundManager fundManager;
    @Autowired
    private TaskServiceImpl taskService;
    @Autowired
    private FundMoxieManager fundMoxieManager;

    /**
     * 魔蝎任务采集失败业务处理
     *
     * @param eventNoticeDTO
     */
    public void grabFail(MoxieTaskEventNoticeDTO eventNoticeDTO) {
        String moxieTaskId = eventNoticeDTO.getMoxieTaskId();
        String message = eventNoticeDTO.getMessage();

        Long taskId = taskAttributeService.findTaskIdByMoxieTid(moxieTaskId);
        if (taskId == null) {
            logger.error("handle moxie business error: moxieTaskId={} doesn't have taskId matched in task_attribute", moxieTaskId);
            return;
        }

        // 任务已经完成,不再继续后续处理.(当任务超时时,会发生魔蝎回调接口重试)
        boolean flag = taskService.isTaskCompleted(taskId);
        if (flag) {
            return;
        }
        // 1.记录采集失败日志
        taskLogService.insertTaskLog(taskId, ETaskStep.CRAWL_FAIL.getText(), new Date(), message);
        // 2.发送任务失败指令
        MoxieDirectivePacket directivePacket = new MoxieDirectivePacket(EDirective.TASK_FAIL);
        directivePacket.setTaskId(taskId);
        directivePacket.setMoxieTaskId(moxieTaskId);
        directivePacket.setRemark(JSON.toJSONString(ImmutableMap.of("taskErrorMsg", "爬数失败")));
        directiveService.process(directivePacket);

    }

    public void loginSuccess(MoxieTaskEventNoticeDTO eventNoticeDTO) {
        MoxieDirectivePacket directivePacket = new MoxieDirectivePacket(EDirective.LOGIN_SUCCESS);
        directivePacket.setMoxieTaskId(eventNoticeDTO.getMoxieTaskId());
        directiveService.process(directivePacket);
    }

    public void loginFail(MoxieTaskEventNoticeDTO eventNoticeDTO) {
        MoxieDirectivePacket directivePacket = new MoxieDirectivePacket(EDirective.LOGIN_FAIL);
        directivePacket.setMoxieTaskId(eventNoticeDTO.getMoxieTaskId());
        directivePacket.setRemark(eventNoticeDTO.getMessage());
        directiveService.process(directivePacket);
    }

    /**
     * 魔蝎账单通知业务处理
     *
     * @param eventNoticeDTO
     */
    @Transactional(rollbackFor = Exception.class)
    public void bill(MoxieTaskEventNoticeDTO eventNoticeDTO) {
        String moxieTaskId = eventNoticeDTO.getMoxieTaskId();

        Long taskId = taskAttributeService.findTaskIdByMoxieTid(moxieTaskId);
        if (taskId == null) {
            logger.error("handle moxie business error: moxieTaskId={} doesn't have taskId matched in task_attribute", moxieTaskId);
            return;
        }

        // 任务已经完成,不再继续后续处理.(当任务超时时,会发生魔蝎回调接口重试)
        boolean flag = taskService.isTaskCompleted(taskId);
        if (flag) {
            return;
        }

        // 获取魔蝎数据,调用洗数,传递账单数据
        boolean result = true;
        String message = null;
        String processResult = null;
        try {
            processResult = this.billAndProcess(taskId, moxieTaskId);
        } catch (Exception e) {
            logger.error("handle moxie business error:bill and process fail.taskId={},moxieTaskId={}", taskId, moxieTaskId, e);
            result = false;
            message = e.getMessage();
        }
        // 3.根据洗数返回结果,发送任务成功或失败指令

        MoxieDirectivePacket directivePacket = new MoxieDirectivePacket();
        directivePacket.setMoxieTaskId(moxieTaskId);
        directivePacket.setTaskId(taskId);
        if (result) {
            directivePacket.setDirective(EDirective.TASK_SUCCESS);
            directivePacket.setRemark(processResult);
        } else {
            directivePacket.setDirective(EDirective.TASK_FAIL);
            directivePacket.setRemark(JSON.toJSONString(ImmutableMap.of("taskErrorMsg", message)));
        }

        directiveService.process(directivePacket);
    }

    private String billAndProcess(Long taskId, String moxieTaskId) throws Exception {
        String moxieResult;
        try {
            moxieResult = fundMoxieManager.queryFundsEx(moxieTaskId);
            // 记录抓取日志
            taskLogService.insertTaskLog(taskId, ETaskStep.CRAWL_SUCCESS.getText(), new Date(), null);
            taskLogService.insertTaskLog(taskId, ETaskStep.CRAWL_COMPLETE.getText(), new Date(), null);
            logger.info("handle moxie business moxieResult,taskId={},moxieTaskId={},result={}", taskId, moxieTaskId, moxieResult);
        } catch (Exception e) {
            logger.error("handle moxie business error:bill fail", e);
            taskLogService.insertTaskLog(taskId, ETaskStep.CRAWL_FAIL.getText(), new Date(), e.getMessage());
            throw new Exception("获取公积金信息失败");
        }
        try {
            String processResult = fundManager.fund(taskId, moxieResult);
            // 记录数据保存日志
            taskLogService.insertTaskLog(taskId, ETaskStep.DATA_SAVE_SUCCESS.getText(), new Date(), null);
            logger.info("handle moxie business processResult,taskId={},moxieTaskId={},result={}", taskId, moxieTaskId, processResult);
            return processResult;
        } catch (Exception e) {
            logger.error("handle moxie business error:process fail", e);
            taskLogService.insertTaskLog(taskId, ETaskStep.DATA_SAVE_FAIL.getText(), new Date(), e.getMessage());
            throw new Exception("洗数失败");
        }
    }

}

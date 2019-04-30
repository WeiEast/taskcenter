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

package com.treefinance.saas.taskcenter.biz.service;

import com.alibaba.fastjson.JSON;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.treefinance.saas.taskcenter.biz.service.directive.DirectiveService;
import com.treefinance.saas.taskcenter.common.enums.EDirective;
import com.treefinance.saas.taskcenter.common.enums.ETaskAttribute;
import com.treefinance.saas.taskcenter.common.enums.ETaskStatus;
import com.treefinance.saas.taskcenter.common.enums.ETaskStep;
import com.treefinance.saas.taskcenter.context.enums.TaskStatusMsgEnum;
import com.treefinance.saas.taskcenter.dao.entity.Task;
import com.treefinance.saas.taskcenter.dao.entity.TaskAttribute;
import com.treefinance.saas.taskcenter.dao.repository.TaskRepository;
import com.treefinance.saas.taskcenter.biz.service.directive.MoxieDirectivePacket;
import com.treefinance.saas.taskcenter.interation.manager.BizTypeManager;
import com.treefinance.saas.taskcenter.service.TaskAttributeService;
import com.treefinance.saas.taskcenter.share.cache.redis.RedisDao;
import com.treefinance.saas.taskcenter.util.SystemUtils;
import com.treefinance.toolkit.util.DateUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * Created by haojiahong on 2017/9/22.
 */
@Service
public class MoxieTimeoutService {

    private static final Logger logger = LoggerFactory.getLogger(MoxieTimeoutService.class);

    private static String LOGIN_TIME_PREFIX = "saas-grap-server-moxie-login-time:";

    @Autowired
    private TaskRepository taskRepository;
    /**
     * 本地任务缓存
     */
    private final LoadingCache<Long, Task> cache = CacheBuilder.newBuilder().expireAfterWrite(5, TimeUnit.MINUTES).maximumSize(20000).build(new CacheLoader<Long, Task>() {
        @Override
        public Task load(Long taskId) throws Exception {
            return taskRepository.getTaskById(taskId);
        }
    });
    @Autowired
    private BizTypeManager bizTypeManager;
    @Autowired
    private TaskLogService taskLogService;
    @Autowired
    private DirectiveService directiveService;
    @Autowired
    private TaskAttributeService taskAttributeService;
    @Autowired
    private RedisDao redisDao;

    /**
     * 记录魔蝎任务创建时间,即开始登录时间.
     *
     * @param taskId
     */
    public void logLoginTime(Long taskId) {
        String now = SystemUtils.nowDateTimeStr();
        taskAttributeService.insertOrUpdate(taskId, ETaskAttribute.LOGIN_TIME.getAttribute(), now);

        String key = LOGIN_TIME_PREFIX + taskId;
        redisDao.setEx(key, now, 10, TimeUnit.MINUTES);
    }

    public void logLoginTime(Long taskId, Date date) {
        taskAttributeService.insertOrUpdate(taskId, ETaskAttribute.LOGIN_TIME.getAttribute(), date);

        String key = LOGIN_TIME_PREFIX + taskId;
        redisDao.setEx(key, DateUtils.format(date), 10, TimeUnit.MINUTES);
    }

    /**
     * 获取登录时间
     *
     * @param taskId
     * @return
     */
    public Date getLoginTime(Long taskId) {
        String key = LOGIN_TIME_PREFIX + taskId;
        String value = redisDao.get(key);
        if (StringUtils.isNotBlank(value)) {
            return DateUtils.parse(value);
        } else {
            TaskAttribute taskAttribute = taskAttributeService.queryAttributeByTaskIdAndName(taskId, ETaskAttribute.LOGIN_TIME.getAttribute(), false);
            if (taskAttribute == null) {
                logger.info("公积金登录超时,taskId={}登录时间记录key={}已失效,需检查魔蝎登录状态回调接口是否异常", taskId, key);
                return null;
            }
            value = taskAttribute.getValue();
            Date date = DateUtils.parse(value);
            // 重新set redis
            this.logLoginTime(taskId, date);
            return date;
        }
    }

    /**
     * 前端收到登录超时后,重置登录时间
     *
     * @param taskId
     */
    public void resetLoginTaskTimeOut(Long taskId) {
        this.logLoginTime(taskId);
    }

    public void handleTaskTimeout(Long taskId) {
        Task task;
        try {
            task = cache.get(taskId);
        } catch (ExecutionException e) {
            logger.error("taskId={} is not exists...", taskId, e);
            return;
        }
        logger.info("handleTaskTimeout async : taskId={}, task={}", taskId, JSON.toJSONString(task));

        Byte taskStatus = task.getStatus();
        if (ETaskStatus.CANCEL.getStatus().equals(taskStatus) || ETaskStatus.SUCCESS.getStatus().equals(taskStatus) || ETaskStatus.FAIL.getStatus().equals(taskStatus)) {
            logger.info("handleTaskTimeout error : the task is completed: {}", JSON.toJSONString(task));
            return;
        }
        Integer timeout = bizTypeManager.getBizTimeout(task.getBizType());
        if(timeout == null){
            return;
        }
        // 任务超时: 当前时间-登录时间>超时时间
        Date currentTime = new Date();
        Date loginTime = getLoginTime(taskId);
        Date timeoutDate = DateUtils.plusSeconds(loginTime, timeout);
        logger.info("moxie isTaskTimeout: taskid={}，loginTime={},current={},timeout={}", taskId, DateUtils.format(loginTime), DateUtils.format(currentTime), timeout);
        if (timeoutDate.before(currentTime)) {
            // 增加日志：任务超时
            String errorMessage = "任务超时：当前时间(" + DateFormatUtils.format(currentTime, "yyyy-MM-dd HH:mm:ss") + ") - 登录时间(" + DateFormatUtils.format(loginTime, "yyyy-MM-dd HH:mm:ss")
                + ")> 超时时间(" + timeout + "秒)";

            taskLogService.log(task.getId(), TaskStatusMsgEnum.TIMEOUT_MSG, errorMessage);

            // 超时处理：任务更新为失败
            MoxieDirectivePacket directivePacket = new MoxieDirectivePacket(EDirective.TASK_FAIL);
            directivePacket.setTaskId(task.getId());
            directivePacket.setRemark(JSON.toJSONString(ImmutableMap.of("taskErrorMsg", errorMessage)));
            directiveService.process(directivePacket);
        }
    }

    @Transactional
    public void handleLoginTimeout(Long taskId, String moxieTaskId) {
        // 重置登录时间
        this.resetLoginTaskTimeOut(taskId);

        // 登录失败(如用户名密码错误),需删除task_attribute中此taskId对应的moxieTaskId,重新登录时,可正常轮询/login/submit接口
        taskAttributeService.insertOrUpdate(taskId, ETaskAttribute.FUND_MOXIE_TASKID.getAttribute(), "");

        // 记录登录超时日志
        Map<String, Object> map = Maps.newHashMap();
        map.put("error", "登录超时");
        map.put("moxieTaskId", moxieTaskId);
        taskLogService.insertTaskLog(taskId, ETaskStep.LOGIN_FAIL.getText(), new Date(), JSON.toJSONString(map));

    }
}
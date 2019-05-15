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
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.treefinance.saas.assistant.model.Constants;
import com.treefinance.saas.taskcenter.biz.service.MoxieTaskService;
import com.treefinance.saas.taskcenter.biz.service.TaskLogService;
import com.treefinance.saas.taskcenter.biz.service.directive.DirectiveService;
import com.treefinance.saas.taskcenter.biz.service.directive.MoxieDirectivePacket;
import com.treefinance.saas.taskcenter.common.enums.EDirective;
import com.treefinance.saas.taskcenter.common.enums.ETaskAttribute;
import com.treefinance.saas.taskcenter.common.enums.ETaskStatus;
import com.treefinance.saas.taskcenter.common.enums.ETaskStep;
import com.treefinance.saas.taskcenter.context.enums.TaskStatusMsgEnum;
import com.treefinance.saas.taskcenter.dao.entity.Task;
import com.treefinance.saas.taskcenter.dao.repository.TaskAttributeRepository;
import com.treefinance.saas.taskcenter.dao.repository.TaskRepository;
import com.treefinance.saas.taskcenter.exception.UnexpectedException;
import com.treefinance.saas.taskcenter.interation.manager.BizTypeManager;
import com.treefinance.saas.taskcenter.share.cache.redis.RedisDao;
import com.treefinance.saas.taskcenter.share.cache.redis.RedisKeyUtils;
import com.treefinance.saas.taskcenter.share.cache.redis.RedissonLocks;
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
import java.util.concurrent.TimeUnit;

/**
 * @author haojiahong
 * @date 2017/9/22.
 */
@Service
public class MoxieTaskServiceImpl implements MoxieTaskService {

    private static final Logger logger = LoggerFactory.getLogger(MoxieTaskServiceImpl.class);

    private static final String LOGIN_TIME_PREFIX = "saas_moxie_task_login_time:";
    private static final String LOCK_KEY_PREFIX = "saas_moxie_task_login_time_update_lock:";

    @Autowired
    private TaskRepository taskRepository;
    @Autowired
    private TaskAttributeRepository taskAttributeRepository;
    @Autowired
    private BizTypeManager bizTypeManager;
    @Autowired
    private TaskLogService taskLogService;
    @Autowired
    private DirectiveService directiveService;
    @Autowired
    private RedisDao redisDao;
    @Autowired
    private RedissonLocks redissonLocks;

    @Override
    public void logLoginTime(Long taskId) {
        this.logLoginTime(taskId, SystemUtils.now());
    }

    @Override
    public void logLoginTime(Long taskId, Date date) {
        try {
            String lockKey = getLoginTimeUpdateLockKey(taskId);
            redissonLocks.tryLock(lockKey, 5, 60, TimeUnit.SECONDS, () -> {
                taskAttributeRepository.insertOrUpdateAttribute(taskId, ETaskAttribute.LOGIN_TIME.getAttribute(), date);

                cacheLoginTime(taskId, date);
            });
        } catch (InterruptedException e) {
            throw new UnexpectedException("Thread interrupted when saving moxie-task's login-time! - taskId: " + taskId, e);
        }
    }

    private void cacheLoginTime(Long taskId, Date date) {
        String key = LOGIN_TIME_PREFIX + taskId;
        redisDao.setEx(key, DateUtils.format(date), 10, TimeUnit.MINUTES);
    }

    @Override
    public Date queryLoginTime(Long taskId) {
        String key = LOGIN_TIME_PREFIX + taskId;
        String value = StringUtils.trim(redisDao.get(key));
        if (StringUtils.isNotEmpty(value)) {
            return DateUtils.parse(value);
        }

        final String lockKey = getLoginTimeUpdateLockKey(taskId);
        try {
            return redissonLocks.tryLock(lockKey, 5, 60, TimeUnit.SECONDS, isLock -> {
                Date date = taskAttributeRepository.queryAttributeValueAsDate(taskId, ETaskAttribute.LOGIN_TIME.getAttribute());
                if (date != null && isLock) {
                    this.cacheLoginTime(taskId, date);
                } else {
                    logger.info("获取魔蝎任务登录时间时,未查询到任务登录时间,任务未登录.taskId={}", taskId);
                }
                return date;
            });
        } catch (InterruptedException e) {
            throw new UnexpectedException("Thread interrupted when querying moxie-task's login-time! - taskId: " + taskId, e);
        }
    }

    private String getLoginTimeUpdateLockKey(Long taskId) {
        return LOCK_KEY_PREFIX + taskId;
    }

    @Override
    public void handleIfTaskTimeout(Long taskId) {
        logger.info("魔蝎超时任务检测 >>> taskId={}", taskId);
        try {
            String lockKey = RedisKeyUtils.genRedisLockKey("moxie_task_timeout_check", Constants.SAAS_ENV_VALUE, String.valueOf(taskId));
            redissonLocks.tryLock(lockKey, 10, 180, TimeUnit.SECONDS, () -> {
                Task task = taskRepository.getTaskById(taskId);
                logger.info("handleIfTaskTimeout async : taskId={}, task={}", taskId, JSON.toJSONString(task));

                Byte taskStatus = task.getStatus();
                if (!ETaskStatus.RUNNING.getStatus().equals(taskStatus)) {
                    logger.info("handleIfTaskTimeout error : the task is completed: {}", JSON.toJSONString(task));
                    return;
                }
                Integer timeout = bizTypeManager.getBizTimeout(task.getBizType());
                if (timeout == null) {
                    return;
                }
                Date loginTime = queryLoginTime(taskId);
                if (loginTime == null) {
                    return;
                }
                // 任务超时: 当前时间-登录时间>超时时间
                Date currentTime = new Date();
                Date timeoutDate = DateUtils.plusSeconds(loginTime, timeout);
                logger.info("moxie isTaskTimeout: taskid={}，loginTime={},current={},timeout={}", taskId, DateUtils.format(loginTime), DateUtils.format(currentTime), timeout);
                if (!timeoutDate.after(currentTime)) {
                    // 增加日志：任务超时
                    String errorMessage = "任务超时：当前时间(" + DateFormatUtils.format(currentTime, "yyyy-MM-dd HH:mm:ss") + ") - 登录时间("
                        + DateFormatUtils.format(loginTime, "yyyy-MM-dd HH:mm:ss") + ")> 超时时间(" + timeout + "秒)";

                    taskLogService.log(task.getId(), TaskStatusMsgEnum.TIMEOUT_MSG, errorMessage);

                    // 超时处理：任务更新为失败
                    MoxieDirectivePacket directivePacket = new MoxieDirectivePacket(EDirective.TASK_FAIL);
                    directivePacket.setTaskId(task.getId());
                    directivePacket.setRemark(JSON.toJSONString(ImmutableMap.of("taskErrorMsg", errorMessage)));
                    directiveService.process(directivePacket);
                }
            });
        } catch (InterruptedException e) {
            throw new UnexpectedException("Thread interrupted when checking moxie's timeout task! - taskId:" + " " + taskId, e);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void handleAfterLoginTimeout(Long taskId, String moxieTaskId) {
        // 前端收到登录超时后,重置登录时间
        this.logLoginTime(taskId);

        // 登录失败(如用户名密码错误),需删除task_attribute中此taskId对应的moxieTaskId,重新登录时,可正常轮询/login/submit接口
        taskAttributeRepository.insertOrUpdateAttribute(taskId, ETaskAttribute.FUND_MOXIE_TASKID.getAttribute(), "");

        // 记录登录超时日志
        Map<String, Object> map = Maps.newHashMap();
        map.put("error", "登录超时");
        map.put("moxieTaskId", moxieTaskId);
        taskLogService.insertTaskLog(taskId, ETaskStep.LOGIN_FAIL.getText(), new Date(), JSON.toJSONString(map));
    }
}

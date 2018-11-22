package com.treefinance.saas.taskcenter.biz.service.thread;

import com.google.common.collect.Maps;
import com.treefinance.saas.assistant.model.Constants;
import com.treefinance.saas.taskcenter.context.cache.RedisDao;
import com.treefinance.saas.taskcenter.context.config.DiamondConfig;
import com.treefinance.saas.taskcenter.biz.service.TaskAliveService;
import com.treefinance.saas.taskcenter.biz.service.TaskService;
import com.treefinance.saas.taskcenter.context.RedisKeyUtils;
import com.treefinance.saas.taskcenter.context.SpringUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.Map;

/**
 * Good Luck Bro , No Bug !
 *
 * @author haojiahong
 * @date 2018/5/28
 */
public class TaskActiveTimeoutThread implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(TaskActiveTimeoutThread.class);

    private TaskAliveService taskAliveService;
    private TaskService taskService;
    private DiamondConfig diamondConfig;
    private RedisDao redisDao;
    private Long taskId;
    private Date startTime;

    public TaskActiveTimeoutThread(Long taskId, Date startTime) {
        this.taskAliveService = SpringUtils.getBean(TaskAliveService.class);
        this.taskService = SpringUtils.getBean(TaskService.class);
        this.diamondConfig = SpringUtils.getBean(DiamondConfig.class);
        this.redisDao = SpringUtils.getBean(RedisDao.class);
        this.taskId = taskId;
        this.startTime = startTime;
    }

    @Override
    public void run() {
        //保证取消任务只会执行一次
        Map<String, Object> lockMap = Maps.newHashMap();
        String lockKey = RedisKeyUtils.genRedisLockKey("task-alive-time-job-task", Constants.SAAS_ENV_VALUE, String.valueOf(taskId));
        try {
            lockMap = redisDao.acquireLock(lockKey, 3 * 60 * 1000L);
            if (MapUtils.isEmpty(lockMap)) {
                return;
            }
            String valueStr = taskAliveService.getTaskAliveTime(taskId);
            if (StringUtils.isBlank(valueStr)) {
                logger.info("任务已经被取消了taskId={}", taskId);
                return;
            }

            Long lastActiveTime = Long.parseLong(valueStr);
            long diff = diamondConfig.getTaskMaxAliveTime();
            if (startTime.getTime() - lastActiveTime > diff) {
                logger.info("任务活跃时间超时,取消任务,taskId={}", taskId);
                taskService.cancelTask(taskId);
                //删除记录的任务活跃时间
                taskAliveService.deleteTaskAliveTime(taskId);
            }
        } finally {
            redisDao.releaseLock(lockKey, lockMap, 3 * 60 * 1000L);
        }
    }
}

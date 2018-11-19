package com.treefinance.saas.taskcenter.biz.service;

import com.google.common.collect.Maps;
import com.treefinance.saas.taskcenter.biz.cache.redis.RedisDao;
import com.treefinance.saas.taskcenter.common.util.DateUtils;
import com.treefinance.saas.taskcenter.common.enums.ETaskAttribute;
import com.treefinance.saas.taskcenter.common.enums.ETaskStatus;
import com.treefinance.saas.taskcenter.dao.entity.Task;
import com.treefinance.saas.taskcenter.dao.entity.TaskAttribute;
import com.treefinance.saas.taskcenter.dao.mapper.TaskMapper;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author haojiahong
 * @date 2018/9/21
 */
@Service
public class TaskAliveService {

    private static final Logger logger = LoggerFactory.getLogger(TaskAliveService.class);
    private final static String PREFIX_ALIVE_TIME_UPDATE_LOCK_KEY = "task-center:alive-time-update-lock:%s";
    private final static String PREFIX_TASK_ACTIVE_TIME_KEY = "task-center:task-active-time:%s";


    @Autowired
    private TaskMapper taskMapper;
    @Autowired
    private RedisDao redisDao;
    @Autowired
    private TaskAttributeService taskAttributeService;

    /**
     * 更新任务最近活跃时间
     * 可能存在多个请求同时更新活跃时间,未获得锁的请求可过滤掉
     *
     * @param taskId
     * @param date
     */
    public void updateTaskActiveTime(Long taskId, Date date) {
        Map<String, Object> lockMap = Maps.newHashMap();
        try {
            lockMap = redisDao.acquireLock(String.format(PREFIX_ALIVE_TIME_UPDATE_LOCK_KEY, taskId), 60 * 1000L);
            if (lockMap != null) {
                Task task = taskMapper.selectByPrimaryKey(taskId);
                if (task == null) {
                    return;
                }
                if (!ETaskStatus.RUNNING.getStatus().equals(task.getStatus())) {
                    logger.info("任务已结束,无需更新任务活跃时间,taskId={}", taskId);
                    return;
                }
                taskAttributeService.insertOrUpdateSelective(taskId, ETaskAttribute.ALIVE_TIME.getAttribute(), DateUtils.getDateStrByDate(date));
                String key = String.format(PREFIX_TASK_ACTIVE_TIME_KEY, taskId);
                String value = date.getTime() + "";
                redisDao.setEx(key, value, 30, TimeUnit.MINUTES);
            }
        } finally {
            redisDao.releaseLock(String.format(PREFIX_ALIVE_TIME_UPDATE_LOCK_KEY, taskId), lockMap, 60 * 1000L);
        }
    }

    public void updateTaskActiveTime(Long taskId) {
        this.updateTaskActiveTime(taskId, new Date());
    }


    public String getTaskAliveTime(Long taskId) {
        String key = String.format(PREFIX_TASK_ACTIVE_TIME_KEY, taskId);
        String lastActiveTimeStr = redisDao.get(key);
        if (StringUtils.isNotBlank(lastActiveTimeStr)) {
            return lastActiveTimeStr;
        } else {
            TaskAttribute taskAttribute = taskAttributeService.findByName(taskId, ETaskAttribute.ALIVE_TIME.getAttribute(), false);
            if (taskAttribute == null) {
                return null;
            }
            String dateStr = taskAttribute.getValue();
            Date date = DateUtils.getDateByStr(dateStr);
            this.updateTaskActiveTime(taskId, date);
            return date.getTime() + "";
        }
    }

    public void deleteTaskAliveTime(Long taskId) {
        String key = String.format(PREFIX_TASK_ACTIVE_TIME_KEY, taskId);
        redisDao.deleteKey(key);
        taskAttributeService.deleteByTaskIdAndName(taskId, ETaskAttribute.ALIVE_TIME.getAttribute());
        logger.info("删除记录的任务活跃时间, taskId={}", taskId);
    }


}

package com.treefinance.saas.taskcenter.biz.service.thread;

import com.google.common.collect.Maps;
import com.treefinance.saas.assistant.model.Constants;
import com.treefinance.saas.taskcenter.biz.service.TaskTimeService;
import com.treefinance.saas.taskcenter.biz.service.task.TaskTimeoutHandler;
import com.treefinance.saas.taskcenter.context.SpringUtils;
import com.treefinance.saas.taskcenter.context.component.AbstractService;
import com.treefinance.saas.taskcenter.context.enums.ETaskStatus;
import com.treefinance.saas.taskcenter.dao.entity.Task;
import com.treefinance.saas.taskcenter.dao.repository.TaskRepository;
import com.treefinance.saas.taskcenter.dto.TaskDTO;
import com.treefinance.saas.taskcenter.share.cache.redis.RedisDao;
import com.treefinance.saas.taskcenter.share.cache.redis.RedisKeyUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Good Luck Bro , No Bug !
 *
 * @author haojiahong
 * @date 2018/5/30
 */
public class TaskCrawlerTimeoutThread extends AbstractService implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(TaskCrawlerTimeoutThread.class);

    private Long taskId;
    private List<TaskTimeoutHandler> taskTimeoutHandlers;
    private TaskTimeService taskTimeService;
    private TaskRepository taskRepository;
    private RedisDao redisDao;

    public TaskCrawlerTimeoutThread(Long taskId, List<TaskTimeoutHandler> taskTimeoutHandlers) {
        this.taskTimeService = SpringUtils.getBean(TaskTimeService.class);
        this.taskRepository = SpringUtils.getBean(TaskRepository.class);
        this.redisDao = SpringUtils.getBean(RedisDao.class);
        this.taskId = taskId;
        this.taskTimeoutHandlers = taskTimeoutHandlers;
    }

    @Override
    public void run() {
        Map<String, Object> lockMap = Maps.newHashMap();
        String lockKey = RedisKeyUtils.genRedisLockKey("task-crawler-time-job-task", Constants.SAAS_ENV_VALUE, String.valueOf(taskId));
        try {
            lockMap = redisDao.acquireLock(lockKey, 3 * 60 * 1000L);
            if (MapUtils.isEmpty(lockMap)) {
                return;
            }
            Task task = taskRepository.getTaskById(taskId);
            // 如果任务已结束,则不处理.
            if (!ETaskStatus.RUNNING.getStatus().equals(task.getStatus())) {
                return;
            }
            TaskDTO taskDTO = convert(task, TaskDTO.class);
            Date loginTime = taskTimeService.getLoginTime(taskDTO.getId());
            if (loginTime == null) {
                return;
            }
            Integer timeout = taskTimeService.getCrawlerTimeoutSeconds(taskDTO.getId());
            taskTimeoutHandlers.forEach(handler -> {
                try {
                    handler.handleTaskTimeout(taskDTO, timeout, loginTime);
                    logger.info("handle timeout task: handler={},task={},loginTime={},timeout={}", handler.getClass(), taskId,
                        DateFormatUtils.format(loginTime, "yyyyMMdd HH:mm:ss"), timeout);
                } catch (Exception e) {
                    logger.error("handle timeout task error: handler={},task={},loginTime={},timeout={}", handler.getClass(), taskId,
                        DateFormatUtils.format(loginTime, "yyyyMMdd HH:mm:ss"), timeout, e);
                }
            });
        } finally {
            redisDao.releaseLock(lockKey, lockMap, 3 * 60 * 1000L);
        }
    }
}

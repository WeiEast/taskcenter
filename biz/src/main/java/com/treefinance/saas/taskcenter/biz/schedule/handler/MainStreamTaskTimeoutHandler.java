package com.treefinance.saas.taskcenter.biz.schedule.handler;

import com.google.common.collect.Maps;
import com.treefinance.saas.taskcenter.biz.service.TaskLogService;
import com.treefinance.saas.taskcenter.biz.service.directive.DirectivePacket;
import com.treefinance.saas.taskcenter.biz.service.directive.DirectiveService;
import com.treefinance.saas.taskcenter.common.enums.EDirective;
import com.treefinance.saas.taskcenter.context.enums.TaskStatusMsgEnum;
import com.treefinance.saas.taskcenter.dao.entity.Task;
import com.treefinance.saas.taskcenter.interation.manager.SpiderTaskManager;
import com.treefinance.toolkit.util.DateUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Map;

/**
 * 主流程数据的超时处理 Created by yh-treefinance on 2017/12/25.
 */
@Component
public class MainStreamTaskTimeoutHandler implements TaskTimeoutHandler {
    private static final Logger logger = LoggerFactory.getLogger(MainStreamTaskTimeoutHandler.class);

    @Autowired
    private DirectiveService directiveService;
    @Autowired
    private TaskLogService taskLogService;
    @Autowired
    private SpiderTaskManager spiderTaskManager;

    @Override
    public void handle(Task task, Integer timeout, Date loginTime) {
        Long taskId = task.getId();
        // 任务超时: 当前时间-登录时间>超时时间
        Date currentTime = new Date();
        logger.info("主流程数据：isTaskTimeout: taskid={}，loginTime={},current={},timeout={}", taskId, DateUtils.format(loginTime), DateUtils.format(currentTime), timeout);

        // 增加日志：任务超时
        String errorMessage = "任务超时：当前时间(" + DateFormatUtils.format(currentTime, "yyyy-MM-dd HH:mm:ss") + ") - 登录时间(" + DateFormatUtils.format(loginTime, "yyyy-MM-dd HH:mm:ss")
            + ")> 超时时间(" + timeout + "秒)";
        taskLogService.log(task.getId(), TaskStatusMsgEnum.TIMEOUT_MSG, errorMessage);

        // 通知爬数取消任务
        Map<String, String> extMap = Maps.newHashMap();
        extMap.put("reason", "timeout");
        spiderTaskManager.cancelQuietly(taskId, extMap);

        // 超时处理：任务更新为失败
        directiveService.process(new DirectivePacket(EDirective.TASK_FAIL, task.getId()));
    }
}

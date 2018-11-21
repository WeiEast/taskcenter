package com.treefinance.saas.taskcenter.biz.service.task.timeout;

import com.datatrees.spider.share.api.SpiderTaskApi;
import com.google.common.collect.Maps;
import com.treefinance.saas.taskcenter.biz.service.TaskLogService;
import com.treefinance.saas.taskcenter.biz.service.impl.TaskLogServiceImpl;
import com.treefinance.saas.taskcenter.biz.service.directive.DirectiveService;
import com.treefinance.saas.taskcenter.biz.service.task.TaskTimeoutHandler;
import com.treefinance.saas.taskcenter.common.enums.EDirective;
import com.treefinance.saas.taskcenter.common.enums.TaskStatusMsgEnum;
import com.treefinance.saas.taskcenter.common.model.dto.DirectiveDTO;
import com.treefinance.saas.taskcenter.common.model.dto.TaskDTO;
import com.treefinance.saas.taskcenter.common.util.CommonUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Map;

/**
 * 主流程数据的超时处理
 * Created by yh-treefinance on 2017/12/25.
 */
@Component
public class MainStreamTaskTimeoutHandler implements TaskTimeoutHandler {
    private static final Logger logger = LoggerFactory.getLogger(MainStreamTaskTimeoutHandler.class);

    @Autowired
    private DirectiveService directiveService;
    @Autowired
    private TaskLogService taskLogService;
    @Autowired
    private SpiderTaskApi spiderTaskApi;

    @Override
    public void handleTaskTimeout(TaskDTO task, Integer timeout, Date loginTime) {
        Long taskId = task.getId();
        // 任务超时: 当前时间-登录时间>超时时间
        Date currentTime = new Date();
        logger.info("主流程数据：isTaskTimeout: taskid={}，loginTime={},current={},timeout={}",
                taskId, CommonUtils.date2Str(loginTime), CommonUtils.date2Str(currentTime), timeout);

        // 增加日志：任务超时
        String errorMessage = "任务超时：当前时间(" + DateFormatUtils.format(currentTime, "yyyy-MM-dd HH:mm:ss")
                + ") - 登录时间(" + DateFormatUtils.format(loginTime, "yyyy-MM-dd HH:mm:ss")
                + ")> 超时时间(" + timeout + "秒)";
        taskLogService.log(task.getId(), TaskStatusMsgEnum.TIMEOUT_MSG, errorMessage);

        // 通知爬数取消任务
        try {
            Map<String, String> extMap = Maps.newHashMap();
            extMap.put("reason", "timeout");
            this.spiderTaskApi.cancel(taskId, extMap);
        } catch (Exception e) {
            logger.error("crawlerService.cancel(" + taskId + ") failed", e);
        }
        // 超时处理：任务更新为失败
        DirectiveDTO directiveDTO = new DirectiveDTO();
        directiveDTO.setTaskId(task.getId());
        directiveDTO.setDirective(EDirective.TASK_FAIL.getText());
        directiveService.process(directiveDTO);

    }
}

package com.treefinance.saas.taskcenter.biz.schedule.handler;

import com.alibaba.fastjson.JSON;
import com.treefinance.saas.taskcenter.biz.callback.AsyncGrabDataHandler;
import com.treefinance.saas.taskcenter.context.enums.EDataType;
import com.treefinance.saas.taskcenter.dao.entity.Task;
import com.treefinance.saas.taskcenter.dao.entity.TaskCallbackLog;
import com.treefinance.saas.taskcenter.biz.callback.AsyncGrabMessage;
import com.treefinance.saas.taskcenter.common.enums.EBizType;
import com.treefinance.saas.taskcenter.interation.manager.domain.CallbackConfigBO;
import com.treefinance.saas.taskcenter.service.AppCallbackConfigService;
import com.treefinance.saas.taskcenter.service.TaskCallbackLogService;
import com.treefinance.toolkit.util.DateUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 运营商流量数据超时处理 Created by yh-treefinance on 2017/12/25.
 */
@Component
public class OperatorFlowTaskTimeoutHandler implements TaskTimeoutHandler {
    private static final Logger logger = LoggerFactory.getLogger(OperatorFlowTaskTimeoutHandler.class);

    @Autowired
    private AppCallbackConfigService appCallbackConfigService;
    @Autowired
    private AsyncGrabDataHandler asyncGrabDataHandler;
    @Autowired
    private TaskCallbackLogService taskCallbackLogService;

    @Override
    public void handle(Task task, Integer timeout, Date loginTime) {
        // 1.非运营商任务
        if (!EBizType.OPERATOR.getCode().equals(task.getBizType())) {
            return;
        }
        Long taskId = task.getId();
        // 任务超时: 当前时间-登录时间>超时时间
        Date currentTime = new Date();
        logger.info("运营商流量：isTaskTimeout: taskid={}，loginTime={},current={},timeout={}", taskId, DateUtils.format(loginTime), DateUtils.format(currentTime), timeout);
        // 2.判断此商户是否配置了运营商流量数据的回调

        String appId = task.getAppId();
        Byte bizType = task.getBizType();
        List<CallbackConfigBO> callbackConfigs = appCallbackConfigService.queryConfigsByAppIdAndBizType(appId, bizType, EDataType.OPERATOR_FLOW);
        logger.info("根据业务类型匹配回调配置结果:taskId={},configList={}", task.getId(), JSON.toJSONString(callbackConfigs));
        if (CollectionUtils.isEmpty(callbackConfigs)) {
            logger.info("handle operator flow task timeout, merchant don't have callback configs: taskId={},timeout={},loginTime={}", taskId, timeout,
                DateFormatUtils.format(loginTime, "yyyyMMdd HH:mm:ss"));
            return;
        }

        // 3.查询流量子任务是否已经回调通知过
        List<Long> configIds = callbackConfigs.stream().map(config -> config.getId().longValue()).collect(Collectors.toList());
        List<TaskCallbackLog> taskCallbackLogs = taskCallbackLogService.queryTaskCallbackLogsByTaskIdAndInConfigIds(taskId, configIds);
        if (CollectionUtils.isNotEmpty(taskCallbackLogs)) {
            logger.info("handle operator flow task timeout, has been callback already: taskId={},timeout={},loginTime={}", taskId, timeout,
                DateFormatUtils.format(loginTime, "yyyyMMdd HH:mm:ss"));
            return;
        }
        // 4.构建数据,发起超时回调
        AsyncGrabMessage asyncGrabMessage = new AsyncGrabMessage();
        asyncGrabMessage.setTaskId(taskId);
        asyncGrabMessage.setUniqueId(task.getUniqueId());
        asyncGrabMessage.setStatus(0);
        asyncGrabMessage.setErrorMsg("任务超时");
        asyncGrabMessage.setDataType(EDataType.OPERATOR_FLOW.getType().intValue());
        asyncGrabMessage.setTimestamp(System.currentTimeMillis());
        asyncGrabDataHandler.handle(asyncGrabMessage);
    }
}

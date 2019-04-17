package com.treefinance.saas.taskcenter.biz.service.task.timeout;

import com.treefinance.saas.taskcenter.biz.service.GrapDataCallbackService;
import com.treefinance.saas.taskcenter.biz.service.TaskCallbackLogService;
import com.treefinance.saas.taskcenter.biz.service.task.TaskTimeoutHandler;
import com.treefinance.saas.taskcenter.context.enums.EDataType;
import com.treefinance.saas.taskcenter.dao.entity.TaskCallbackLog;
import com.treefinance.saas.taskcenter.dto.AsycGrapDTO;
import com.treefinance.saas.taskcenter.dto.TaskDTO;
import com.treefinance.saas.taskcenter.facade.enums.EBizType;
import com.treefinance.saas.taskcenter.interation.manager.domain.CallbackConfigBO;
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
    private GrapDataCallbackService grapDataCallbackService;
    @Autowired
    private TaskCallbackLogService taskCallbackLogService;

    @Override
    public void handleTaskTimeout(TaskDTO task, Integer timeout, Date loginTime) {
        // 1.非运营商任务
        if (!EBizType.OPERATOR.getCode().equals(task.getBizType())) {
            return;
        }
        Long taskId = task.getId();
        // 任务超时: 当前时间-登录时间>超时时间
        Date currentTime = new Date();
        logger.info("运营商流量：isTaskTimeout: taskid={}，loginTime={},current={},timeout={}", taskId, DateUtils.format(loginTime), DateUtils.format(currentTime), timeout);
        // 2.判断此商户是否配置了运营商流量数据的回调
        List<CallbackConfigBO> callbackConfigs = grapDataCallbackService.getCallbackConfigs(task, EDataType.OPERATOR_FLOW);
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
        AsycGrapDTO asycGrapDTO = new AsycGrapDTO();
        asycGrapDTO.setTaskId(taskId);
        asycGrapDTO.setUniqueId(task.getUniqueId());
        asycGrapDTO.setStatus(0);
        asycGrapDTO.setErrorMsg("任务超时");
        asycGrapDTO.setDataType(EDataType.OPERATOR_FLOW.getType().intValue());
        asycGrapDTO.setTimestamp(new Date().getTime());
        grapDataCallbackService.handleAyscData(asycGrapDTO);
    }
}

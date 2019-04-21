package com.treefinance.saas.taskcenter.biz.service.directive.process.impl;

import com.alibaba.fastjson.JSON;
import com.treefinance.saas.taskcenter.biz.service.MonitorService;
import com.treefinance.saas.taskcenter.biz.service.directive.process.AbstractCallbackDirectiveProcessor;
import com.treefinance.saas.taskcenter.biz.service.directive.process.DirectiveContext;
import com.treefinance.saas.taskcenter.common.enums.EBizType;
import com.treefinance.saas.taskcenter.common.enums.EDirective;
import com.treefinance.saas.taskcenter.common.enums.ETaskStatus;
import com.treefinance.saas.taskcenter.context.Constants;
import com.treefinance.saas.taskcenter.interation.manager.domain.AppLicense;
import com.treefinance.saas.taskcenter.service.domain.AttributedTaskInfo;
import com.treefinance.saas.taskcenter.share.AsyncExecutor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 任务失败回调 Created by yh-treefinance on 2017/7/10.
 */
@Component
public class FailureDirectiveProcessor extends AbstractCallbackDirectiveProcessor {
    @Autowired
    protected MonitorService monitorService;
    @Autowired
    private AsyncExecutor asyncExecutor;

    @Override
    public EDirective getSpecifiedDirective() {
        return EDirective.TASK_FAIL;
    }

    @Override
    protected void doProcess(DirectiveContext context) {
        AttributedTaskInfo task = context.getTask();
        String appId = task.getAppId();

        // 1.任务置为失败
        task.setStatus(ETaskStatus.FAIL.getStatus());

        // 2.更新任务状态
        String errorCode = taskService.updateStatusIfDone(task.getId(), ETaskStatus.FAIL.getStatus());
        task.setStepCode(errorCode);

        // 3.发送监控消息
        monitorService.sendMonitorMessage(task.getId());

        // 4.获取商户密钥
        AppLicense appLicense = licenseManager.getAppLicenseByAppId(appId);
        // 5.成数据map
        Map<String, Object> dataMap = generateDataMap(context);
        // 6.回调之前预处理
        precallback(dataMap, appLicense, context);

        handleTaskFailMsg(context, task);

        // 7.异步触发触发回调
        asyncExecutor.runAsync(context, dto -> callback(dataMap, appLicense, dto));
    }

    /**
     * 处理返回到前端的消息
     *
     * @param directiveDTO
     * @param task
     */
    private void handleTaskFailMsg(DirectiveContext directiveDTO, AttributedTaskInfo task) {
        try {
            if (EBizType.OPERATOR.getCode().equals(task.getBizType())) {
                Map<String, Object> remarkMap = JSON.parseObject(directiveDTO.getRemark());
                // 如果是运营商维护导致任务失败,爬数发来的任务指令中,directiveDTO的remark字段为{"errorMsg","当前运营商正在维护中，请稍后重试"}.
                // 如果是其他原因导致的任务失败,则返回下面的默认值.
                remarkMap.putIfAbsent("errorMsg", Constants.OPERATOR_TASK_FAIL_MSG);
                directiveDTO.setRemark(JSON.toJSONString(remarkMap));
                logger.info("handle task-fail-msg: result={},directiveDTO={}", JSON.toJSONString(directiveDTO));
            }
            if (EBizType.DIPLOMA.getCode().equals(task.getBizType())) {
                Map<String, Object> remarkMap = JSON.parseObject(directiveDTO.getRemark());
                remarkMap.put("errorMsg", Constants.DIPLOMA_TASK_FAIL_MSG);
                directiveDTO.setRemark(JSON.toJSONString(remarkMap));
                logger.info("handle task-fail-msg: result={},directiveDTO={}", JSON.toJSONString(directiveDTO));
            }
        } catch (Exception e) {
            logger.info("handle result failed : directiveDTO={}", JSON.toJSONString(directiveDTO), e);
        }
    }
}

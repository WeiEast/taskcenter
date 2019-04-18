package com.treefinance.saas.taskcenter.biz.service.moxie.directive.process.impl;

import com.treefinance.saas.taskcenter.biz.service.MonitorService;
import com.treefinance.saas.taskcenter.biz.service.moxie.directive.process.MoxieAbstractDirectiveProcessor;
import com.treefinance.saas.taskcenter.common.enums.ETaskStatus;
import com.treefinance.saas.taskcenter.context.enums.moxie.EMoxieDirective;
import com.treefinance.saas.taskcenter.dto.moxie.MoxieDirectiveDTO;
import com.treefinance.saas.taskcenter.interation.manager.domain.AppLicense;
import com.treefinance.saas.taskcenter.service.domain.AttributedTaskInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 成功指令处理 Created by yh-treefinance on 2017/7/6.
 */
@Component
public class MoxieSuccessDirectiveProcessor extends MoxieAbstractDirectiveProcessor {

    protected static final Logger logger = LoggerFactory.getLogger(MoxieSuccessDirectiveProcessor.class);

    @Autowired
    protected MonitorService monitorService;

    @Override
    protected void doProcess(EMoxieDirective directive, MoxieDirectiveDTO directiveDTO) {
        AttributedTaskInfo task = directiveDTO.getTask();
        Long taskId = task.getId();
        String appId = task.getAppId();

        // 获取商户密钥
        AppLicense appLicense = licenseManager.getAppLicenseByAppId(appId);
        // 生成数据map
        Map<String, Object> dataMap = generateDataMap(directiveDTO);
        // 回调之前预处理
        precallback(dataMap, appLicense, directiveDTO);
        // 触发回调: 0-无需回调，1-回调成功，-1-回调失败
        int result = callback(dataMap, appLicense, directiveDTO);

        if (result == 0) {
            task.setStatus(ETaskStatus.SUCCESS.getStatus());
        } else if (result == 1) {
            task.setStatus(ETaskStatus.SUCCESS.getStatus());
        } else {
            // 指令发生变更 ： task_success -> callback_fail
            taskNextDirectiveService.insert(taskId, directiveDTO.getDirective());

            task.setStatus(ETaskStatus.FAIL.getStatus());
            directiveDTO.setDirective(EMoxieDirective.CALLBACK_FAIL.getText());
        }
        // 更新任务状态,记录任务成功日志
        String stepCode = taskService.updateStatusIfDone(taskId, task.getStatus());
        task.setStepCode(stepCode);

        // 发送监控消息
        monitorService.sendMonitorMessage(task.getId());

    }

}

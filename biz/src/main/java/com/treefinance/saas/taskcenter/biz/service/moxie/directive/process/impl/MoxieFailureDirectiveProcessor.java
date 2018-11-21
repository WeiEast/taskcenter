package com.treefinance.saas.taskcenter.biz.service.moxie.directive.process.impl;

import com.treefinance.saas.taskcenter.biz.service.common.AsyncExecutor;
import com.treefinance.saas.taskcenter.biz.service.monitor.MonitorService;
import com.treefinance.saas.taskcenter.biz.service.moxie.directive.process.MoxieAbstractDirectiveProcessor;
import com.treefinance.saas.taskcenter.common.enums.ETaskStatus;
import com.treefinance.saas.taskcenter.common.enums.moxie.EMoxieDirective;
import com.treefinance.saas.taskcenter.common.model.dto.AppLicenseDTO;
import com.treefinance.saas.taskcenter.common.model.dto.TaskDTO;
import com.treefinance.saas.taskcenter.common.model.moxie.MoxieDirectiveDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 *
 */
@Component
public class MoxieFailureDirectiveProcessor extends MoxieAbstractDirectiveProcessor {
    @Autowired
    private AsyncExecutor asyncExecutor;
    @Autowired
    private MonitorService monitorService;

    @Override
    protected void doProcess(EMoxieDirective directive, MoxieDirectiveDTO directiveDTO) {
        TaskDTO taskDTO = directiveDTO.getTask();
        String appId = taskDTO.getAppId();

        // 任务置为失败
        taskDTO.setStatus(ETaskStatus.FAIL.getStatus());

        // 更新任务状态,记录失败任务日志
        String stepCode = taskService.updateStatusIfDone(taskDTO.getId(), ETaskStatus.FAIL.getStatus());
        taskDTO.setStepCode(stepCode);

        //发送监控消息
        monitorService.sendMonitorMessage(taskDTO.getId());

        //获取商户秘钥,包装数据:任务失败后返回失败信息加密后通过指令传递给前端
        AppLicenseDTO appLicense = appLicenseService.getAppLicense(appId);
        //成数据map
        Map<String, Object> dataMap = generateDataMap(directiveDTO);
        //回调之前预处理
        precallback(dataMap, appLicense, directiveDTO);
        //异步触发触发回调
        asyncExecutor.runAsync(directiveDTO, _directiveDTO -> {
            callback(dataMap, appLicense, _directiveDTO);
        });


    }
}

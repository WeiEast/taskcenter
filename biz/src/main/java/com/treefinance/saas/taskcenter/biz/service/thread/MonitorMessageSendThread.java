package com.treefinance.saas.taskcenter.biz.service.thread;

import com.alibaba.fastjson.JSON;
import com.treefinance.saas.taskcenter.biz.service.TaskService;
import com.treefinance.saas.taskcenter.biz.service.monitor.EcommerceMonitorService;
import com.treefinance.saas.taskcenter.biz.service.monitor.EmailMonitorService;
import com.treefinance.saas.taskcenter.biz.service.monitor.MonitorPluginService;
import com.treefinance.saas.taskcenter.biz.service.monitor.OperatorMonitorService;
import com.treefinance.saas.taskcenter.context.SpringUtils;
import com.treefinance.saas.taskcenter.context.enums.ETaskStatus;
import com.treefinance.saas.taskcenter.dto.TaskDTO;
import com.treefinance.saas.taskcenter.facade.enums.EBizType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Good Luck Bro , No Bug !
 *
 * @author haojiahong
 * @date 2018/5/29
 */
public class MonitorMessageSendThread implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(MonitorMessageSendThread.class);

    private Long taskId;
    private TaskService taskService;
    private MonitorPluginService monitorPluginService;
    private OperatorMonitorService operatorMonitorService;
    private EcommerceMonitorService ecommerceMonitorService;
    private EmailMonitorService emailMonitorService;

    public MonitorMessageSendThread(Long taskId) {
        this.taskService = SpringUtils.getBean(TaskService.class);
        this.monitorPluginService = SpringUtils.getBean(MonitorPluginService.class);
        this.operatorMonitorService = SpringUtils.getBean(OperatorMonitorService.class);
        this.ecommerceMonitorService = SpringUtils.getBean(EcommerceMonitorService.class);
        this.emailMonitorService = SpringUtils.getBean(EmailMonitorService.class);
        this.taskId = taskId;
    }

    @Override
    public void run() {
        TaskDTO taskDTO = taskService.getById(taskId);
        Byte status = taskDTO.getStatus();
        // 仅成功、失败、取消发送任务
        if (!ETaskStatus.SUCCESS.getStatus().equals(status) && !ETaskStatus.FAIL.getStatus().equals(status) && !ETaskStatus.CANCEL.getStatus().equals(status)) {
            return;
        }

        // 发送任务监控消息
        monitorPluginService.sendTaskMonitorMessage(taskDTO);
        EBizType bizType = EBizType.of(taskDTO.getBizType());
        if (bizType != null) {
            switch (bizType) {
                case OPERATOR:
                    // 发送运营商监控消息
                    operatorMonitorService.sendMessage(taskDTO);
                    break;
                case ECOMMERCE:
                    // 发送电商监控消息
                    ecommerceMonitorService.sendMessage(taskDTO);
                    break;
                case EMAIL:
                case EMAIL_H5:
                    emailMonitorService.sendMessage(taskDTO);
                    break;
                default:
                    break;
            }
            logger.info("sendMonitorMessage: type={}, task={}", bizType, JSON.toJSONString(taskDTO));
        }
    }

}

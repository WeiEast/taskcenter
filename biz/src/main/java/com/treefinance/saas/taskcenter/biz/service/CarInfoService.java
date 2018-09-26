package com.treefinance.saas.taskcenter.biz.service;

import com.treefinance.saas.taskcenter.biz.service.monitor.MonitorService;
import com.treefinance.saas.taskcenter.common.enums.ETaskStatus;
import com.treefinance.saas.taskcenter.common.enums.ETaskStep;
import com.treefinance.saas.taskcenter.common.model.dto.CarInfoCollectTaskLogDTO;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Good Luck Bro , No Bug !
 * 车辆信息采集处理类
 *
 * @author haojiahong
 * @date 2018/5/31
 */
@Service
public class CarInfoService {

    private final static Logger logger = LoggerFactory.getLogger(CarInfoService.class);

    @Autowired
    private TaskService taskService;
    @Autowired
    private TaskLogService taskLogService;
    @Autowired
    private MonitorService monitorService;


    @Transactional(rollbackFor = Exception.class)
    public void updateCollectTaskStatusAndTaskLogAndSendMonitor(Long taskId, List<CarInfoCollectTaskLogDTO> logList) {
        for (CarInfoCollectTaskLogDTO log : logList) {
            taskLogService.insert(taskId, log.getMsg(), log.getOccurTime(), log.getErrorMsg());
            //任务成功
            if (StringUtils.equalsIgnoreCase(log.getMsg(), ETaskStep.TASK_SUCCESS.getText())) {
                taskService.updateTaskStatus(taskId, ETaskStatus.SUCCESS.getStatus());
            }
            //任务失败
            if (StringUtils.equalsIgnoreCase(log.getMsg(), ETaskStep.TASK_FAIL.getText())) {
                taskService.updateTaskStatus(taskId, ETaskStatus.FAIL.getStatus());
            }
        }
        monitorService.sendMonitorMessage(taskId);
    }


}

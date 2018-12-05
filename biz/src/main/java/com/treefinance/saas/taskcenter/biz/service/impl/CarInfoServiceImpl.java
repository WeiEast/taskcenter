/*
 * Copyright © 2015 - 2017 杭州大树网络技术有限公司. All Rights Reserved
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.treefinance.saas.taskcenter.biz.service.impl;

import com.treefinance.saas.taskcenter.biz.service.CarInfoService;
import com.treefinance.saas.taskcenter.biz.service.TaskLogService;
import com.treefinance.saas.taskcenter.biz.service.TaskService;
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
 * Good Luck Bro , No Bug ! 车辆信息采集处理类
 *
 * @author haojiahong
 * @date 2018/5/31
 */
@Service
public class CarInfoServiceImpl implements CarInfoService {

    private final static Logger logger = LoggerFactory.getLogger(CarInfoServiceImpl.class);

    @Autowired
    private TaskService taskService;
    @Autowired
    private TaskLogService taskLogService;
    @Autowired
    private MonitorService monitorService;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void updateCollectTaskStatusAndTaskLogAndSendMonitor(Long taskId, List<CarInfoCollectTaskLogDTO> logList) {
        for (CarInfoCollectTaskLogDTO log : logList) {
            taskLogService.insertTaskLog(taskId, log.getMsg(), log.getOccurTime(), log.getErrorMsg());
            // 任务成功
            if (StringUtils.equalsIgnoreCase(log.getMsg(), ETaskStep.TASK_SUCCESS.getText())) {
                taskService.updateStatusById(taskId, ETaskStatus.SUCCESS.getStatus());
            }
            // 任务失败
            if (StringUtils.equalsIgnoreCase(log.getMsg(), ETaskStep.TASK_FAIL.getText())) {
                taskService.updateStatusById(taskId, ETaskStatus.FAIL.getStatus());
            }
        }
        monitorService.sendMonitorMessage(taskId);
    }

}

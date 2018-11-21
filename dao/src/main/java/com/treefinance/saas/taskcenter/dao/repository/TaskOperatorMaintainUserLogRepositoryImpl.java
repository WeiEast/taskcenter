/*
 * Copyright © 2015 - 2017 杭州大树网络技术有限公司. All Rights Reserved
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package com.treefinance.saas.taskcenter.dao.repository;

import com.treefinance.basicservice.security.crypto.facade.EncryptionIntensityEnum;
import com.treefinance.basicservice.security.crypto.facade.ISecurityCryptoService;
import com.treefinance.commonservice.uid.UidService;
import com.treefinance.saas.taskcenter.dao.entity.TaskOperatorMaintainUserLog;
import com.treefinance.saas.taskcenter.dao.mapper.TaskOperatorMaintainUserLogMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.annotation.Nonnull;

/**
 * @author Jerry
 * @date 2018/11/21 16:49
 */
@Repository
public class TaskOperatorMaintainUserLogRepositoryImpl implements TaskOperatorMaintainUserLogRepository {
    @Autowired
    private TaskOperatorMaintainUserLogMapper taskOperatorMaintainUserLogMapper;
    @Autowired
    private UidService uidService;
    @Autowired
    private ISecurityCryptoService iSecurityCryptoService;

    @Override
    public void insertLog(@Nonnull Long taskId, @Nonnull String appId, @Nonnull String mobile,@Nonnull  String operatorName) {
        TaskOperatorMaintainUserLog log = new TaskOperatorMaintainUserLog();
        log.setId(uidService.getId());
        log.setTaskId(taskId);
        log.setAppId(appId);
        log.setMobile(iSecurityCryptoService.encrypt(mobile, EncryptionIntensityEnum.NORMAL));
        log.setOperatorName(operatorName);

        taskOperatorMaintainUserLogMapper.insertSelective(log);
    }
}

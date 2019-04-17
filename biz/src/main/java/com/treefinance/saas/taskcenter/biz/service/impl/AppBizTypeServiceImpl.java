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

package com.treefinance.saas.taskcenter.biz.service.impl;

import com.treefinance.saas.taskcenter.biz.service.AbstractService;
import com.treefinance.saas.taskcenter.biz.service.AppBizTypeService;
import com.treefinance.saas.taskcenter.interation.manager.BizTypeManager;
import com.treefinance.saas.taskcenter.interation.manager.domain.BizTypeInfoBO;
import org.springframework.stereotype.Service;

import javax.annotation.Nonnull;
import javax.annotation.Resource;

/**
 * Created by yh-treefinance on 2017/8/2.
 */
@Service
public class AppBizTypeServiceImpl extends AbstractService implements AppBizTypeService {

    @Resource
    private BizTypeManager bizTypeManager;

    @Override
    public Integer getBizTimeout(@Nonnull Byte bizType) {
        BizTypeInfoBO info = bizTypeManager.getBizTypeInfoByBizType(bizType);
        if (info != null) {
            Integer timeout = info.getTimeout();
            if (timeout != null) {
                return timeout;
            }
        }

        logger.warn("未获取到当前导入任务类型的超时设置，bizType信息丢失！bizType={}", bizType);
        return null;
    }

}

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

package com.treefinance.saas.taskcenter.service;

import com.treefinance.saas.taskcenter.context.enums.EDataType;
import com.treefinance.saas.taskcenter.interation.manager.domain.CallbackConfigBO;

import javax.annotation.Nonnull;

import java.util.List;

/**
 * @author Jerry
 * @date 2018/11/21 12:16
 */
public interface AppCallbackConfigService {

    /**
     * 获取指定业务类型的回调配置： 如果有配置该业务类型，则使用该业务类型；没有则使用全局配置
     *
     * @param appId 商户ID
     * @param bizType 业务类型
     * @param dataType 数据类型
     * @return 回调配置列表
     */
    List<CallbackConfigBO> queryConfigsByAppIdAndBizType(String appId, @Nonnull Byte bizType, @Nonnull EDataType dataType);
}

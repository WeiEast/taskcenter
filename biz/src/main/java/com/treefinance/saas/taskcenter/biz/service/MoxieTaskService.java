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

package com.treefinance.saas.taskcenter.biz.service;

import java.util.Date;

/**
 * @author Jerry
 * @date 2019-04-19 00:29
 */
public interface MoxieTaskService {

    /**
     * 记录魔蝎任务创建时间,即开始登录时间.
     *
     * @param taskId 任务ID
     */
    void logLoginTime(Long taskId);

    void logLoginTime(Long taskId, Date date);

    /**
     * 查询登录时间
     *
     * @param taskId 任务ID
     * @return 登录时间
     */
    Date queryLoginTime(Long taskId);

    void handleIfTaskTimeout(Long taskId);

    void handleAfterLoginTimeout(Long taskId, String moxieTaskId);
}

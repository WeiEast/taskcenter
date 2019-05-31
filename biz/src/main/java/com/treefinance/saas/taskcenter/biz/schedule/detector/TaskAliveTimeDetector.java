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

package com.treefinance.saas.taskcenter.biz.schedule.detector;

import javax.annotation.Nonnull;

import java.util.Date;

/**
 * @author Jerry
 * @date 2019-03-12 19:22
 */
public interface TaskAliveTimeDetector {

    /**
     * 任务活跃时间检测
     * 
     * @param taskId 任务ID
     * @param detectTime 任务检测时间
     * @throws InterruptedException
     */
    void detect(@Nonnull Long taskId, @Nonnull Date detectTime) throws InterruptedException;
}

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

package com.treefinance.saas.taskcenter.share.cache.redis;

import com.google.common.base.Joiner;

/**
 * @author haojiahong
 */
public final class RedisKeyUtils {

    private final static String PREFIX_REDIS_LOCK_KEY = "saas-grap-server:redis_lock";

    private final static String PREFIX_TASK_LOGIN_TIME_KEY = "saas_gateway_task_time:";

    private RedisKeyUtils() {}

    public static String genRedisLockKey(String prefix, String... s) {
        return Joiner.on(":").join(PREFIX_REDIS_LOCK_KEY, prefix, s);
    }

    /**
     * 获取记录任务登录时间的redis key
     *
     * @param taskId
     * @return
     */
    public static String genTaskLoginTimeKey(Long taskId) {
        return PREFIX_TASK_LOGIN_TIME_KEY + taskId;
    }

}

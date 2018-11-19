package com.treefinance.saas.taskcenter.biz.util;

import com.google.common.base.Joiner;

/**
 * @author haojiahong
 */
public final class RedisKeyUtils {

    private final static String PREFIX_REDIS_LOCK_KEY = "saas-grap-server:redis_lock";

    private final static String PREFIX_TASK_LOGIN_TIME_KEY = "saas_gateway_task_time:";

    private RedisKeyUtils() {
    }

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

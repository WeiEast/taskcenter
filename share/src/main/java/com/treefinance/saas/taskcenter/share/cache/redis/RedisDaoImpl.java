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

import com.treefinance.saas.taskcenter.context.Constants;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Component("redisDao")
public class RedisDaoImpl implements RedisDao {

    private static final Logger logger = LoggerFactory.getLogger(RedisDaoImpl.class);

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    public String get(String key) {
        try {
            return stringRedisTemplate.opsForValue().get(key);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return null;
        }
    }
    @Override
    public  Object getObject(String key){
        try {
            return redisTemplate.opsForValue().get(key);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return null;
        }
    }

    @Override
    public Boolean setEx(String key, String value, long timeout, TimeUnit unit) {
        try {
            stringRedisTemplate.opsForValue().set(key, value, timeout, unit);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return false;
        }
        return true;
    }
    @Override
    public void setObject(String key, Object value, Long time,TimeUnit unit) {
        try {
            redisTemplate.opsForValue().set(key, value,time,unit);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);

        }
    }

    @Override
    public Long incrBy(String key, long increment, long timeout, TimeUnit unit) {
        try {
            Long count = stringRedisTemplate.opsForValue().increment(key, increment);
            stringRedisTemplate.expire(key, timeout, unit);
            return count;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return null;
        }
    }

    @Override
    public boolean saveString2List(final String key, final String value) {
        return saveListString(key, Collections.singletonList(value));
    }

    @Override
    public boolean saveListString(final String key, final List<String> valueList) {
        Long result = stringRedisTemplate.opsForList().rightPushAll(key, valueList.toArray(new String[0]));
        stringRedisTemplate.expire(key, Constants.REDIS_KEY_TIMEOUT, TimeUnit.SECONDS);
        return result != null;
    }

    @Override
    public String getStringFromList(final String key) {
        return stringRedisTemplate.opsForList().rightPop(key);
    }

    @Override
    public String pullResult(final String obtainRedisKey) {
        try {
            return stringRedisTemplate.opsForValue().get(obtainRedisKey);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return null;
        }
    }

    @Override
    public boolean pushMessage(final String submitRedisKey, final String messageType) {
        return this.pushMessage(submitRedisKey, messageType, Constants.REDIS_KEY_TIMEOUT);
    }

    @Override
    public boolean pushMessage(String submitRedisKey, String messageType, int ttlSeconds) {
        try {
            stringRedisTemplate.opsForValue().set(submitRedisKey, messageType, ttlSeconds, TimeUnit.SECONDS);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return false;
        }
        return true;
    }

    @Override
    public void deleteKey(String key) {
        stringRedisTemplate.delete(key);
    }

    @Override
    public Map<String, Object> acquireLock(String lockKey, long expired) {
        Map<String, Object> map = new HashMap<>(2);
        long value = System.currentTimeMillis() + expired + 1;
        boolean acquired = stringRedisTemplate.opsForValue().setIfAbsent(lockKey, String.valueOf(value));
        if (acquired) {
            map.put("isSuccess", true);
            map.put("expireTimeStr", String.valueOf(value));
            return map;
        } else {
            String oldValueStr = stringRedisTemplate.opsForValue().get(lockKey);
            // 如果其他资源之前获得锁已经超时
            if (StringUtils.isNotBlank(oldValueStr) && Long.parseLong(oldValueStr) < System.currentTimeMillis()) {
                String getValue = stringRedisTemplate.opsForValue().getAndSet(lockKey, String.valueOf(value));
                // 上一个锁超时后会有很多线程去争夺锁，所以只有拿到oldValue的线程才是获得锁的。
                if (Long.parseLong(getValue) == Long.parseLong(oldValueStr)) {
                    map.put("isSuccess", true);
                    map.put("expireTimeStr", String.valueOf(value));
                    return map;
                } else {
                    return null;
                }
            } else {
                return null;
            }
        }
    }

    @Override
    public void releaseLock(String lockKey, Map<String, Object> lockMap, long expireMsecs) {
        if (MapUtils.isEmpty(lockMap)) {
            return;
        }
        Boolean locked = (Boolean)lockMap.get("isSuccess");
        String lockExpiresStr = (String)lockMap.get("expireTimeStr");
        if (locked) {
            String oldValueStr = stringRedisTemplate.opsForValue().get(lockKey);
            if (oldValueStr != null) {
                // 竞争的 redis.getSet 导致其时间跟原有的由误差，若误差在 超时范围内，说明仍旧是 原来的锁
                long diff = Long.parseLong(lockExpiresStr) - Long.parseLong(oldValueStr);
                if (diff < expireMsecs) {
                    stringRedisTemplate.delete(lockKey);
                } else {
                    // 这个进程的锁超时了，被新的进程锁获得替换了。则不进行任何操作。打印日志，方便后续跟进
                    logger.error("the lockKey over time.lockKey:{}.expireMsecs:{},over time is {}", lockKey, expireMsecs,
                        System.currentTimeMillis() - Long.valueOf(lockExpiresStr));
                }
            }
        }

    }
}

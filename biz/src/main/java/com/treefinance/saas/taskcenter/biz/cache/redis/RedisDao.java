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

package com.treefinance.saas.taskcenter.biz.cache.redis;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public interface RedisDao {

    /**
     * 删除key
     *
     * @param key
     */
    void deleteKey(String key);


    /**
     * String操作:获取指定key的值
     *
     * @param key
     * @return
     */
    String get(String key);


    /**
     * String操作:设置指定key的值,并设置过期时间
     *
     * @param key
     * @param value
     * @param timeout
     * @param unit
     */
    Boolean setEx(String key, String value, long timeout, TimeUnit unit);


    /**
     * String操作:增加,负数则为自减,并设置过期时间
     *
     * @param key
     * @param increment
     * @param timeout
     * @param unit
     * @return
     */
    Long incrBy(String key, long increment, long timeout, TimeUnit unit);


    /**
     * List操作:存储值列表在list尾部
     *
     * @param key       键
     * @param valueList 值列表
     * @return
     */
    boolean saveListString(final String key, final List<String> valueList);


    /**
     * List操作:存储值在list尾部
     *
     * @param key   键
     * @param value 值
     * @return
     */
    boolean saveString2List(final String key, final String value);

    /**
     * List操作:移除并获取list最后一个元素
     *
     * @param key
     * @return
     */
    String getStringFromList(final String key);


    boolean pushMessage(String submitRedisKey, String messageType);

    boolean pushMessage(String submitRedisKey, String messageType, int ttlSeconds);

    String pullResult(String obtainRedisKey);

    /**
     * 分布式锁,获取锁
     *
     * @param lockKey 锁key
     * @param expired 锁的超时时间(毫秒),超时时间后自动释放锁,防止死锁
     * @return
     */
    Map<String, Object> acquireLock(String lockKey, long expired);

    /**
     * 分布式锁,释放锁
     *
     * @param lockKey 锁key
     * @param lockMap 比较值,判断所要释放的锁是否是当前锁
     * @param expired 锁设定的超时时间(毫秒)
     */
    void releaseLock(String lockKey, Map<String, Object> lockMap, long expired);


}

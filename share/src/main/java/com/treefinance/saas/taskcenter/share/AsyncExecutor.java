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

package com.treefinance.saas.taskcenter.share;

import com.alibaba.fastjson.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

/**
 * 异步执行器 Created by yh-treefinance on 2017/7/10.
 */
@Component
public class AsyncExecutor {
    /**
     * logger
     */
    protected final Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * 异步执行数据
     *
     * @param t
     * @param consumer
     * @param <T>
     */
    @Async
    public <T> void runAsync(T t, Consumer<T> consumer) {
        if (logger.isDebugEnabled()) {
            logger.debug("{} is running with data={} ", Thread.currentThread().getName(), JSON.toJSONString(t));
        }
        consumer.accept(t);
    }
}

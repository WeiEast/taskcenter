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

package com.treefinance.saas.taskcenter.biz.schedule;

import com.dangdang.ddframe.job.api.ShardingContext;
import com.dangdang.ddframe.job.api.simple.SimpleJob;
import com.google.common.base.Stopwatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Jerry
 * @since 00:18 05/08/2017
 */
public abstract class BaseSingleJob implements SimpleJob {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public final void execute(ShardingContext shardingContext) {
        if (shardingContext.getShardingItem() == 0) {
            final String simpleName = getClass().getSimpleName();
            logger.info("Starting schedule job: {}", simpleName);
            final Stopwatch stopwatch = Stopwatch.createStarted();
            try {
                this.process();
            } catch (Throwable e) {
                logger.error("Unexpected exception when running leader job!", e);
            } finally {
                logger.info("Completed schedule job: {}, cost: {}", simpleName, stopwatch);
            }
        }
    }

    /**
     * 任务处理
     */
    protected abstract void process();
}
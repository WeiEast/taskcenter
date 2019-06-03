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

package com.treefinance.saas.taskcenter.biz.mq;

import com.alibaba.fastjson.JSON;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nonnull;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * @author Jerry
 * @date 2019-05-16 10:45
 */
public abstract class AbstractJsonMessageListener<T> extends AbstractRocketMqMessageListener {
    private Class<T> entityClass;

    public AbstractJsonMessageListener() {
        Type type = ((ParameterizedType)getClass().getGenericSuperclass()).getActualTypeArguments()[0];
        this.entityClass = (Class<T>)type;
    }

    @Override
    protected final void handleMessage(@Nonnull String msgBody) {
        final String content = msgBody.trim();
        if (StringUtils.isEmpty(content)) {
            logger.warn("消息数据为空，跳过处理，message={}", msgBody);
            return;
        }

        T jsonObject = null;
        try {
            jsonObject = JSON.parseObject(content, entityClass);
        } catch (Exception e) {
            logger.error("非法数据，非json格式，无法解析. msg = {}", content, e);
        }
        if (jsonObject == null) {
            return;
        }

        processMessage(jsonObject);
    }

    protected abstract void processMessage(@Nonnull T message);
}

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

package com.treefinance.saas.taskcenter.common.util;

import com.google.common.collect.Lists;
import org.springframework.cglib.beans.BeanCopier;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author luoyihua
 * @date 2017/5/10
 */
public final class BeanUtils {

    private BeanUtils() {

    }

    private static final Map<String, BeanCopier> BEAN_COPIER_MAP = new ConcurrentHashMap<>();

    /**
     * 基于CGLIB的bean properties 的拷贝，性能要远优于{@code org.springframework.beans.BeanUtils.copyProperties}
     *
     * @param source
     * @param target
     */
    public static void copyProperties(Object source, Object target) {
        if (source == null || target == null) {
            return;
        }

        String key = String.format("%s:%s", source.getClass().getName(), target.getClass().getName());
        if (!BEAN_COPIER_MAP.containsKey(key)) {
            BeanCopier beanCopier = BeanCopier.create(source.getClass(), target.getClass(), false);
            BEAN_COPIER_MAP.putIfAbsent(key, beanCopier);
        }
        BeanCopier beanCopier = BEAN_COPIER_MAP.get(key);
        beanCopier.copy(source, target, null);
    }

    /**
     * 对象转化
     *
     * @param src
     * @param target
     * @param <T>
     * @return
     */
    public static <T> T convert(Object src, T target) {
        copyProperties(src, target);
        return target;
    }

    public static <S, T> List<T> convertList(List<S> request, Class<T> cls) {
        List<T> result = Lists.newArrayList();
        if (request == null) return result;
        for (S obj : request) {
            try {
                T target = cls.newInstance();
                result.add(convert(obj, target));
            } catch (Exception e) {
                throw new IllegalArgumentException("对象copy失败，请检查相关module", e);
            }
        }
        return result;
    }

}

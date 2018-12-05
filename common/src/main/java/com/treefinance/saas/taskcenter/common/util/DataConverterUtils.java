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
import org.apache.commons.collections.CollectionUtils;

import java.util.List;

/**
 * Created by luoyihua on 2017/5/10.
 */
public final class DataConverterUtils {

    private DataConverterUtils() {
    }

    /**
     *
     * @param sourceList
     * @return
     */
    public static <F, T> List<T> convert(List<F> sourceList, Class<T> targetClz) {
        if (CollectionUtils.isNotEmpty(sourceList)) {
            List<T> ret = Lists.newArrayListWithExpectedSize(sourceList.size());
            for (F source : sourceList) {
                ret.add(convert(source, targetClz));
            }
            return ret;
        }
        return Lists.newArrayList();
    }

    public static <F, T> T convert(F source, Class<T> targetClz) {
        try {
            T target = targetClz.newInstance();
            BeanUtils.copyProperties(source, target);
            return target;
        } catch (IllegalAccessException | InstantiationException | ExceptionInInitializerError
                | SecurityException e) {
            throw new RuntimeException(
                    "failed to create instance of " + targetClz.getName() + " - " + e.getMessage(), e);
        }
    }

}

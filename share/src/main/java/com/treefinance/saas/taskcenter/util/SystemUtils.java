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

package com.treefinance.saas.taskcenter.util;

import com.treefinance.saas.taskcenter.context.Constants;
import com.treefinance.toolkit.util.RegExp;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.LocalDateTime;

import java.util.Date;

/**
 * @author haojiahong
 * @date 2017/9/22.
 */
public final class SystemUtils {

    private static final String DEFAULT_DATETIME_PATTERN = "yyyy-MM-dd HH:mm:ss";

    private SystemUtils() {}

    /**
     * 获取当前时间的字符串(格式为yyyy-MM-dd HH:mm:ss)
     *
     * @return the string of now time that formatted with the default {@link #DEFAULT_DATETIME_PATTERN}
     */
    public static String nowDateTimeStr() {
        return LocalDateTime.now().toString(DEFAULT_DATETIME_PATTERN);
    }

    /**
     * 获取当前时间的字符串
     *
     * @return the now time
     */
    public static Date now() {
        return new Date();
    }

    /**
     * 字符串正则匹配
     *
     * @param dest
     * @param regex
     * @return
     */
    public static boolean regexMatch(String dest, String regex) {
        if (StringUtils.isEmpty(dest) || StringUtils.isEmpty(regex)) {
            return false;
        }

        return RegExp.matches(dest, regex);
    }

    public static boolean isDataNotifyModel(Byte notifyModel) {
        return Constants.NOTIFY_MODEL_1.equals(notifyModel);
    }

    public static boolean isTrue(Byte value) {
        return Constants.YES.equals(value);
    }

    public static boolean isNotTrue(Byte value) {
        return !isTrue(value);
    }
}

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

package com.treefinance.saas.taskcenter.util;

import com.treefinance.toolkit.util.http.client.MoreHttp;
import com.treefinance.toolkit.util.http.client.MoreHttpFactory;
import com.treefinance.toolkit.util.http.exception.HttpException;
import org.apache.commons.lang3.StringUtils;

/**
 * 数据下载器
 */
public final class RemoteDataUtils {

    private static final MoreHttp CLIENT = MoreHttpFactory.createCustom();

    private RemoteDataUtils() {
    }

    public static <T> T download(String url, Class<T> clazz) throws HttpException {
        return CLIENT.get(url, StringUtils.EMPTY, clazz);
    }
}

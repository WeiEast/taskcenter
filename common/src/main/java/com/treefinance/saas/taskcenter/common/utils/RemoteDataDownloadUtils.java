package com.treefinance.saas.taskcenter.common.utils;

import com.treefinance.toolkit.util.http.client.MoreHttp;
import com.treefinance.toolkit.util.http.client.MoreHttpFactory;
import com.treefinance.toolkit.util.http.exception.HttpException;
import org.apache.commons.lang3.StringUtils;

/**
 * 数据下载器
 */
public final class RemoteDataDownloadUtils {

    private static final MoreHttp CLIENT = MoreHttpFactory.createCustom();

    private RemoteDataDownloadUtils() {
    }

    public static <T> T download(String url, Class<T> clazz) throws HttpException {
        return CLIENT.get(url, StringUtils.EMPTY, clazz);
    }
}

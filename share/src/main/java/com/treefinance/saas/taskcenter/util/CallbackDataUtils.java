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

import com.alibaba.fastjson.TypeReference;
import com.treefinance.b2b.saas.util.DataUtils;
import com.treefinance.saas.taskcenter.exception.CallbackEncryptException;
import com.treefinance.toolkit.util.crypto.exception.CryptoException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * Created by luoyihua on 2017/5/10.
 */
public final class CallbackDataUtils {
    private static final Logger logger = LoggerFactory.getLogger(CallbackDataUtils.class);

    private CallbackDataUtils() {}

    /**
     * RSA 加密
     *
     * @param data
     * @param publicKey
     * @return
     * @throws CallbackEncryptException
     */
    public static String encryptByRSA(Object data, String publicKey) throws CallbackEncryptException {
        if (data == null) {
            return null;
        }

        try {
            String encryptedData = DataUtils.encryptBeanAsBase64StringByRsa(data, publicKey);
            logger.debug("Finish encrypting callback for encryptedData '{}'.", encryptedData);
            return encryptedData;
        } catch (CryptoException e) {
            throw new CallbackEncryptException("Error encrypting callback data", e);
        }
    }

    /**
     * AES 加密( 先AES，后Base64)
     *
     * @param data
     * @param dataKey
     * @return
     */
    public static String encryptByAES(Object data, String dataKey) throws CallbackEncryptException {
        try {
            return DataUtils.encryptBeanAsBase64StringByAes(data, dataKey);
        } catch (Exception e) {
            throw new CallbackEncryptException("encryptByAES exception", e);
        }
    }

    public static String decryptByAES(byte[] data, String dataKey) throws CallbackEncryptException {
        try {
            return DataUtils.decryptAsStringByAes(data, dataKey);
        } catch (Exception e) {
            throw new CallbackEncryptException("decryptByAES exception", e);
        }
    }

    public static Map<String, Object> decryptAsMapByAES(byte[] data, String dataKey) throws CallbackEncryptException {
        try {
            return DataUtils.decryptAsMapByAes(data, dataKey);
        } catch (Exception e) {
            throw new CallbackEncryptException("decryptByAES exception", e);
        }
    }

    /**
     * AES 解密
     *
     * @param data
     * @param dataKey
     * @return
     */
    public static <T> T decryptByAES(byte[] data, String dataKey, Class<T> clazz) throws CallbackEncryptException {
        try {
            return DataUtils.decryptAsBeanByAes(data, dataKey, clazz);
        } catch (Exception e) {
            throw new CallbackEncryptException("decryptByAES exception", e);
        }
    }

    public static <T> T decryptByAES(byte[] data, String dataKey, TypeReference<T> clazz) throws CallbackEncryptException {
        try {
            return DataUtils.decryptAsBeanByAes(data, dataKey, clazz);
        } catch (Exception e) {
            throw new CallbackEncryptException("decryptByAES exception", e);
        }
    }
}

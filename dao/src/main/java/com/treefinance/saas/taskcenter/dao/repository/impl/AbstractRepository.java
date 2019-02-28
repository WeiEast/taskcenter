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

package com.treefinance.saas.taskcenter.dao.repository.impl;

import com.treefinance.basicservice.security.crypto.facade.EncryptionIntensityEnum;
import com.treefinance.basicservice.security.crypto.facade.ISecurityCryptoService;
import com.treefinance.commonservice.uid.UidService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author Jerry
 * @date 2019-01-10 17:17
 */
public abstract class AbstractRepository {

    private static final String ENCRYPTED_SIGN = "$";
    @Autowired
    private UidService uidService;
    @Autowired
    private ISecurityCryptoService securityCryptoService;

    protected long generateUniqueId() {
        return uidService.getId();
    }

    protected String encryptNormal(String text) {
        if (StringUtils.isNotEmpty(text)) {
            return securityCryptoService.encrypt(text, EncryptionIntensityEnum.NORMAL);
        }
        return text;
    }

    protected String encryptNormal(String text, boolean encrypt) {
        if (encrypt) {
            return encryptNormal(text);
        }

        return text;
    }

    protected String decryptNormal(String text) {
        if (StringUtils.isNotEmpty(text) && text.contains(ENCRYPTED_SIGN)) {
            return securityCryptoService.decrypt(text, EncryptionIntensityEnum.NORMAL);
        }

        return text;
    }

    protected String decryptNormal(String text, boolean decrypt) {
        if (decrypt) {
            return decryptNormal(text);
        }

        return text;
    }

}

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

package com.treefinance.saas.taskcenter.interation.manager.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

/**
 * @author Jerry
 * @date 2018/12/19 13:54
 */
@Getter
@Setter
@ToString
public class AppLicense implements Serializable {
    public static final AppLicense NULL = new AppLicense();
    /**
     * appId
     */
    private String appId;

    /**
     * sdk 公钥
     */
    private String sdkPublicKey;
    /**
     * sdk 私钥
     */
    private String sdkPrivateKey;

    /**
     * server公钥
     */
    private String serverPublicKey;

    /**
     * server私钥
     */
    private String serverPrivateKey;

    /**
     * 数据密钥
     */
    private String dataSecretKey;
}
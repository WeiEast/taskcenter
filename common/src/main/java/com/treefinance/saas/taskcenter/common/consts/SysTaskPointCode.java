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

package com.treefinance.saas.taskcenter.common.consts;

/**
 * 系统层面的埋点code
 *
 * @author Jerry
 * @date 2019-02-28 17:45
 */
public final class SysTaskPointCode {

    /**
     * 登录开始
     */
    public static final String LOGIN_START = "900101";
    /**
     * 登录阶段：短信验证码已发送
     */
    public static final String LOGIN_SMS_START= "900102";
    /**
     * 登录阶段：收到短信验证码
     */
    public static final String LOGIN_SMS_RECEIVED = "900103";
    /**
     * 登录阶段：短信验证码认证成功
     */
    public static final String LOGIN_SMS_CERT_SUCCESS = "900104";
    /**
     * 登录成功
     */
    public static final String LOGIN_SUCCESS = "900190";
    /**
     * 准备爬取
     */
    public static final String CRAWL_PRE = "900200";
    /**
     * 开始爬取
     */
    public static final String CRAWL_START = "900201";
    /**
     * 爬取阶段：短信验证码已发送
     */
    public static final String CRAWL_SMS_START = "900202";
    /**
     * 爬取阶段：收到短信验证码
     */
    public static final String CRAWL_SMS_RECEIVED = "900203";
    /**
     * 爬取阶段：短信验证码认证成功
     */
    public static final String CRAWL_SMS_CERT_SUCCESS = "900204";
    /**
     * 爬取成功
     */
    public static final String CRAWL_SUCCESS = "900290";
    /**
     * 数据处理开始
     */
    public static final String PROCESS_DATA_START = "900301";
    /**
     * 数据处理成功并入库
     */
    public static final String PROCESS_DATA_SUCCESS = "900302";
    /**
     * 生成回调数据的快照
     */
    public static final String PROCESS_DATA_SNAPSHOT = "900303";
    /**
     * 开始回调
     */
    public static final String CALLBACK_START = "900401";
    /**
     * 回调成功
     */
    public static final String CALLBACK_SUCCESS = "900402";
}

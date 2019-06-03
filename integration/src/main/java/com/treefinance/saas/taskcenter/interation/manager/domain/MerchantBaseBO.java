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
 * @date 2018/12/11 01:04
 */
@Getter
@Setter
@ToString
public class MerchantBaseBO implements Serializable {

    /** 商户编号 */
    private String appId;
    /** app名称 */
    private String appName;
    /** 公司简称 */
    private String chName;
    /** 公司 */
    private String company;
    /** 业务 */
    private String bussiness;
    /** 业务经营类型2 */
    private String bussiness2;
    /** 商户地址 */
    private String address;
    /** 联系人 */
    private String contactPerson;
    /** 联系号码 */
    private String contactValue;

    private Byte isActive;

}

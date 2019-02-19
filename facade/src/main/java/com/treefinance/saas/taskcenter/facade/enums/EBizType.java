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

package com.treefinance.saas.taskcenter.facade.enums;

import org.apache.commons.lang3.StringUtils;

import java.util.Objects;
import java.util.stream.Stream;

/**
 * @author Jerry
 * @date 2017/4/27.
 */
public enum EBizType {
    /**
     * 邮箱
     */
    EMAIL("EMAIL", (byte) 1),
    /**
     * 邮箱h5
     */
    EMAIL_H5("EMAIL_H5", (byte) 1),
    /**
     * 电商
     */
    ECOMMERCE("ECOMMERCE", (byte) 2),
    /**
     * 运营商
     */
    OPERATOR("OPERATOR", (byte) 3),
    /**
     * 公积金
     */
    FUND("FUND", (byte) 4),
    /**
     * 学历
     */
    DIPLOMA("DIPLOMA", (byte) 7),
    /**
     * 汽车信息
     */
    CAR_INFO("CAR_INFO", (byte)9),
    /**
     * 同盾数据（傲彝）
     */
    TONGDUN("TONGDUN", (byte)10),
    /**
     * 同盾数据（随手）
     */
    TONGDUN_KANIU("TONGDUN_KANIU", (byte)11),
    /**
     * 同盾数据（铁树信用）
     */
    TONGDUN_TIESHU("TONGDUN_TIESHU", (byte)12),
    /**
     * 企业
     */
    ENTERPRISE("ENTERPRISE", (byte)13),
    /**
     * 网查
     */
    OPINION_DETECT("OPINION_DETECT", (byte)14);

    private String text;
    private Byte code;

    EBizType(String text, Byte code) {
        this.text = text;
        this.code = code;
    }

    public Byte getCode() {
        return code;
    }

    public String getText() {
        return text;
    }

    public static Byte getCode(String text) {
        if (StringUtils.isEmpty(text)) {
            return (byte)0;
        }

        return Stream.of(EBizType.values()).filter(item -> item.getText().equalsIgnoreCase(text)).findFirst().map(EBizType::getCode).orElse((byte)0);
    }

    public static String getName(Byte code) {
        if (Objects.nonNull(code)) {
            return Stream.of(EBizType.values()).filter(item -> item.getCode().equals(code)).findFirst().map(EBizType::getText).orElse(null);
        }
        return null;
    }

    public static EBizType from(String name) {
        try {
            return EBizType.valueOf(name);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("The task type '" + name + "' is unsupported.", e);
        }
    }

    public static EBizType of(String name) {
        if (StringUtils.isEmpty(name)) {
            return null;
        }

        return Stream.of(EBizType.values()).filter(item -> item.getText().equalsIgnoreCase(name)).findFirst().orElse(null);
    }

    public static EBizType of(Byte bizType) {
        if (Objects.nonNull(bizType)) {
            return Stream.of(EBizType.values()).filter(item -> item.getCode().equals(bizType)).findFirst().orElse(null);
        }
        return null;
    }

}

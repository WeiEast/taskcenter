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

package com.treefinance.saas.taskcenter.common.enums;

import org.apache.commons.lang3.StringUtils;

/**
 * @author luoyihua
 * @date 2017/5/11.
 */
public enum EDirective {
    /**
     * 等待中(过渡指令)
     */
    WAITING("waiting", "请等待"),
    /**
     * 登录成功（目前主要用于魔蝎）
     */
    LOGIN_SUCCESS("login_success", "登录成功"),
    /**
     * 登录失败（目前主要用于魔蝎）
     */
    LOGIN_FAIL("login_fail", "登录失败"),
    /**
     * 短信验证码
     */
    REQUIRE_SMS("require_sms", "请输入短信验证码"),
    /**
     * 图片验证码
     */
    REQUIRE_PICTURE("require_picture", "请输入图片验证码"),
    /**
     * 二维码
     */
    REQUIRE_QR("require_qr", "请扫描二维码"),
    /**
     *
     */
    GRAB_URL("grab_url", ""),
    /**
     * 回调失败
     */
    CALLBACK_FAIL("callback_fail", "回调失败"),
    /**
     * 回调成功
     */
    TASK_SUCCESS("task_success", "任务成功"),
    /**
     * 任务失败
     */
    TASK_FAIL("task_fail", "任务失败"),
    /**
     * 任务成功
     */
    TASK_CANCEL("task_cancel", "任务取消"),
    /**
     * 临时成功状态，成功指令处理但回调失败的处理阶段记录成临时成功的过渡状态，即：task_success -> temporary_success -> callback_fail
     */
    TEMPORARY_SUCCESS("temporary_success", "临时成功"),
    /**
     * 需要二次密码
     */
    REQUIRE_SECOND_PASSWORD("require_second_password", "需要二次密码"),
    /**
     * 校验成功
     */
    VALIDATE_SUCCESS("validate_success", "校验成功"),
    /**
     * 校验失败
     */
    VALIDATE_FAIL("validate_fail", "校验失败"),
    /**
     * 校验结束
     */
    VALIDATE_OVER("validate_over", "校验结束");


    private final String value;
    private final String desc;

    EDirective(String value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    /**
     * @see #value()
     */
    @Deprecated
    public String getText() {
        return value();
    }

    public String value() {
        return value;
    }

    public String getDesc() {
        return desc;
    }

    public static EDirective directiveOf(String text) {
        if (StringUtils.isNotEmpty(text)) {
            for (EDirective item : EDirective.values()) {
                if (text.equalsIgnoreCase(item.value())) {
                    return item;
                }
            }
        }
        return null;
    }

    public static boolean isTaskSuccess(String directive) {
        return EDirective.TASK_SUCCESS.value().equals(directive);
    }

    public static boolean isTaskFailure(String directive) {
        return EDirective.TASK_FAIL.value().equals(directive);
    }

    public static boolean isTaskCancel(String directive) {
        return EDirective.TASK_CANCEL.value().equals(directive);
    }

    public static boolean isTaskSuccess(EDirective directive) {
        return EDirective.TASK_SUCCESS.equals(directive);
    }

    public static boolean isTaskFailure(EDirective directive) {
        return EDirective.TASK_FAIL.equals(directive);
    }

    public static boolean isTaskCancel(EDirective directive) {
        return EDirective.TASK_CANCEL.equals(directive);
    }

}

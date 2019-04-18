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
     * 短信验证码
     */
    REQUIRE_SMS("require_sms"),
    /**
     * 图片验证码
     */
    REQUIRE_PICTURE("require_picture"),
    /**
     * 二维码
     */
    REQUIRE_QR("require_qr"),
    /**
     *
     */
    GRAB_URL("grab_url"),
    /**
     * 回调失败
     */
    CALLBACK_FAIL("callback_fail"),
    /**
     * 回调成功
     */
    TASK_SUCCESS("task_success"),
    /**
     * 任务失败
     */
    TASK_FAIL("task_fail"),
    /**
     * 任务成功
     */
    TASK_CANCEL("task_cancel");

    private final String text;

    EDirective(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }


    public static EDirective directiveOf(String text) {
        if (StringUtils.isNotEmpty(text)) {
            for (EDirective item : EDirective.values()) {
                if (text.equalsIgnoreCase(item.getText())) {
                    return item;
                }
            }
        }
        return null;
    }

    public static boolean isTaskSuccess(String directive) {
        return EDirective.TASK_SUCCESS.getText().equals(directive);
    }

    public static boolean isTaskFailure(String directive) {
        return EDirective.TASK_FAIL.getText().equals(directive);
    }

    public static boolean isTaskCancel(String directive) {
        return EDirective.TASK_CANCEL.getText().equals(directive);
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

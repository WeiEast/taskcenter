package com.treefinance.saas.taskcenter.context.enums.moxie;

import org.apache.commons.lang3.StringUtils;

/**
 * @author haojiahong
 * @date 2017/9/15.
 */
public enum EMoxieDirective {

    LOGIN_SUCCESS("login_success", (byte)1),
    LOGIN_FAIL("login_fail", (byte)1),
    TASK_SUCCESS("task_success", (byte)2),
    TASK_FAIL("task_fail", (byte)2),
    TASK_CANCEL("task_cancel", (byte)2),
    CALLBACK_FAIL("callback_fail", (byte)2);

    private final String text;
    private final Byte stepCode;

    EMoxieDirective(String text, Byte stepCode) {
        this.text = text;
        this.stepCode = stepCode;
    }

    public static EMoxieDirective directiveOf(String text) {
        if (StringUtils.isNotEmpty(text)) {
            for (EMoxieDirective item : EMoxieDirective.values()) {
                if (text.equalsIgnoreCase(item.getText())) {
                    return item;
                }
            }
        }
        return null;
    }

    public String getText() {
        return text;
    }

    public Byte getStepCode() {
        return stepCode;
    }

}

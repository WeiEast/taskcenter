package com.treefinance.saas.taskcenter.context.enums;

import org.apache.commons.lang3.StringUtils;

/**
 * @author luoyihua
 * @date 2017/5/11.
 */
public enum EDirective {
    /**
     * 短信验证码
     */
    REQUIRE_SMS("require_sms", (byte)1),
    /**
     * 图片验证码
     */
    REQUIRE_PICTURE("require_picture", (byte)2),
    /**
     * 二维码
     */
    REQUIRE_QR("require_qr", (byte)3),
    /**
     *
     */
    GRAB_URL("grab_url", (byte)3),
    /**
     * 回调失败
     */
    CALLBACK_FAIL("callback_fail", (byte)3),
    /**
     * 回调成功
     */
    TASK_SUCCESS("task_success", (byte)3),
    /**
     * 任务失败
     */
    TASK_FAIL("task_fail", (byte)3),
    /**
     * 任务成功
     */
    TASK_CANCEL("task_cancel", (byte)3);

    private Byte code;
    private String text;

    EDirective(String text, Byte code) {
        this.code = code;
        this.text = text;
    }

    public Byte getCode() {
        return code;
    }

    public void setCode(Byte code) {
        this.code = code;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public static Byte getCode(String text) {
        if (StringUtils.isNotEmpty(text)) {
            for (EDirective item : EDirective.values()) {
                if (text.equals(item.getText())) {
                    return item.getCode();
                }
            }
        }
        return -1;
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
}

package com.treefinance.saas.taskcenter.common.enums;

import java.util.Objects;

/**
 * Created by luoyihua on 2017/4/27.
 */
public enum EBizType {
    EMAIL("EMAIL", (byte) 1),
    EMAIL_H5("EMAIL_H5", (byte) 1),
    ECOMMERCE("ECOMMERCE", (byte) 2),
    OPERATOR("OPERATOR", (byte) 3),
    FUND("FUND", (byte) 4),
    DIPLOMA("DIPLOMA", (byte) 7),
    CAR_INFO("CAR_INFO", (byte) 9),;

    private String text;
    private Byte code;

    EBizType(String text, Byte code) {
        this.text = text;
        this.code = code;
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
        if (Objects.nonNull(text)) {
            for (EBizType item : EBizType.values()) {
                if (text.equals(item.getText())) {
                    return item.getCode();
                }
            }
        }
        return -1;
    }

    public static String getName(Byte code) {
        if (Objects.nonNull(code)) {
            for (EBizType item : EBizType.values()) {
                if (code.equals(item.getCode())) {
                    return item.getText();
                }
            }
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
        for (EBizType item : EBizType.values()) {
            if (item.text.equalsIgnoreCase(name)) {
                return item;
            }
        }
        return null;
    }

    public static EBizType of(Byte bizType) {
        for (EBizType item : EBizType.values()) {
            if (item.code.equals(bizType)) {
                return item;
            }
        }
        return null;
    }

}

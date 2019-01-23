package com.treefinance.saas.taskcenter.common.enums;

import java.util.Objects;

/**
 * @author 张琰佳
 * @since 7:37 PM 2019/1/23
 */
public enum CodeStepEnum {
    ;
    private String code;

    private String step;
    private String subStep;

    private String msg;

    CodeStepEnum(String code, String step,String subStep,String msg) {
        this.code = code;
        this.step = step;
        this.subStep=subStep;
        this.msg=msg;
    }
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getStep() {
        return step;
    }

    public void setStep(String step) {
        this.step = step;
    }

    public String getSubStep() {
        return subStep;
    }

    public void setSubStep(String subStep) {
        this.subStep = subStep;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public static String getStep(String key) {
        if (Objects.nonNull(key)) {
            for (CodeStepEnum item : CodeStepEnum.values()) {
                if (key.equals(item.getCode())) {
                    return item.getStep();
                }
            }
        }
        return null;
    }

    public static String getSubStep(String key) {
        if (Objects.nonNull(key)) {
            for (CodeStepEnum item : CodeStepEnum.values()) {
                if (key.equals(item.getCode())) {
                    return item.getSubStep();
                }
            }
        }
        return null;
    }

    public static String getMsg(String key) {
        if (Objects.nonNull(key)) {
            for (CodeStepEnum item : CodeStepEnum.values()) {
                if (key.equals(item.getCode())) {
                    return item.getMsg();
                }
            }
        }
        return null;
    }
}

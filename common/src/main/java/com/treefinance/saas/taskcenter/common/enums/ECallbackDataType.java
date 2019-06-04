package com.treefinance.saas.taskcenter.common.enums;

import java.util.Objects;
import java.util.stream.Stream;

/**
 * @author guimeichao
 * @date 2019/6/4
 */
public enum ECallbackDataType {
    /**
     * 邮箱
     */
    BANKBILL("bankbill", (byte)1),
    /**
     * 电商
     */
    ECOMMERCE("ecommerce", (byte)2),
    /**
     * 运营商
     */
    OPERATOR("operator", (byte)3),
    /**
     * 公积金
     */
    FUND("fund", (byte)4),
    /**
     * 学历
     */
    DIPLOMA("education", (byte)7),
    /**
     * 汽车信息
     */
    CAR_INFO("cardInfo", (byte)9),
    /**
     * 同盾数据（傲彝）
     */
    TONGDUN("tongdun", (byte)10),
    /**
     * 同盾数据（随手）
     */
    TONGDUN_KANIU("tongdun", (byte)11),
    /**
     * 同盾数据（铁树信用）
     */
    TONGDUN_TIESHU("tongdun", (byte)12),
    /**
     * 企业
     */
    ENTERPRISE("enterprise", (byte)13),
    /**
     * 网查
     */
    OPINION_DETECT("opiniondetect", (byte)14),
    /**
     * 网信账单
     */
    BILL_WANGXIN_CLEAN("billWangxin", (byte)15);

    private String text;
    private Byte code;

    ECallbackDataType(String text, Byte code) {
        this.text = text;
        this.code = code;
    }

    public static ECallbackDataType of(Byte bizType) {
        if (Objects.nonNull(bizType)) {
            return Stream.of(ECallbackDataType.values()).filter(item -> item.getCode().equals(bizType)).findFirst().orElse(null);
        }
        return null;
    }

    public Byte getCode() {
        return code;
    }

    public String getText() {
        return text;
    }
}

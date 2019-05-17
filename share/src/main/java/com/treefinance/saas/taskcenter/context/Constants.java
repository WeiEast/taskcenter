package com.treefinance.saas.taskcenter.context;

import com.treefinance.b2b.saas.context.conf.PropertiesConfiguration;

/**
 * @author Jerry
 */
public final class Constants {

    public static final int REDIS_KEY_TIMEOUT = PropertiesConfiguration.getInstance().getInt("platform.redisKey.timeout", 600);

    /**
     * 错误信息字段名
     */
    public static final String ERROR_MSG_NAME = "errorMsg";

    /**
     * 运营商错误提示信息
     */
    public static final String OPERATOR_TASK_FAIL_MSG = "运营商导入失败，请稍后再试。";
    /**
     * 学信网错误提示
     */
    public static final String DIPLOMA_TASK_FAIL_MSG = "学历信息导入失败，请稍后再试。";
    /**
     * 通知方式0：返回数据url
     */
    public static final Byte NOTIFY_MODEL_0 = Byte.valueOf("0");
    /**
     * 通知方式1：返回完整的数据
     */
    public static final Byte NOTIFY_MODEL_1 = Byte.valueOf("1");
    /**
     * 是
     */
    public static final Byte YES = Byte.valueOf("1");
    /**
     * 否
     */
    public static final Byte NO = Byte.valueOf("0");

    public static final Byte DATA_TYPE_0 = Byte.valueOf("0");

    /**
     * 回调类型：后端回调
     */
    public static final Byte CALLBACK_TYPE_BACKEND = Byte.valueOf("1");

    /**
     * 回调类型：前端回调
     */
    public static final Byte CALLBACK_TYPE_FRONTEND = Byte.valueOf("2");

    private Constants() {}
}
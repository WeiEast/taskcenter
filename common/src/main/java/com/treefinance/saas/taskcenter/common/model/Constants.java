package com.treefinance.saas.taskcenter.common.model;

import com.datatrees.common.conf.PropertiesConfiguration;

public interface Constants {

    int REDIS_KEY_TIMEOUT = PropertiesConfiguration.getInstance().getInt("platform.redisKey.timeout", 600);

    /**
     * 错误信息字段名
     */
    String ERROR_MSG_NAME = "errorMsg";

    /**
     * 运营商错误提示信息
     */
    String OPERATOR_TASK_FAIL_MSG = "运营商导入失败，请稍后再试。";
    /**
     * 学信网错误提示
     */
    String DIPLOMA_TASK_FAIL_MSG = "学历信息导入失败，请稍后再试。";

}
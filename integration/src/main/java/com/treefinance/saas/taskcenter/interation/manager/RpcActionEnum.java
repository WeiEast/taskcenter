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

package com.treefinance.saas.taskcenter.interation.manager;

/**
 * RPC action list.
 *
 * @author Jerry
 * @date 2018/11/29 17:02
 */
public enum RpcActionEnum {
    /**
     * 查询app的 license信息
     */
    QUERY_APP_LICENSE,
    /**
     * 查询回调用的license信息
     */
    QUERY_CALLBACK_LICENSE,
    /**
     * 查询全部app licenses
     */
    QUERY_APP_LICENSES,
    /**
     * 根据bizType查询app licenses
     */
    QUERY_APP_LICENSES_BY_BIZ_TYPE,
    /**
     * 根据appId查询可用app licenses
     */
    QUERY_VALID_APP_LICENSES_BY_APP_ID,
    /**
     * 查询全部biz-type
     */
    QUERY_BIZ_TYPE_LIST,
    /**
     * 查询全部biz-type，发挥值包含主键ID
     */
    QUERY_BIZ_TYPE_LIST_SIMPLE,
    /**
     * 根据指定的biz_type值查询列表
     */
    QUERY_BIZ_TYPE_LIST_ASSIGNED,
    /**
     * 根据指定的taskId列表查询任务日志
     */
    QUERY_TASK_LOG_ASSIGNED_TASK_IDS,

    /**
     * 查询商户每天的访问统计记录的集合
     */
    STATISTICS_MERCHANT_DAILY_ACCESS_RESULT_SET,
    /**
     * 查询商户每天的访问统计记录
     */
    STATISTICS_MERCHANT_DAILY_ACCESS_RECORDS,
    /**
     * 查询商户的访问统计记录
     */
    STATISTICS_MERCHANT_ACCESS_RECORDS,
    /**
     * 查询商户的成功访问统计记录
     */
    STATISTICS_MERCHANT_ACCESS_SUCCESS_RECORDS,
    /**
     * 查询saas每天的ErrorStep统计记录
     */
    STATISTICS_ERROR_STEP_DAILY_RECORDS,

}

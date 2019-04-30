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

package com.treefinance.saas.taskcenter.service.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;

/**
 * @author Jerry
 * @date 2019-03-05 02:09
 */
@Getter
@Setter
@ToString
public class TaskInfo implements Serializable {

    /**
     * 任务ID
     */
    private Long id;
    /**
     * 商户ID
     */
    private String appId;
    /**
     * 用户唯一标识
     */
    private String uniqueId;
    /**
     * 账号
     */
    private String accountNo;
    /**
     * 站点标识
     */
    private String webSite;
    /**
     * 业务类型
     */
    private Byte bizType;
    /**
     * 状态
     */
    private Byte status;
    /**
     * 步骤编码
     */
    private String stepCode;
    /**
     * 环境标识
     */
    private Byte saasEnv;
    /**
     * 任务创建时间
     */
    private Date createTime;
    /**
     * 最近更新时间
     */
    private Date lastUpdateTime;

}

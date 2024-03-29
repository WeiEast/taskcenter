/*
 * Copyright © 2015 - 2017 杭州大树网络技术有限公司. All Rights Reserved
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package com.treefinance.saas.taskcenter.dao.param;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @author Jerry
 * @date 2018/11/21 19:42
 */
@Getter
@Setter
public class TaskQuery implements Serializable {

    @Deprecated
    private Long id;
    /**
     * 商户ID列表
     */
    private List<String> appIds;
    /**
     * 业务类型
     */
    private List<Byte> bizTypes;
    /**
     * 网站标识
     */
    private String website;
    /**
     * 用户ID
     */
    private String uniqueId;
    /**
     * 账号
     */
    private String accountNo;
    /**
     * 步骤
     */
    private String stepCode;
    /**
     * 环境标识
     */
    private Byte saasEnv;
    /**
     * 状态
     */
    private Byte status;
    /**
     * 起始时间
     */
    private Date startDate;
    /**
     * 结束时间
     */
    private Date endDate;
    /**
     * 排序方式
     */
    private String order;

}

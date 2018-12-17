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

package com.treefinance.saas.taskcenter.facade.request;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Jerry
 * @date 2018/12/16 22:50
 */
@Getter
@Setter
@ToString
public class CompositeTaskAttrPagingQueryRequest extends BasePagingQueryRequest {

    /**
     * 商户ID
     */
    private String appId;
    /**
     * 业务类型 注意：hessian反序列化过程，Byte,Short类型利用序列化整型处理。
     */
    private List<Byte> bizTypes;
    /**
     * 网站标识
     */
    private String website;
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
     * 属性名
     */
    private String attrName;
    /**
     * 属性值
     */
    private String attrValue;
    /**
     * 排序方式
     */
    private String order;

    public void addBizType(Byte bizType) {
        if (bizTypes == null) {
            bizTypes = new ArrayList<>();
        }
        bizTypes.add(bizType);
    }
}

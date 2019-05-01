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

package com.treefinance.saas.taskcenter.facade.result;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;

/**
 *  纯task_attribute表数据
 * @author Jerry
 * @date 2019-05-01 22:48
 */
@Getter
@Setter
@ToString
public class SimpleAttributeDTO implements Serializable {

    /**
     * 主键ID
     */
    private Long id;
    /**
     * 任务ID
     */
    private Long taskId;
    /**
     * 属性名
     */
    private String name;
    /**
     * 属性值
     */
    private String value;
    /**
     * 创建时间
     */
    private Date createTime;
    /**
     * 更新时间
     */
    private Date lastUpdateTime;
}

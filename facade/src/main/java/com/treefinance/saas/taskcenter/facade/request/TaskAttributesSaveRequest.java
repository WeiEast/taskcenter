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

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Jerry
 * @date 2019-05-03 23:17
 */
@Getter
@Setter
@ToString
public class TaskAttributesSaveRequest implements Serializable {

    /**
     * 任务ID
     */
    private Long taskId;
    /**
     * 保存的属性列表
     */
    private List<SavedTaskAttribute> attributes;

    public void addAttribute(String name, String value, boolean sensitive) {
        if (attributes == null) {
            attributes = new LinkedList<>();
        }
        attributes.add(new SavedTaskAttribute(name, value, sensitive));
    }

    public void addAttribute(String name, String value) {
        addAttribute(name, value, false);
    }

    public boolean isEmpty() {
        return attributes == null || attributes.isEmpty();
    }
}

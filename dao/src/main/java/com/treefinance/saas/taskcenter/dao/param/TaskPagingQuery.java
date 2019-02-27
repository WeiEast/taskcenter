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
import lombok.ToString;

/**
 * @author Jerry
 * @date 2018/11/21 19:42
 */
@Getter
@Setter
@ToString(callSuper = true)
public class TaskPagingQuery extends TaskQuery {

    private int offset = -1;
    private int limit = -1;

    public TaskPagingQuery() {}

    public TaskPagingQuery(int offset, int limit) {
        this.offset = offset;
        this.limit = limit;
    }
}

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

import lombok.Setter;
import lombok.ToString;

/**
 * @author Jerry
 * @date 2018/12/14 16:16
 */
@Setter
@ToString(callSuper = true)
public class TaskPagingQueryRequest extends TaskQueryRequest {

    /**
     * 第几页
     */
    private Integer pageNum;
    /**
     * 每页记录数
     */
    private Integer pageSize;
    /**
     * 偏移量
     */
    private Integer offset;

    public Integer getPageNum() {
        if (pageNum == null || pageNum < 0) {
            pageNum = 1;
        }
        return pageNum;
    }

    public Integer getPageSize() {
        if (pageSize == null || pageSize <= 0) {
            pageSize = 20;
        }
        return pageSize;
    }

    public Integer getOffset() {
        if (offset == null) {
            offset = (getPageNum() - 1) * pageSize;
        }
        return offset;
    }
}

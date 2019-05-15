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

package com.treefinance.saas.taskcenter.biz.service.directive.process;

import com.alibaba.fastjson.JSON;
import com.treefinance.saas.taskcenter.context.enums.EGrabStatus;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nonnull;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Jerry
 * @date 2019-04-23 15:55
 */
public class CallbackEntity extends HashMap<String, Object> {

    public CallbackEntity() {}

    public CallbackEntity(Map<? extends String, ?> m) {
        super(m);
    }

    public void setTaskIdIfAbsent(Long taskId) {
        this.putIfAbsent("taskId", taskId);
    }

    public void setUniqueIdIfAbsent(String uniqueId) {
        this.putIfAbsent("uniqueId", uniqueId);
    }

    public void setData(Object data) {
        this.put("data", data);
    }

    private void setStatus(String status, String msg) {
        this.put("taskStatus", status);
        this.put("taskErrorMsg", msg);
    }

    public void setStatus(@Nonnull EGrabStatus status, String msg) {
        this.setStatus(status.getCode(), msg);
    }

    public void setStatus(@Nonnull EGrabStatus status) {
        this.setStatus(status.getCode(), status.getName());
    }

    public void success() {
        setStatus(EGrabStatus.SUCCESS, StringUtils.EMPTY);
    }

    public void failure(String msg) {
        setStatus(EGrabStatus.FAIL, msg);
    }

    public void cancel(String msg) {
        setStatus(EGrabStatus.CANCEL, msg);
    }

    public void emptyData() {
        this.setStatus(EGrabStatus.RESULT_EMPTY);
        this.setData(StringUtils.EMPTY);
    }

    /**
     * 工商页面数据是否不爬取，1表示不爬取，其他表示爬取
     */
    public boolean getCrawlerStatus() {
        final Object crawlerStatus = this.get("crawlerStatus");
        return Byte.valueOf("1").equals(crawlerStatus);
    }

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }
}

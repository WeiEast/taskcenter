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

package com.treefinance.saas.taskcenter.context.config;

import com.github.diamond.client.extend.annotation.AfterUpdate;
import com.github.diamond.client.extend.annotation.BeforeUpdate;
import com.github.diamond.client.extend.annotation.DAttribute;
import com.github.diamond.client.extend.annotation.DResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * Created by luoyihua on 2017/5/4.
 */
@Component("diamondConfig")
@Scope
@DResource
public class DiamondConfig {
    private static final Logger logger = LoggerFactory.getLogger(DiamondConfig.class);

    @DAttribute(key = "task.max.alive.time")
    private Integer taskMaxAliveTime;

    @DAttribute(key = "gongfudai.http.url")
    private String httpUrl;

    @DAttribute(key = "gongfudai.appid")
    private String gfdAppId;

    @BeforeUpdate
    public void before(String key, Object newValue) {
        logger.info(key + " update to " + newValue + " start...");
    }

    @AfterUpdate
    public void after(String key, Object newValue) {
        logger.info(key + " update to " + newValue + " end...");
    }

    public Integer getTaskMaxAliveTime() {
        return taskMaxAliveTime * 1000;
    }

    public void setTaskMaxAliveTime(Integer taskMaxAliveTime) {
        this.taskMaxAliveTime = taskMaxAliveTime;
    }

    public String getHttpUrl() {
        return httpUrl;
    }

    public void setHttpUrl(String httpUrl) {
        this.httpUrl = httpUrl;
    }

    public String getGfdAppId() {
        return gfdAppId;
    }

    public void setGfdAppId(String gfdAppId) {
        this.gfdAppId = gfdAppId;
    }
}

package com.treefinance.saas.taskcenter.biz.config;

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
}

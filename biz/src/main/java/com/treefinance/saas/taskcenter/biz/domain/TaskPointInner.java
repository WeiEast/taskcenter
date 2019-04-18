package com.treefinance.saas.taskcenter.biz.domain;

import java.io.Serializable;

/**
 * @author:guoguoyun
 * @date:Created in 2019/4/18下午12:59
 */
public class TaskPointInner implements Serializable{
    private String uniqueId;
    private Byte bizType;
    private String appId;
    private String sourceId ;

    public String getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(String uniqueId) {
        this.uniqueId = uniqueId;
    }

    public Byte getBizType() {
        return bizType;
    }

    public void setBizType(Byte bizType) {
        this.bizType = bizType;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getSourceId() {
        return sourceId;
    }

    public void setSourceId(String sourceId) {
        this.sourceId = sourceId;
    }
}

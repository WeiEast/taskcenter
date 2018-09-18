package com.treefinance.saas.taskcenter.facade.result;

import com.treefinance.saas.taskcenter.facade.result.common.BaseResult;

import java.util.Date;

/**
 * @author:guoguoyun
 * @date:Created in 2018/9/18上午11:41
 */
public class TaskRO extends BaseResult {

    private Long id;

    private String uniqueId;

    private String appId;

    private String accountNo;

    private String webSite;

    private Byte bizType;


    private Byte status;

    private String stepCode;


    private Date createTime;

    private Date lastUpdateTime;

    private Byte saasEnv;

    public TaskRO(Long id, String uniqueId, String appId, String accountNo, String webSite, Byte bizType, Byte status, String stepCode, Date createTime, Date lastUpdateTime, Byte saasEnv) {
        this.id = id;
        this.uniqueId = uniqueId;
        this.appId = appId;
        this.accountNo = accountNo;
        this.webSite = webSite;
        this.bizType = bizType;
        this.status = status;
        this.stepCode = stepCode;
        this.createTime = createTime;
        this.lastUpdateTime = lastUpdateTime;
        this.saasEnv = saasEnv;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(String uniqueId) {
        this.uniqueId = uniqueId;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getAccountNo() {
        return accountNo;
    }

    public void setAccountNo(String accountNo) {
        this.accountNo = accountNo;
    }

    public String getWebSite() {
        return webSite;
    }

    public void setWebSite(String webSite) {
        this.webSite = webSite;
    }

    public Byte getBizType() {
        return bizType;
    }

    public void setBizType(Byte bizType) {
        this.bizType = bizType;
    }

    public Byte getStatus() {
        return status;
    }

    public void setStatus(Byte status) {
        this.status = status;
    }

    public String getStepCode() {
        return stepCode;
    }

    public void setStepCode(String stepCode) {
        this.stepCode = stepCode;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getLastUpdateTime() {
        return lastUpdateTime;
    }

    public void setLastUpdateTime(Date lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }

    public Byte getSaasEnv() {
        return saasEnv;
    }

    public void setSaasEnv(Byte saasEnv) {
        this.saasEnv = saasEnv;
    }

    @Override
    public String toString() {
        return "TaskRequest{" +
                "id=" + id +
                ", uniqueId='" + uniqueId + '\'' +
                ", appId='" + appId + '\'' +
                ", accountNo='" + accountNo + '\'' +
                ", webSite='" + webSite + '\'' +
                ", bizType=" + bizType +
                ", status=" + status +
                ", stepCode='" + stepCode + '\'' +
                ", createTime=" + createTime +
                ", lastUpdateTime=" + lastUpdateTime +
                ", saasEnv=" + saasEnv +
                '}';
    }
}

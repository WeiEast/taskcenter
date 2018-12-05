package com.treefinance.saas.taskcenter.facade.request;

import com.treefinance.saas.knife.request.PageRequest;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @author:guoguoyun
 * @date:Created in 2018/9/18上午11:13
 */
public class TaskRequest extends PageRequest implements Serializable{

    private Long id;

    private String uniqueId;

    private String appId;

    private String accountNo;

    private String webSite;

    private Byte bizType;

    private List<Byte> bizTypeList;

    private Byte status;

    private String stepCode;

    private Date createTime;

    private Date createTimeStart;

    private Date createTimeEnd;

    private Date lastUpdateTime;

    private Byte saasEnv;

    private List<String> appIdList;

    private Date startDate;

    private Date endDate;
    /**
     * 排序方式
     */
    private String orderByClause;

    public TaskRequest(){

    }

    public TaskRequest(Long id, String uniqueId, String appId, String accountNo, String webSite, Byte bizType, Byte status, String stepCode, Date createTime, Date lastUpdateTime, Byte saasEnv) {
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

    public List<String> getAppIdList() {
        return appIdList;
    }

    public void setAppIdList(List<String> appIdList) {
        this.appIdList = appIdList;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public List<Byte> getBizTypeList() {
        return bizTypeList;
    }

    public void setBizTypeList(List<Byte> bizTypeList) {
        this.bizTypeList = bizTypeList;
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

    public String getOrderByClause() {
        return orderByClause;
    }

    public void setOrderByClause(String orderByClause) {
        this.orderByClause = orderByClause;
    }

    public Date getCreateTimeStart() {
        return createTimeStart;
    }

    public void setCreateTimeStart(Date createTimeStart) {
        this.createTimeStart = createTimeStart;
    }

    public Date getCreateTimeEnd() {
        return createTimeEnd;
    }

    public void setCreateTimeEnd(Date createTimeEnd) {
        this.createTimeEnd = createTimeEnd;
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
                ", bizTypeList=" + bizTypeList +
                ", status=" + status +
                ", stepCode='" + stepCode + '\'' +
                ", createTime=" + createTime +
                ", createTimeStart=" + createTimeStart +
                ", createTimeEnd=" + createTimeEnd +
                ", lastUpdateTime=" + lastUpdateTime +
                ", saasEnv=" + saasEnv +
                ", orderByClause='" + orderByClause + '\'' +
                ", appIdList=" + appIdList +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                '}';
    }
}

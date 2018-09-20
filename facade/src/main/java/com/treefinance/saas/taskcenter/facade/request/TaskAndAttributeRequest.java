package com.treefinance.saas.taskcenter.facade.request;

import com.treefinance.saas.knife.request.PageRequest;
import com.treefinance.saas.taskcenter.facade.result.common.BaseResult;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @author chengtong
 * @date 18/9/19 20:27
 */
public class TaskAndAttributeRequest extends PageRequest implements Serializable{

    private String appId;

    private Byte saasEnv;

    private String name;

    private Integer status;

    private Integer bizType;

    private List<Integer> bizTypeList;

    private String webSite;
    private String value;
    private Date startTime;
    private Date endTime;

    private Integer start;
    private Integer limit;

    private String orderStr;

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public Byte getSaasEnv() {
        return saasEnv;
    }

    public void setSaasEnv(Byte saasEnv) {
        this.saasEnv = saasEnv;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getBizType() {
        return bizType;
    }

    public void setBizType(Integer bizType) {
        this.bizType = bizType;
    }

    public List<Integer> getBizTypeList() {
        return bizTypeList;
    }

    public void setBizTypeList(List<Integer> bizTypeList) {
        this.bizTypeList = bizTypeList;
    }

    public String getWebSite() {
        return webSite;
    }

    public void setWebSite(String webSite) {
        this.webSite = webSite;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public Integer getStart() {
        return start;
    }

    public void setStart(Integer start) {
        this.start = start;
    }

    public Integer getLimit() {
        return limit;
    }

    public void setLimit(Integer limit) {
        this.limit = limit;
    }

    public String getOrderStr() {
        return orderStr;
    }

    public void setOrderStr(String orderStr) {
        this.orderStr = orderStr;
    }
}

package com.treefinance.saas.taskcenter.facade.request;

import com.treefinance.saas.taskcenter.facade.result.common.BaseResult;

import java.util.Date;
import java.util.List;

/**
 * @author:guoguoyun
 * @date:Created in 2018/9/18下午2:04
 */
public class TaskCallbackLogRequest extends BaseRequest{

    private Long id;

    private Long taskId;

    private List<Long> taskIdList;

    private Long configId;

    private Byte type;

    private String url;

    private String requestParam;

    private String responseData;

    private Integer consumeTime;

    private Integer httpCode;


    private String callbackCode;


    private String callbackMsg;


    private Byte failureReason;


    private Date createTime;


    private Date lastUpdateTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    public Long getConfigId() {
        return configId;
    }

    public void setConfigId(Long configId) {
        this.configId = configId;
    }

    public Byte getType() {
        return type;
    }

    public void setType(Byte type) {
        this.type = type;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getRequestParam() {
        return requestParam;
    }

    public void setRequestParam(String requestParam) {
        this.requestParam = requestParam;
    }

    public String getResponseData() {
        return responseData;
    }

    public void setResponseData(String responseData) {
        this.responseData = responseData;
    }

    public Integer getConsumeTime() {
        return consumeTime;
    }

    public void setConsumeTime(Integer consumeTime) {
        this.consumeTime = consumeTime;
    }

    public Integer getHttpCode() {
        return httpCode;
    }

    public void setHttpCode(Integer httpCode) {
        this.httpCode = httpCode;
    }

    public String getCallbackCode() {
        return callbackCode;
    }

    public void setCallbackCode(String callbackCode) {
        this.callbackCode = callbackCode;
    }

    public String getCallbackMsg() {
        return callbackMsg;
    }

    public void setCallbackMsg(String callbackMsg) {
        this.callbackMsg = callbackMsg;
    }

    public Byte getFailureReason() {
        return failureReason;
    }

    public void setFailureReason(Byte failureReason) {
        this.failureReason = failureReason;
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

    public List<Long> getTaskIdList() {
        return taskIdList;
    }

    public void setTaskIdList(List<Long> taskIdList) {
        this.taskIdList = taskIdList;
    }

    @Override
    public String toString() {
        return "TaskCallbackLogRequest{" +
                "id=" + id +
                ", taskId=" + taskId +
                ", taskIdList=" + taskIdList +
                ", configId=" + configId +
                ", type=" + type +
                ", url='" + url + '\'' +
                ", requestParam='" + requestParam + '\'' +
                ", responseData='" + responseData + '\'' +
                ", consumeTime=" + consumeTime +
                ", httpCode=" + httpCode +
                ", callbackCode='" + callbackCode + '\'' +
                ", callbackMsg='" + callbackMsg + '\'' +
                ", failureReason=" + failureReason +
                ", createTime=" + createTime +
                ", lastUpdateTime=" + lastUpdateTime +
                '}';
    }
}

package com.treefinance.saas.taskcenter.facade.result;

import com.treefinance.saas.taskcenter.facade.result.common.BaseResult;

import java.util.Date;

/**
 * @author:guoguoyun
 * @date:Created in 2018/9/18下午1:54
 */
public class TaskLogRO extends BaseResult {
    private Long id;


    private Long taskId;


    private String stepCode;


    private String msg;


    private Date occurTime;

    private String errorMsg;


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

    public String getStepCode() {
        return stepCode;
    }

    public void setStepCode(String stepCode) {
        this.stepCode = stepCode;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Date getOccurTime() {
        return occurTime;
    }

    public void setOccurTime(Date occurTime) {
        this.occurTime = occurTime;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
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

    @Override
    public String toString() {
        return "TaskLogRO{" +
                "id=" + id +
                ", taskId=" + taskId +
                ", stepCode='" + stepCode + '\'' +
                ", msg='" + msg + '\'' +
                ", occurTime=" + occurTime +
                ", errorMsg='" + errorMsg + '\'' +
                ", createTime=" + createTime +
                ", lastUpdateTime=" + lastUpdateTime +
                '}';
    }
}

package com.treefinance.saas.taskcenter.facade.request;

import java.util.Date;
import java.util.List;

/**
 * @author:guoguoyun
 * @date:Created in 2018/9/18下午1:49
 */
public class TaskLogRequest extends BaseRequest {

    private Long id;

    private Long taskId;

    private List<Long> taskIdList;

    private String stepCode;

    private String msg;

    private Date occurTime;

    private String errorMsg;

    private Date createTime;

    private Date lastUpdateTime;

    /**
     * 排序方式
     */
    private String orderByClause;

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

    public String getOrderByClause() {

        return orderByClause;
    }

    public void setOrderByClause(String orderByClause) {
        orderByClause = orderByClause;
    }

    public List<Long> getTaskIdList() {
        return taskIdList;
    }

    public void setTaskIdList(List<Long> taskIdList) {
        this.taskIdList = taskIdList;
    }

    @Override
    public String toString() {
        return "TaskLogRequest{" + "id=" + id + ", taskId=" + taskId + ", stepCode='" + stepCode + '\'' + ", msg='" + msg + '\'' + ", occurTime=" + occurTime + ", errorMsg='"
            + errorMsg + '\'' + ", createTime=" + createTime + ", lastUpdateTime=" + lastUpdateTime + ", orderByClause='" + orderByClause + '\'' + '}';
    }
}

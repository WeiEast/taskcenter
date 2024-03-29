package com.treefinance.saas.taskcenter.facade.request;

import java.util.Date;

/**
 * @author:guoguoyun
 * @date:Created in 2018/9/18下午1:58
 */
public class TaskBuryPointLogRequest extends BaseRequest {
    private Long id;

    private Long taskId;

    private String appId;

    private String code;

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

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
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

    @Override
    public String toString() {
        return "TaskBuryPointLogRequest{" + "id=" + id + ", taskId=" + taskId + ", appId='" + appId + '\'' + ", code='" + code + '\'' + ", createTime=" + createTime
            + ", lastUpdateTime=" + lastUpdateTime + ", orderByClause='" + orderByClause + '\'' + '}';
    }
}

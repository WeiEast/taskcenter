package com.treefinance.saas.taskcenter.facade.result;

import com.treefinance.saas.taskcenter.facade.result.common.BaseResult;

import java.util.Date;

/**
 * @author:guoguoyun
 * @date:Created in 2018/9/18下午1:46
 */
public class TaskAttributeRO extends BaseResult {

    private Long id;

    private Long taskId;

    private String name;

    private String value;

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
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
        return "TaskAttributeRO{" + "id=" + id + ", taskId=" + taskId + ", name='" + name + '\'' + ", value='" + value + '\'' + ", createTime=" + createTime + ", lastUpdateTime="
            + lastUpdateTime + '}';
    }
}

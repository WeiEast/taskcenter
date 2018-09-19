package com.treefinance.saas.taskcenter.facade.request;

import java.util.Date;
import java.util.List;

/**
 * @author:guoguoyun
 * @date:Created in 2018/9/18上午11:47
 */
public class TaskAttributeRequest extends BaseRequest{


    private Long id;


    private List<Long> taskIds;


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

    public List<Long> getTaskIds() {
        return taskIds;
    }

    public void setTaskIds(List<Long> taskIds) {
        this.taskIds = taskIds;
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
        return "TaskAttributeRequest{" +
                "id=" + id +
                ", taskIds=" + taskIds +
                ", name='" + name + '\'' +
                ", value='" + value + '\'' +
                ", createTime=" + createTime +
                ", lastUpdateTime=" + lastUpdateTime +
                '}';
    }
}

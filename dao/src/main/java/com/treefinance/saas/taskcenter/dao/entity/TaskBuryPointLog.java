package com.treefinance.saas.taskcenter.dao.entity;

import java.io.Serializable;
import java.util.Date;

public class TaskBuryPointLog implements Serializable {
    /**
     * This field was generated by MyBatis Generator. This field corresponds to the database table task_bury_point_log
     *
     * @mbg.generated Tue Sep 18 11:27:31 CST 2018
     */
    private static final long serialVersionUID = 1L;
    /**
     *
     * This field was generated by MyBatis Generator. This field corresponds to the database column
     * task_bury_point_log.id
     *
     * @mbg.generated Tue Sep 18 11:27:31 CST 2018
     */
    private Long id;
    /**
     *
     * This field was generated by MyBatis Generator. This field corresponds to the database column
     * task_bury_point_log.taskId
     *
     * @mbg.generated Tue Sep 18 11:27:31 CST 2018
     */
    private Long taskId;
    /**
     *
     * This field was generated by MyBatis Generator. This field corresponds to the database column
     * task_bury_point_log.appId
     *
     * @mbg.generated Tue Sep 18 11:27:31 CST 2018
     */
    private String appId;
    /**
     *
     * This field was generated by MyBatis Generator. This field corresponds to the database column
     * task_bury_point_log.code
     *
     * @mbg.generated Tue Sep 18 11:27:31 CST 2018
     */
    private String code;
    /**
     *
     * This field was generated by MyBatis Generator. This field corresponds to the database column
     * task_bury_point_log.createTime
     *
     * @mbg.generated Tue Sep 18 11:27:31 CST 2018
     */
    private Date createTime;
    /**
     *
     * This field was generated by MyBatis Generator. This field corresponds to the database column
     * task_bury_point_log.lastUpdateTime
     *
     * @mbg.generated Tue Sep 18 11:27:31 CST 2018
     */
    private Date lastUpdateTime;

    /**
     * This method was generated by MyBatis Generator. This method returns the value of the database column
     * task_bury_point_log.id
     *
     * @return the value of task_bury_point_log.id
     *
     * @mbg.generated Tue Sep 18 11:27:31 CST 2018
     */
    public Long getId() {
        return id;
    }

    /**
     * This method was generated by MyBatis Generator. This method sets the value of the database column
     * task_bury_point_log.id
     *
     * @param id the value for task_bury_point_log.id
     *
     * @mbg.generated Tue Sep 18 11:27:31 CST 2018
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * This method was generated by MyBatis Generator. This method returns the value of the database column
     * task_bury_point_log.taskId
     *
     * @return the value of task_bury_point_log.taskId
     *
     * @mbg.generated Tue Sep 18 11:27:31 CST 2018
     */
    public Long getTaskId() {
        return taskId;
    }

    /**
     * This method was generated by MyBatis Generator. This method sets the value of the database column
     * task_bury_point_log.taskId
     *
     * @param taskId the value for task_bury_point_log.taskId
     *
     * @mbg.generated Tue Sep 18 11:27:31 CST 2018
     */
    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    /**
     * This method was generated by MyBatis Generator. This method returns the value of the database column
     * task_bury_point_log.appId
     *
     * @return the value of task_bury_point_log.appId
     *
     * @mbg.generated Tue Sep 18 11:27:31 CST 2018
     */
    public String getAppId() {
        return appId;
    }

    /**
     * This method was generated by MyBatis Generator. This method sets the value of the database column
     * task_bury_point_log.appId
     *
     * @param appId the value for task_bury_point_log.appId
     *
     * @mbg.generated Tue Sep 18 11:27:31 CST 2018
     */
    public void setAppId(String appId) {
        this.appId = appId == null ? null : appId.trim();
    }

    /**
     * This method was generated by MyBatis Generator. This method returns the value of the database column
     * task_bury_point_log.code
     *
     * @return the value of task_bury_point_log.code
     *
     * @mbg.generated Tue Sep 18 11:27:31 CST 2018
     */
    public String getCode() {
        return code;
    }

    /**
     * This method was generated by MyBatis Generator. This method sets the value of the database column
     * task_bury_point_log.code
     *
     * @param code the value for task_bury_point_log.code
     *
     * @mbg.generated Tue Sep 18 11:27:31 CST 2018
     */
    public void setCode(String code) {
        this.code = code == null ? null : code.trim();
    }

    /**
     * This method was generated by MyBatis Generator. This method returns the value of the database column
     * task_bury_point_log.createTime
     *
     * @return the value of task_bury_point_log.createTime
     *
     * @mbg.generated Tue Sep 18 11:27:31 CST 2018
     */
    public Date getCreateTime() {
        return createTime;
    }

    /**
     * This method was generated by MyBatis Generator. This method sets the value of the database column
     * task_bury_point_log.createTime
     *
     * @param createTime the value for task_bury_point_log.createTime
     *
     * @mbg.generated Tue Sep 18 11:27:31 CST 2018
     */
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    /**
     * This method was generated by MyBatis Generator. This method returns the value of the database column
     * task_bury_point_log.lastUpdateTime
     *
     * @return the value of task_bury_point_log.lastUpdateTime
     *
     * @mbg.generated Tue Sep 18 11:27:31 CST 2018
     */
    public Date getLastUpdateTime() {
        return lastUpdateTime;
    }

    /**
     * This method was generated by MyBatis Generator. This method sets the value of the database column
     * task_bury_point_log.lastUpdateTime
     *
     * @param lastUpdateTime the value for task_bury_point_log.lastUpdateTime
     *
     * @mbg.generated Tue Sep 18 11:27:31 CST 2018
     */
    public void setLastUpdateTime(Date lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }

    /**
     * This method was generated by MyBatis Generator. This method corresponds to the database table task_bury_point_log
     *
     * @mbg.generated Tue Sep 18 11:27:31 CST 2018
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", taskId=").append(taskId);
        sb.append(", appId=").append(appId);
        sb.append(", code=").append(code);
        sb.append(", createTime=").append(createTime);
        sb.append(", lastUpdateTime=").append(lastUpdateTime);
        sb.append("]");
        return sb.toString();
    }
}
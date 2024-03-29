package com.treefinance.saas.taskcenter.dao.entity;

import java.io.Serializable;
import java.util.Date;

public class TaskPoint implements Serializable {
    /**
     * This field was generated by MyBatis Generator. This field corresponds to the database table task_point
     *
     * @mbg.generated Tue Feb 26 14:18:47 CST 2019
     */
    private static final long serialVersionUID = 1L;
    /**
     *
     * This field was generated by MyBatis Generator. This field corresponds to the database column task_point.id
     *
     * @mbg.generated Tue Feb 26 14:18:47 CST 2019
     */
    private Long id;
    /**
     *
     * This field was generated by MyBatis Generator. This field corresponds to the database column task_point.task_id
     *
     * @mbg.generated Tue Feb 26 14:18:47 CST 2019
     */
    private Long taskId;
    /**
     *
     * This field was generated by MyBatis Generator. This field corresponds to the database column task_point.app_id
     *
     * @mbg.generated Tue Feb 26 14:18:47 CST 2019
     */
    private String appId;
    /**
     *
     * This field was generated by MyBatis Generator. This field corresponds to the database column task_point.unique_id
     *
     * @mbg.generated Tue Feb 26 14:18:47 CST 2019
     */
    private String uniqueId;
    /**
     *
     * This field was generated by MyBatis Generator. This field corresponds to the database column task_point.type
     *
     * @mbg.generated Tue Feb 26 14:18:47 CST 2019
     */
    private Byte type;
    /**
     *
     * This field was generated by MyBatis Generator. This field corresponds to the database column task_point.code
     *
     * @mbg.generated Tue Feb 26 14:18:47 CST 2019
     */
    private String code;
    /**
     *
     * This field was generated by MyBatis Generator. This field corresponds to the database column task_point.step
     *
     * @mbg.generated Tue Feb 26 14:18:47 CST 2019
     */
    private String step;
    /**
     *
     * This field was generated by MyBatis Generator. This field corresponds to the database column task_point.sub_step
     *
     * @mbg.generated Tue Feb 26 14:18:47 CST 2019
     */
    private String subStep;
    /**
     *
     * This field was generated by MyBatis Generator. This field corresponds to the database column task_point.biz_type
     *
     * @mbg.generated Tue Feb 26 14:18:47 CST 2019
     */
    private Byte bizType;
    /**
     *
     * This field was generated by MyBatis Generator. This field corresponds to the database column task_point.msg
     *
     * @mbg.generated Tue Feb 26 14:18:47 CST 2019
     */
    private String msg;
    /**
     *
     * This field was generated by MyBatis Generator. This field corresponds to the database column task_point.remake
     *
     * @mbg.generated Tue Feb 26 14:18:47 CST 2019
     */
    private String remake;
    /**
     *
     * This field was generated by MyBatis Generator. This field corresponds to the database column task_point.ip
     *
     * @mbg.generated Tue Feb 26 14:18:47 CST 2019
     */
    private String ip;
    /**
     *
     * This field was generated by MyBatis Generator. This field corresponds to the database column
     * task_point.occur_time
     *
     * @mbg.generated Tue Feb 26 14:18:47 CST 2019
     */
    private Date occurTime;
    /**
     *
     * This field was generated by MyBatis Generator. This field corresponds to the database column
     * task_point.create_time
     *
     * @mbg.generated Tue Feb 26 14:18:47 CST 2019
     */
    private Date createTime;
    /**
     *
     * This field was generated by MyBatis Generator. This field corresponds to the database column
     * task_point.last_update_time
     *
     * @mbg.generated Tue Feb 26 14:18:47 CST 2019
     */
    private Date lastUpdateTime;

    /**
     * This method was generated by MyBatis Generator. This method returns the value of the database column
     * task_point.id
     *
     * @return the value of task_point.id
     *
     * @mbg.generated Tue Feb 26 14:18:47 CST 2019
     */
    public Long getId() {
        return id;
    }

    /**
     * This method was generated by MyBatis Generator. This method sets the value of the database column task_point.id
     *
     * @param id the value for task_point.id
     *
     * @mbg.generated Tue Feb 26 14:18:47 CST 2019
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * This method was generated by MyBatis Generator. This method returns the value of the database column
     * task_point.task_id
     *
     * @return the value of task_point.task_id
     *
     * @mbg.generated Tue Feb 26 14:18:47 CST 2019
     */
    public Long getTaskId() {
        return taskId;
    }

    /**
     * This method was generated by MyBatis Generator. This method sets the value of the database column
     * task_point.task_id
     *
     * @param taskId the value for task_point.task_id
     *
     * @mbg.generated Tue Feb 26 14:18:47 CST 2019
     */
    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    /**
     * This method was generated by MyBatis Generator. This method returns the value of the database column
     * task_point.app_id
     *
     * @return the value of task_point.app_id
     *
     * @mbg.generated Tue Feb 26 14:18:47 CST 2019
     */
    public String getAppId() {
        return appId;
    }

    /**
     * This method was generated by MyBatis Generator. This method sets the value of the database column
     * task_point.app_id
     *
     * @param appId the value for task_point.app_id
     *
     * @mbg.generated Tue Feb 26 14:18:47 CST 2019
     */
    public void setAppId(String appId) {
        this.appId = appId == null ? null : appId.trim();
    }

    /**
     * This method was generated by MyBatis Generator. This method returns the value of the database column
     * task_point.unique_id
     *
     * @return the value of task_point.unique_id
     *
     * @mbg.generated Tue Feb 26 14:18:47 CST 2019
     */
    public String getUniqueId() {
        return uniqueId;
    }

    /**
     * This method was generated by MyBatis Generator. This method sets the value of the database column
     * task_point.unique_id
     *
     * @param uniqueId the value for task_point.unique_id
     *
     * @mbg.generated Tue Feb 26 14:18:47 CST 2019
     */
    public void setUniqueId(String uniqueId) {
        this.uniqueId = uniqueId == null ? null : uniqueId.trim();
    }

    /**
     * This method was generated by MyBatis Generator. This method returns the value of the database column
     * task_point.type
     *
     * @return the value of task_point.type
     *
     * @mbg.generated Tue Feb 26 14:18:47 CST 2019
     */
    public Byte getType() {
        return type;
    }

    /**
     * This method was generated by MyBatis Generator. This method sets the value of the database column task_point.type
     *
     * @param type the value for task_point.type
     *
     * @mbg.generated Tue Feb 26 14:18:47 CST 2019
     */
    public void setType(Byte type) {
        this.type = type;
    }

    /**
     * This method was generated by MyBatis Generator. This method returns the value of the database column
     * task_point.code
     *
     * @return the value of task_point.code
     *
     * @mbg.generated Tue Feb 26 14:18:47 CST 2019
     */
    public String getCode() {
        return code;
    }

    /**
     * This method was generated by MyBatis Generator. This method sets the value of the database column task_point.code
     *
     * @param code the value for task_point.code
     *
     * @mbg.generated Tue Feb 26 14:18:47 CST 2019
     */
    public void setCode(String code) {
        this.code = code == null ? null : code.trim();
    }

    /**
     * This method was generated by MyBatis Generator. This method returns the value of the database column
     * task_point.step
     *
     * @return the value of task_point.step
     *
     * @mbg.generated Tue Feb 26 14:18:47 CST 2019
     */
    public String getStep() {
        return step;
    }

    /**
     * This method was generated by MyBatis Generator. This method sets the value of the database column task_point.step
     *
     * @param step the value for task_point.step
     *
     * @mbg.generated Tue Feb 26 14:18:47 CST 2019
     */
    public void setStep(String step) {
        this.step = step == null ? null : step.trim();
    }

    /**
     * This method was generated by MyBatis Generator. This method returns the value of the database column
     * task_point.sub_step
     *
     * @return the value of task_point.sub_step
     *
     * @mbg.generated Tue Feb 26 14:18:47 CST 2019
     */
    public String getSubStep() {
        return subStep;
    }

    /**
     * This method was generated by MyBatis Generator. This method sets the value of the database column
     * task_point.sub_step
     *
     * @param subStep the value for task_point.sub_step
     *
     * @mbg.generated Tue Feb 26 14:18:47 CST 2019
     */
    public void setSubStep(String subStep) {
        this.subStep = subStep == null ? null : subStep.trim();
    }

    /**
     * This method was generated by MyBatis Generator. This method returns the value of the database column
     * task_point.biz_type
     *
     * @return the value of task_point.biz_type
     *
     * @mbg.generated Tue Feb 26 14:18:47 CST 2019
     */
    public Byte getBizType() {
        return bizType;
    }

    /**
     * This method was generated by MyBatis Generator. This method sets the value of the database column
     * task_point.biz_type
     *
     * @param bizType the value for task_point.biz_type
     *
     * @mbg.generated Tue Feb 26 14:18:47 CST 2019
     */
    public void setBizType(Byte bizType) {
        this.bizType = bizType;
    }

    /**
     * This method was generated by MyBatis Generator. This method returns the value of the database column
     * task_point.msg
     *
     * @return the value of task_point.msg
     *
     * @mbg.generated Tue Feb 26 14:18:47 CST 2019
     */
    public String getMsg() {
        return msg;
    }

    /**
     * This method was generated by MyBatis Generator. This method sets the value of the database column task_point.msg
     *
     * @param msg the value for task_point.msg
     *
     * @mbg.generated Tue Feb 26 14:18:47 CST 2019
     */
    public void setMsg(String msg) {
        this.msg = msg == null ? null : msg.trim();
    }

    /**
     * This method was generated by MyBatis Generator. This method returns the value of the database column
     * task_point.remake
     *
     * @return the value of task_point.remake
     *
     * @mbg.generated Tue Feb 26 14:18:47 CST 2019
     */
    public String getRemake() {
        return remake;
    }

    /**
     * This method was generated by MyBatis Generator. This method sets the value of the database column
     * task_point.remake
     *
     * @param remake the value for task_point.remake
     *
     * @mbg.generated Tue Feb 26 14:18:47 CST 2019
     */
    public void setRemake(String remake) {
        this.remake = remake == null ? null : remake.trim();
    }

    /**
     * This method was generated by MyBatis Generator. This method returns the value of the database column
     * task_point.ip
     *
     * @return the value of task_point.ip
     *
     * @mbg.generated Tue Feb 26 14:18:47 CST 2019
     */
    public String getIp() {
        return ip;
    }

    /**
     * This method was generated by MyBatis Generator. This method sets the value of the database column task_point.ip
     *
     * @param ip the value for task_point.ip
     *
     * @mbg.generated Tue Feb 26 14:18:47 CST 2019
     */
    public void setIp(String ip) {
        this.ip = ip == null ? null : ip.trim();
    }

    /**
     * This method was generated by MyBatis Generator. This method returns the value of the database column
     * task_point.occur_time
     *
     * @return the value of task_point.occur_time
     *
     * @mbg.generated Tue Feb 26 14:18:47 CST 2019
     */
    public Date getOccurTime() {
        return occurTime;
    }

    /**
     * This method was generated by MyBatis Generator. This method sets the value of the database column
     * task_point.occur_time
     *
     * @param occurTime the value for task_point.occur_time
     *
     * @mbg.generated Tue Feb 26 14:18:47 CST 2019
     */
    public void setOccurTime(Date occurTime) {
        this.occurTime = occurTime;
    }

    /**
     * This method was generated by MyBatis Generator. This method returns the value of the database column
     * task_point.create_time
     *
     * @return the value of task_point.create_time
     *
     * @mbg.generated Tue Feb 26 14:18:47 CST 2019
     */
    public Date getCreateTime() {
        return createTime;
    }

    /**
     * This method was generated by MyBatis Generator. This method sets the value of the database column
     * task_point.create_time
     *
     * @param createTime the value for task_point.create_time
     *
     * @mbg.generated Tue Feb 26 14:18:47 CST 2019
     */
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    /**
     * This method was generated by MyBatis Generator. This method returns the value of the database column
     * task_point.last_update_time
     *
     * @return the value of task_point.last_update_time
     *
     * @mbg.generated Tue Feb 26 14:18:47 CST 2019
     */
    public Date getLastUpdateTime() {
        return lastUpdateTime;
    }

    /**
     * This method was generated by MyBatis Generator. This method sets the value of the database column
     * task_point.last_update_time
     *
     * @param lastUpdateTime the value for task_point.last_update_time
     *
     * @mbg.generated Tue Feb 26 14:18:47 CST 2019
     */
    public void setLastUpdateTime(Date lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }

    /**
     * This method was generated by MyBatis Generator. This method corresponds to the database table task_point
     *
     * @mbg.generated Tue Feb 26 14:18:47 CST 2019
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
        sb.append(", uniqueId=").append(uniqueId);
        sb.append(", type=").append(type);
        sb.append(", code=").append(code);
        sb.append(", step=").append(step);
        sb.append(", subStep=").append(subStep);
        sb.append(", bizType=").append(bizType);
        sb.append(", msg=").append(msg);
        sb.append(", remake=").append(remake);
        sb.append(", ip=").append(ip);
        sb.append(", occurTime=").append(occurTime);
        sb.append(", createTime=").append(createTime);
        sb.append(", lastUpdateTime=").append(lastUpdateTime);
        sb.append("]");
        return sb.toString();
    }
}
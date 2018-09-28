package com.treefinance.saas.taskcenter.facade.request;

import java.io.Serializable;

/**
 * @author haojiahong
 * @date 2018/9/27
 */
public class TaskDirectiveRequest implements Serializable {

    private static final long serialVersionUID = -7805858512390163733L;

    /**
     * 指令ID
     */
    private String directiveId;
    /**
     * 指令
     */
    private String directive;
    /**
     * 任务ID
     */
    private Long taskId;
    /**
     * 指令扩展信息：json格式
     */
    private String remark;
    /**
     * 任务详细信息
     */
    private Task4DirectiveRequest task;

    public String getDirectiveId() {
        return directiveId;
    }

    public void setDirectiveId(String directiveId) {
        this.directiveId = directiveId;
    }

    public String getDirective() {
        return directive;
    }

    public void setDirective(String directive) {
        this.directive = directive;
    }

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Task4DirectiveRequest getTask() {
        return task;
    }

    public void setTask(Task4DirectiveRequest task) {
        this.task = task;
    }
}

/*
 * Copyright © 2015 - 2017 杭州大树网络技术有限公司. All Rights Reserved
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.treefinance.saas.taskcenter.dto;

/**
 * Created by yh-treefinance on 2017/7/6.
 */
public class DirectiveDTO extends BaseDTO {
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
    private TaskDTO task;


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

    public TaskDTO getTask() {
        return task;
    }

    public void setTask(TaskDTO task) {
        this.task = task;
    }
}

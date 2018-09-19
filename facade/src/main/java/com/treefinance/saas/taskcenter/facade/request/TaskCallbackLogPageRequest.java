package com.treefinance.saas.taskcenter.facade.request;

import com.treefinance.saas.knife.request.PageRequest;

import java.util.List;

/**
 * @author chengtong
 * @date 18/9/19 15:57
 */
public class TaskCallbackLogPageRequest extends PageRequest {

    /**
     * 编号列表
     * */
    List<Long> taskIdList;

    public List<Long> getTaskIdList() {
        return taskIdList;
    }

    public void setTaskIdList(List<Long> taskIdList) {
        this.taskIdList = taskIdList;
    }
}

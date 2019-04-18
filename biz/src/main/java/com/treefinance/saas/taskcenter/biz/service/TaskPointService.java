package com.treefinance.saas.taskcenter.biz.service;

import com.treefinance.saas.taskcenter.facade.request.TaskPointRequest;

/**
 * @author 张琰佳
 * @since 8:38 PM 2019/1/24
 */
public interface TaskPointService {

    /**
     * 添加系统埋点
     * 
     * @param taskId 任务ID
     * @param pointCode 埋点编号
     */
    void addTaskPoint(Long taskId, String pointCode);

    /**
     * 添加系统埋点
     * 
     * @param taskId 任务ID
     * @param pointCode 埋点编号
     * @param ip IP
     */
    void addTaskPoint(Long taskId, String pointCode, String ip);

    /**
     * 添加埋点
     *
     * @param taskPointRequest 埋点参数
     */
    void addTaskPoint(TaskPointRequest taskPointRequest);
}

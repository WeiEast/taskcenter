package com.treefinance.saas.taskcenter.facade.service;

import com.treefinance.saas.taskcenter.facade.request.CompositeTaskAttrPagingQueryRequest;
import com.treefinance.saas.taskcenter.facade.request.TaskAndAttributeRequest;
import com.treefinance.saas.taskcenter.facade.request.TaskCreateRequest;
import com.treefinance.saas.taskcenter.facade.request.TaskPagingQueryRequest;
import com.treefinance.saas.taskcenter.facade.request.TaskQueryRequest;
import com.treefinance.saas.taskcenter.facade.request.TaskRequest;
import com.treefinance.saas.taskcenter.facade.request.TaskStepLogRequest;
import com.treefinance.saas.taskcenter.facade.request.TaskUpdateRequest;
import com.treefinance.saas.taskcenter.facade.response.TaskResponse;
import com.treefinance.saas.taskcenter.facade.result.CompositeTaskAttrDTO;
import com.treefinance.saas.taskcenter.facade.result.PagingDataSet;
import com.treefinance.saas.taskcenter.facade.result.SimpleTaskDTO;
import com.treefinance.saas.taskcenter.facade.result.TaskAndAttributeRO;
import com.treefinance.saas.taskcenter.facade.result.TaskRO;
import com.treefinance.saas.taskcenter.facade.result.TaskUpdateStatusDTO;
import com.treefinance.saas.taskcenter.facade.result.TaskingMerchantBaseDTO;
import com.treefinance.saas.taskcenter.facade.result.common.TaskPagingResult;
import com.treefinance.saas.taskcenter.facade.result.common.TaskResult;

import javax.validation.constraints.NotNull;

import java.util.Date;
import java.util.List;

/**
 * @author:guoguoyun
 * @date:Created in 2018/9/18上午10:20
 */
public interface TaskFacade {

    /**
     * 条件查询任务列表
     *
     * @param taskRequest
     * @return
     */
    @Deprecated
    TaskResult<List<TaskRO>> queryTask(TaskRequest taskRequest);

    /**
     * 条件查询任务列表（分页）
     *
     * @param taskRequest
     * @deprecated Use {@link #queryPagingTasks(TaskPagingQueryRequest)} instead
     */
    @Deprecated
    TaskPagingResult<TaskRO> queryTaskWithPagination(TaskRequest taskRequest);

    /**
     * 更新任务
     *
     * @param taskId 必填
     * @param accountNo 账号
     * @param webSite 网站标识
     * @deprecated Use {@link #updateAccountNoAndWebsiteWhenProcessing(Long, String, String)} instead
     */
    @Deprecated
    TaskResult<Void> updateTask(Long taskId, String accountNo, String webSite);

    /**
     * 更新处理中的任务信息
     * <p>
     * 注意：accountNo会加密,设计不友好
     * </p>
     *
     * @param request 更新请求
     * @return 更新数
     * @deprecated Use {@link #updateProcessingTaskById(TaskUpdateRequest)} instead
     */
    @Deprecated
    TaskResult<Integer> updateUnfinishedTask(TaskUpdateRequest request);

    /**
     * @deprecated Use {@link #updateStatusIfDone(Long, Byte)} } instead
     */
    @Deprecated
    TaskResult<String> updateTaskStatusWithStep(Long taskId, Byte status);

    /**
     * 根据id获得任务详情
     *
     * @deprecated Use {@link #getTaskById(Long)} instead
     */
    @Deprecated
    TaskResult<TaskRO> getTaskByPrimaryKey(TaskRequest request);

    /**
     * console -- 查询任务列表 /saas/console/tasks
     *
     * @param request
     * @return
     */
    @Deprecated
    TaskPagingResult<TaskRO> queryTaskListPage(TaskRequest request);

    /**
     * console -- 查询任务列表数据
     *
     * @param taskRequest
     * @return
     */
    @Deprecated
    TaskResult<List<TaskRO>> queryTaskList(TaskRequest taskRequest);

    @Deprecated
    TaskPagingResult<TaskAndAttributeRO> queryTaskAndTaskAttribute(TaskAndAttributeRequest request);

    /**
     *
     * @deprecated Use {@link #listTaskIdsWithSameTrigger(Long)} instead
     */
    @Deprecated
    TaskResult<List<Long>> getUserTaskIdList(Long taskId);

    /**
     * 根据指定的环境<code>saasEnv</code>和时间区间列出正在运行中的任务。
     * <p/>
     * 注意：<code>startTime</code> &lt; <code>endTime</code>
     *
     * @param saasEnv 环境标识
     * @param startTime 终止时间（不包含）
     * @param endTime 起始时间（包含）
     * @return 运行中的任务列表
     * @deprecated Use {@link #listRunningTasks(Byte, Date, Date)} instead
     */
    @Deprecated
    TaskResult<List<TaskRO>> selectRecentRunningTaskList(Byte saasEnv, Date startTime, Date endTime);

    /******************* QUERY操作 *******************/

    /**
     * 根据任务ID获取任务
     * 
     * @param id 任务ID
     * @return 任务信息 {@link SimpleTaskDTO}
     */
    TaskResponse<SimpleTaskDTO> getTaskById(Long id);

    /**
     * 根据指定的环境<code>saasEnv</code>和时间区间列出正在运行中的任务。
     * <p/>
     * 注意：<code>startTime</code> &lt; <code>endTime</code>
     * 
     * @param saasEnv 环境标识
     * @param startTime 起始时间（包含）
     * @param endTime 终止时间（不包含）
     * @return 运行中的任务列表
     */
    TaskResponse<List<SimpleTaskDTO>> listRunningTasks(Byte saasEnv, Date startTime, Date endTime);

    /**
     * 列出跟指定任务具有相同触发条件的任务ID。 触发条件包括相同的appId，相同的uniqueId以及相同的业务类型
     *
     * @param taskId 任务ID
     * @return taskId的列表
     */
    TaskResponse<List<Long>> listTaskIdsWithSameTrigger(Long taskId);

    /**
     * 根据taskId查询任务并检查是否是完成的。
     * 
     * @param taskId 任务ID
     * @return {@link SimpleTaskDTO} if the task was completed, otherwise null.
     */
    TaskResponse<SimpleTaskDTO> queryCompletedTaskById(Long taskId);

    /**
     * 根据查询条件查询任务列表
     * 
     * @param request 查询条件
     * @return 任务列表
     */
    TaskResponse<List<SimpleTaskDTO>> queryTasks(TaskQueryRequest request);

    /**
     * 分页查询任务数据
     * 
     * @param request 分页查询条件
     * @return 分页数据
     */
    TaskResponse<PagingDataSet<SimpleTaskDTO>> queryPagingTasks(TaskPagingQueryRequest request);

    /**
     * 分页查询复合任务信息，包含任务属性
     * 
     * @param request 分页查询条件
     * @return 复合任务的分页数据
     */
    TaskResponse<PagingDataSet<CompositeTaskAttrDTO>> queryPagingCompositeTaskAttrs(CompositeTaskAttrPagingQueryRequest request);

    /******************* CUD操作 *******************/

    /**
     * 创建任务
     *
     * @param request 创建请求参数
     * @return 任务ID
     */
    TaskResponse<Long> createTask(TaskCreateRequest request);

    /**
     * 更新处理中的任务信息
     *
     * @param request 更新请求
     * @return 更新的记录数
     */
    TaskResponse<Integer> updateProcessingTaskById(TaskUpdateRequest request);

    /**
     * 针对正在处理中的任务，根据需要更新任务的账号<code>accountNo</code>和网站标识<code>website</code>
     * <p>
     * 注意：如果账号已存在，则不更新账号信息
     * </p>
     *
     * @param taskId 任务ID
     * @param accountNo 账号
     * @param website 网站标识
     * @return 更新结果{@link TaskUpdateStatusDTO}
     */
    TaskResponse<TaskUpdateStatusDTO> updateAccountNoAndWebsiteIfNeedWhenProcessing(Long taskId, String accountNo, String website);

    /**
     * 针对正在处理中的任务，更新任务的账号<code>accountNo</code>和网站标识<code>website</code>
     *
     * @param taskId 任务ID
     * @param accountNo 账号
     * @param website 网站标识
     * @return 更新结果{@link TaskUpdateStatusDTO}
     */
    TaskResponse<TaskUpdateStatusDTO> updateAccountNoAndWebsiteWhenProcessing(Long taskId, String accountNo, String website);

    /**
     * 更新任务完成时的任务状态，比如：成功，失败或取消
     *
     * @param taskId 任务ID
     * @param status 任务状态，比如：成功，失败或取消
     * @return stepCode when the task was cancel or failed, otherwise return null.
     */
    TaskResponse<String> updateStatusIfDone(Long taskId, Byte status);

    /**
     * 取消任务
     *
     * @param taskId 任务ID
     * @return Void
     */
    TaskResponse<Void> cancelTask(Long taskId);

    /**
     * 结束任务并发送监控消息
     * 
     * @param taskId 任务ID
     * @param logList 任务结束状态日志
     * @return Void
     */
    TaskResponse<Void> completeTaskAndMonitoring(Long taskId, List<TaskStepLogRequest> logList);

    /**
     * 根据任务ID查询该任务对应的商户信息
     * 
     * @param taskId 任务ID
     * @return 任务对应的商户信息
     */
    TaskResponse<TaskingMerchantBaseDTO> queryTaskingMerchantByTaskId(@NotNull Long taskId);
}

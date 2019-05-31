package com.treefinance.saas.taskcenter.biz.service.directive.process;

import com.google.common.base.Stopwatch;
import com.treefinance.saas.taskcenter.biz.service.TaskLogService;
import com.treefinance.saas.taskcenter.service.TaskNextDirectiveService;
import com.treefinance.saas.taskcenter.biz.service.TaskService;
import com.treefinance.saas.taskcenter.biz.service.directive.process.interceptor.ProcessorInterceptorChain;
import com.treefinance.saas.taskcenter.common.enums.ETaskAttribute;
import com.treefinance.saas.taskcenter.service.TaskAttributeService;
import com.treefinance.saas.taskcenter.service.domain.AttributedTaskInfo;
import com.treefinance.toolkit.util.Assert;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * @author Jerry
 * @date 2019/4/19
 */
public abstract class AbstractDirectiveProcessor implements DirectiveProcessor {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    protected TaskService taskService;
    @Autowired
    protected TaskLogService taskLogService;
    @Autowired
    protected TaskAttributeService taskAttributeService;
    @Autowired
    protected TaskNextDirectiveService taskNextDirectiveService;
    @Autowired
    private ProcessorInterceptorChain interceptorChain;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void process(DirectiveContext context) {
        Stopwatch stopwatch = Stopwatch.createStarted();
        try {
            // 检查DirectiveContext是否合法
            validate(context);
            // 查询任务信息
            AttributedTaskInfo task = taskService.getAttributedTaskInfo(context.getTaskId(), ETaskAttribute.SOURCE_ID.getAttribute());
            if (task == null) {
                throw new IllegalStateException("Task not found! - taskId: " + context.getTaskId());
            }
            // 检查任务是否已完成
            if (taskService.isCompleted(task.getStatus())) {
                logger.warn("Skip completed task when processing directive! - directive-context: {}", context);
                return;
            }
            // DirectiveContext设置task
            context.setTask(task);

            try {
                // 处理指令
                this.doProcess(context);
            } finally {
                interceptorChain.applyAfterCompletion(context);
            }
        } finally {
            logger.info("Directive processing completed! >> cost: {}, directive-context: {}", stopwatch.toString(), context);
        }
    }

    /**
     * 检查DirectiveContext是否合法
     *
     * @param context 指令信息上下文
     */
    protected void validate(DirectiveContext context) {
        Assert.notNull(context, "Empty directive context!");
        Assert.notNull(context.getDirective(), "Invalid directive context!");
        Assert.notNull(context.getTaskId(), "Not found 'taskId' in directive context!");
    }

    /**
     * do processing when received given directive
     *
     * @param context 指令信息上下文
     */
    protected abstract void doProcess(DirectiveContext context);

    /**
     * 保存任务日志
     */
    protected void saveTaskLog(Long taskId, String msg, String errorMsg) {
        try {
            taskLogService.insertTaskLog(taskId, msg, new Date(), StringUtils.substring(errorMsg, 0, 1000));
        } catch (Exception e) {
            logger.warn("保存任务日志失败！- taskId: {}, msg: {}", taskId, msg, e);
        }
    }
}

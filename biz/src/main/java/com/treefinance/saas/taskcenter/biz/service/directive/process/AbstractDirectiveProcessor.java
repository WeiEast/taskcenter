package com.treefinance.saas.taskcenter.biz.service.directive.process;

import com.alibaba.fastjson.JSON;
import com.treefinance.saas.taskcenter.biz.service.TaskNextDirectiveService;
import com.treefinance.saas.taskcenter.biz.service.TaskService;
import com.treefinance.saas.taskcenter.context.enums.EDirective;
import com.treefinance.saas.taskcenter.context.enums.ETaskAttribute;
import com.treefinance.saas.taskcenter.context.enums.ETaskStatus;
import com.treefinance.saas.taskcenter.dto.DirectiveDTO;
import com.treefinance.saas.taskcenter.service.AccountNoService;
import com.treefinance.saas.taskcenter.service.TaskAttributeService;
import com.treefinance.saas.taskcenter.service.domain.AttributedTaskInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by yh-treefinance on 2017/7/5.
 */
public abstract class AbstractDirectiveProcessor extends CallbackableDirectiveProcessor implements DirectiveProcessor {
    /**
     * logger
     */
    protected final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    protected TaskService taskService;
    @Autowired
    protected  TaskAttributeService taskAttributeService;
    @Autowired
    protected TaskNextDirectiveService taskNextDirectiveService;
    @Autowired
    private AccountNoService accountNoService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void process(DirectiveDTO directiveDTO) {
        long start = System.currentTimeMillis();
        if (directiveDTO == null || directiveDTO.getTaskId() == null) {
            logger.error("handle directive error : directive or taskId is null, directive={}", JSON.toJSONString(directiveDTO));
            return;
        }
        Long taskId = directiveDTO.getTaskId();
        // 1.转化为指令
        String directiveName = directiveDTO.getDirective();
        EDirective directive = EDirective.directiveOf(directiveName);
        if (directive == null) {
            logger.error("handle directive error : no support the directive of {}, directive={}", directiveName, JSON.toJSONString(directiveDTO));
            return;
        }
        // 2.初始化任务详细
        AttributedTaskInfo task = directiveDTO.getTask();
        if (task == null) {
            task = taskService.getAttributedTaskInfo(taskId, ETaskAttribute.SOURCE_ID.getAttribute());
            if (task == null) {
                throw new IllegalStateException("Task not found! - taskId: " + taskId);
            }
            directiveDTO.setTask(task);
        }
        // 3.任务是否是已经完成
        Byte taskStatus = task.getStatus();
        if (ETaskStatus.CANCEL.getStatus().equals(taskStatus) || ETaskStatus.SUCCESS.getStatus().equals(taskStatus) || ETaskStatus.FAIL.getStatus().equals(taskStatus)) {
            logger.info("handle directive error : the task id={} is completed: directive={}", taskId, JSON.toJSONString(directiveDTO));
            return;
        }
        // 4.处理指令
        try {
            this.doProcess(directive, directiveDTO);
        } finally {
            accountNoService.saveAccountNoIfAbsent(taskId);
            taskNextDirectiveService.insertAndCacheNextDirective(taskId, directiveDTO);
            logger.info("process directive completed  cost {} ms : directive={}", System.currentTimeMillis() - start, JSON.toJSONString(directiveDTO));
        }
    }

    /**
     * 处理指令
     *
     * @param directive
     * @param directiveDTO
     */
    protected abstract void doProcess(EDirective directive, DirectiveDTO directiveDTO);

}

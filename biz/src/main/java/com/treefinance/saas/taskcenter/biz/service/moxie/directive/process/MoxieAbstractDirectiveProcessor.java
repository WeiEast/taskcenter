package com.treefinance.saas.taskcenter.biz.service.moxie.directive.process;

import com.alibaba.fastjson.JSON;
import com.treefinance.saas.taskcenter.biz.service.AppLicenseService;
import com.treefinance.saas.taskcenter.biz.service.TaskAttributeService;
import com.treefinance.saas.taskcenter.biz.service.TaskNextDirectiveService;
import com.treefinance.saas.taskcenter.biz.service.TaskService;
import com.treefinance.saas.taskcenter.biz.service.directive.process.CallbackableDirectiveProcessor;
import com.treefinance.saas.taskcenter.common.enums.ETaskAttribute;
import com.treefinance.saas.taskcenter.common.enums.ETaskStatus;
import com.treefinance.saas.taskcenter.common.enums.moxie.EMoxieDirective;
import com.treefinance.saas.taskcenter.common.model.dto.TaskDTO;
import com.treefinance.saas.taskcenter.common.model.moxie.MoxieDirectiveDTO;
import com.treefinance.saas.taskcenter.common.utils.JsonUtils;
import com.treefinance.saas.taskcenter.dao.entity.TaskAttribute;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

public abstract class MoxieAbstractDirectiveProcessor extends CallbackableDirectiveProcessor implements MoxieDirectiveProcessor {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    protected TaskService taskService;
    @Autowired
    protected TaskNextDirectiveService taskNextDirectiveService;
    @Autowired
    protected TaskAttributeService taskAttributeService;
    @Autowired
    protected AppLicenseService appLicenseService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void process(MoxieDirectiveDTO directiveDTO) {
        long start = System.currentTimeMillis();
        if (directiveDTO == null) {
            logger.error("handle moxie directive error : directive is null");
            return;
        }
        if (directiveDTO.getTaskId() == null && StringUtils.isBlank(directiveDTO.getMoxieTaskId())) {
            logger.error("handle moxie directive error : taskId and moxieTaskId are null");
            return;
        }
        // 1.转化为指令
        String directiveName = directiveDTO.getDirective();
        EMoxieDirective directive = EMoxieDirective.directiveOf(directiveName);
        if (directive == null) {
            logger.error("handle moxie directive error : no support the directive of {}, directive={}", directiveName, JsonUtils.toJsonString(directiveDTO));
            return;
        }
        // 2.初始化任务详细
        Long taskId = directiveDTO.getTaskId();
        if (taskId == null) {
            String moxieTaskId = directiveDTO.getMoxieTaskId();
            TaskAttribute taskAttribute = taskAttributeService.findByNameAndValue(ETaskAttribute.FUND_MOXIE_TASKID.getAttribute(), moxieTaskId, false);
            if (taskAttribute == null) {
                logger.error("handle moxie directive error : moxieTaskId={} doesn't have taskId matched in task_attribute", moxieTaskId);
                return;
            }
            taskId = taskAttribute.getTaskId();
        }
        TaskDTO taskDTO = directiveDTO.getTask();
        if (taskDTO == null) {
            taskDTO = taskService.getById(taskId);
            if (taskDTO == null) {
                logger.error("handle moxie directive error : taskId={} is not exists, directive={}", taskId, JsonUtils.toJsonString(directiveDTO));
                return;
            }
            directiveDTO.setTask(taskDTO);
        }
        // 3.任务是否是已经完成
        Byte taskStatus = taskDTO.getStatus();
        if (ETaskStatus.CANCEL.getStatus().equals(taskStatus)
                || ETaskStatus.SUCCESS.getStatus().equals(taskStatus)
                || ETaskStatus.FAIL.getStatus().equals(taskStatus)) {
            logger.info("handle moxie directive error : the task id={} is completed: directive={}", taskId, JsonUtils.toJsonString(directiveDTO));
            return;
        }
        // 4.处理指令
        try {
            this.doProcess(directive, directiveDTO);
        } finally {
            taskNextDirectiveService.insertAndCacheNextDirective(taskId, directiveDTO);
            logger.info("handle moxie directive completed  cost {} ms : directive={}", System.currentTimeMillis() - start, JSON.toJSONString(directiveDTO));
        }
    }


    protected <T> T ifNull(T value, T defaultValue) {
        return value == null ? defaultValue : value;
    }


    /**
     * 处理指令
     *
     * @param directive
     * @param directiveDTO
     */
    protected abstract void doProcess(EMoxieDirective directive, MoxieDirectiveDTO directiveDTO);


}
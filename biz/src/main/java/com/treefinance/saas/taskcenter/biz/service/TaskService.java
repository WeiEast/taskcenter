package com.treefinance.saas.taskcenter.biz.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.treefinance.basicservice.security.crypto.facade.EncryptionIntensityEnum;
import com.treefinance.basicservice.security.crypto.facade.ISecurityCryptoService;
import com.treefinance.commonservice.uid.UidGenerator;
import com.treefinance.saas.assistant.model.Constants;
import com.treefinance.saas.taskcenter.biz.service.directive.DirectiveService;
import com.treefinance.saas.taskcenter.biz.utils.CommonUtils;
import com.treefinance.saas.taskcenter.biz.utils.DataConverterUtils;
import com.treefinance.saas.taskcenter.common.enums.EDirective;
import com.treefinance.saas.taskcenter.common.enums.ETaskAttribute;
import com.treefinance.saas.taskcenter.common.enums.ETaskStatus;
import com.treefinance.saas.taskcenter.common.enums.ETaskStep;
import com.treefinance.saas.taskcenter.common.model.dto.DirectiveDTO;
import com.treefinance.saas.taskcenter.common.model.dto.TaskDTO;
import com.treefinance.saas.taskcenter.dao.entity.Task;
import com.treefinance.saas.taskcenter.dao.entity.TaskCriteria;
import com.treefinance.saas.taskcenter.dao.entity.TaskLog;
import com.treefinance.saas.taskcenter.dao.mapper.TaskMapper;
import com.treefinance.saas.taskcenter.facade.request.TaskCreateRequest;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.ValidationException;
import java.util.Map;

/**
 * @author haojiahong
 * @date 2018/9/21
 */
@Service
public class TaskService {
    protected final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private TaskAttributeService taskAttributeService;
    @Autowired
    private ISecurityCryptoService securityCryptoService;
    @Autowired
    private TaskMapper taskMapper;
    @Autowired
    private TaskLogService taskLogService;
    @Autowired
    private DirectiveService directiveService;

    /**
     * 创建任务
     *
     * @param taskCreateRequest
     */
    @Transactional
    public Long createTask(TaskCreateRequest taskCreateRequest) {
        long id = UidGenerator.getId();
        Task task = new Task();
        task.setUniqueId(taskCreateRequest.getUniqueId());
        task.setAppId(taskCreateRequest.getAppId());
        task.setBizType(taskCreateRequest.getBizType());
        task.setStatus((byte) 0);
        if (StringUtils.isNotBlank(taskCreateRequest.getWebsite())) {
            task.setWebSite(taskCreateRequest.getWebsite());
        }
        task.setId(id);
        task.setSaasEnv(Byte.valueOf(Constants.SAAS_ENV_VALUE));
        taskMapper.insertSelective(task);
        if (StringUtils.isNotBlank(taskCreateRequest.getExtra())) {
            JSONObject jsonObject = JSON.parseObject(taskCreateRequest.getExtra());
            if (MapUtils.isNotEmpty(jsonObject)) {
                setAttribute(id, jsonObject);
            }
        }
        if (StringUtils.isNotEmpty(taskCreateRequest.getSource())) {
            taskAttributeService.insert(id, ETaskAttribute.SOURCE_TYPE.getAttribute(), taskCreateRequest.getSource());
        }
        // 记录创建日志
        taskLogService.logCreateTask(id);
        return id;
    }


    private void setAttribute(Long taskId, Map map) {
        String mobileAttribute = ETaskAttribute.MOBILE.getAttribute();
        String nameAttribute = ETaskAttribute.NAME.getAttribute();
        String idCardAttribute = ETaskAttribute.ID_CARD.getAttribute();
        String mobile = map.get(mobileAttribute) == null ? "" : String.valueOf(map.get(mobileAttribute));
        if (StringUtils.isNotBlank(mobile)) {
            boolean b = CommonUtils.regexMatch(mobile, "^1(3|4|5|6|7|8|9)[0-9]\\d{8}$");
            if (!b) {
                throw new ValidationException(String.format("the mobile number is illegal! mobile=%s", mobile));
            }
            String value = securityCryptoService.encrypt(mobile, EncryptionIntensityEnum.NORMAL);
            taskAttributeService.insert(taskId, mobileAttribute, value);
        }
        String name = map.get(nameAttribute) == null ? "" : String.valueOf(map.get(nameAttribute));
        if (StringUtils.isNotBlank(name)) {
            String value = securityCryptoService.encrypt(name, EncryptionIntensityEnum.NORMAL);
            taskAttributeService.insert(taskId, nameAttribute, value);
        }
        String idCard = map.get(idCardAttribute) == null ? "" : String.valueOf(map.get(idCardAttribute));
        if (StringUtils.isNotBlank(idCard)) {
            String value = securityCryptoService.encrypt(idCard, EncryptionIntensityEnum.NORMAL);
            taskAttributeService.insert(taskId, idCardAttribute, value);
        }
    }

    public TaskDTO getById(Long taskId) {
        Task task = taskMapper.selectByPrimaryKey(taskId);
        if (task == null) {
            return null;
        }
        TaskDTO result = DataConverterUtils.convert(task, TaskDTO.class);
        return result;
    }


    /**
     * 任务是否完成
     *
     * @param taskid
     * @return
     */
    public boolean isTaskCompleted(Long taskid) {
        if (taskid == null) {
            return false;
        }
        Task existTask = taskMapper.selectByPrimaryKey(taskid);
        if (existTask == null) {
            return false;
        }
        Byte status = existTask.getStatus();
        if (ETaskStatus.CANCEL.getStatus().equals(status)
                || ETaskStatus.FAIL.getStatus().equals(status)
                || ETaskStatus.SUCCESS.getStatus().equals(status)) {
            return true;
        }
        return false;
    }


    /**
     * 正常流程下取消任务
     *
     * @param taskId 任务id
     */
    public void cancelTask(Long taskId) {
        logger.info("取消任务 : taskId={} ", taskId);
        Task existTask = taskMapper.selectByPrimaryKey(taskId);
        if (existTask != null && existTask.getStatus() == 0) {
            logger.info("取消正在执行任务 : taskId={} ", taskId);
            DirectiveDTO cancelDirective = new DirectiveDTO();
            cancelDirective.setTaskId(taskId);
            cancelDirective.setDirective(EDirective.TASK_CANCEL.getText());
            directiveService.process(cancelDirective);
        }
    }


    @Transactional
    public String cancelTaskWithStep(Long taskId) {

        Task task = new Task();
        task.setId(taskId);
        task.setStatus(ETaskStatus.CANCEL.getStatus());
        TaskLog taskLog = taskLogService.queryLastestErrorLog(taskId);
        if (taskLog != null) {
            task.setStepCode(taskLog.getStepCode());
        } else {
            logger.error("更新任务状态为取消时,未查询到取消任务日志信息 taskId={}", taskId);
        }
        updateUnfinishedTask(task);
        // 取消任务
        taskLogService.logCancleTask(taskId);
        return task.getStepCode();

    }


    /**
     * 更新未完成任务
     *
     * @param task
     * @return
     */
    private int updateUnfinishedTask(Task task) {
        TaskCriteria taskCriteria = new TaskCriteria();
        taskCriteria.createCriteria().andIdEqualTo(task.getId())
                .andStatusNotIn(Lists.newArrayList(ETaskStatus.CANCEL.getStatus(),
                        ETaskStatus.SUCCESS.getStatus(), ETaskStatus.FAIL.getStatus()));
        return taskMapper.updateByExampleSelective(task, taskCriteria);
    }

    @Transactional
    public String failTaskWithStep(Long taskId) {
        TaskCriteria taskCriteria = new TaskCriteria();
        taskCriteria.createCriteria().andIdEqualTo(taskId).
                andStatusNotIn(Lists.newArrayList(ETaskStatus.CANCEL.getStatus(),
                        ETaskStatus.SUCCESS.getStatus(), ETaskStatus.FAIL.getStatus()));
        ;
        Task task = new Task();
        task.setId(taskId);
        task.setStatus(ETaskStatus.FAIL.getStatus());
        TaskLog taskLog = taskLogService.queryLastestErrorLog(taskId);
        if (taskLog != null) {
            task.setStepCode(taskLog.getStepCode());
        } else {
            logger.error("更新任务状态为失败时,未查询到失败任务日志信息 taskId={}", taskId);
        }
        taskMapper.updateByExampleSelective(task, taskCriteria);
        //如果任务是超时导致的失败,则不记录任务失败日志了
        if (!ETaskStep.TASK_TIMEOUT.getStepCode().equals(task.getStepCode())) {
            taskLogService.logFailTask(taskId);
        }
        return task.getStepCode();
    }


    @Transactional
    public String updateTaskStatusWithStep(Long taskId, Byte status) {
        if (ETaskStatus.SUCCESS.getStatus().equals(status)) {
            Task task = new Task();
            task.setId(taskId);
            task.setStatus(status);
            updateUnfinishedTask(task);
            taskLogService.logSuccessTask(taskId);
            return null;
        }
        if (ETaskStatus.FAIL.getStatus().equals(status)) {
            return this.failTaskWithStep(taskId);
        }
        return null;
    }

    /**
     * 更改任务状态
     *
     * @param taskId
     * @param status
     * @return
     */
    public int updateTaskStatus(Long taskId, Byte status) {
        Task task = new Task();
        task.setId(taskId);
        task.setStatus(status);

        return updateUnfinishedTask(task);
    }
}

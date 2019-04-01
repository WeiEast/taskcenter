package com.treefinance.saas.taskcenter.biz.service.moxie;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Maps;
import com.treefinance.saas.grapserver.facade.service.FundMoxieFacade;
import com.treefinance.saas.knife.result.SaasResult;
import com.treefinance.saas.processor.thirdparty.facade.fund.FundService;
import com.treefinance.saas.taskcenter.biz.service.TaskAttributeService;
import com.treefinance.saas.taskcenter.biz.service.TaskLogService;
import com.treefinance.saas.taskcenter.biz.service.impl.TaskServiceImpl;
import com.treefinance.saas.taskcenter.biz.service.moxie.directive.MoxieDirectiveService;
import com.treefinance.saas.taskcenter.context.enums.ETaskAttribute;
import com.treefinance.saas.taskcenter.context.enums.ETaskStatus;
import com.treefinance.saas.taskcenter.context.enums.ETaskStep;
import com.treefinance.saas.taskcenter.context.enums.moxie.EMoxieDirective;
import com.treefinance.saas.taskcenter.dao.entity.TaskAttribute;
import com.treefinance.saas.taskcenter.dto.TaskDTO;
import com.treefinance.saas.taskcenter.dto.moxie.MoxieDirectiveDTO;
import com.treefinance.saas.taskcenter.dto.moxie.MoxieTaskEventNoticeDTO;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Map;

/**
 * Created by haojiahong on 2017/9/15.
 */
@Service
public class MoxieBusinessService {
    protected final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private TaskLogService taskLogService;
    @Autowired
    private MoxieDirectiveService moxieDirectiveService;
    @Autowired
    private TaskAttributeService taskAttributeService;
    @Autowired
    private FundService fundService;
    @Autowired
    private TaskServiceImpl taskService;
    @Autowired
    private FundMoxieFacade fundMoxieFacade;

    /**
     * 魔蝎任务采集失败业务处理
     *
     * @param eventNoticeDTO
     */
    public void grabFail(MoxieTaskEventNoticeDTO eventNoticeDTO) {
        String moxieTaskId = eventNoticeDTO.getMoxieTaskId();
        String message = eventNoticeDTO.getMessage();
        if (StringUtils.isBlank(moxieTaskId)) {
            logger.error("handle moxie business error: moxieTaskId={} is null", moxieTaskId);
            return;
        }
        TaskAttribute taskAttribute = taskAttributeService.findByNameAndValue(ETaskAttribute.FUND_MOXIE_TASKID.getAttribute(), moxieTaskId, false);
        if (taskAttribute == null) {
            logger.error("handle moxie business error: moxieTaskId={} doesn't have taskId matched in task_attribute", moxieTaskId);
            return;
        }
        long taskId = taskAttribute.getTaskId();
        // 任务已经完成,不再继续后续处理.(当任务超时时,会发生魔蝎回调接口重试)
        boolean flag = isTaskDone(taskId);
        if (flag) {
            return;
        }
        // 1.记录采集失败日志
        taskLogService.insertTaskLog(taskId, ETaskStep.CRAWL_FAIL.getText(), new Date(), message);
        // 2.发送任务失败指令
        MoxieDirectiveDTO directiveDTO = new MoxieDirectiveDTO();
        directiveDTO.setDirective(EMoxieDirective.TASK_FAIL.getText());
        Map<String, Object> map = Maps.newHashMap();
        map.put("taskErrorMsg", "爬数失败");
        directiveDTO.setRemark(JSON.toJSONString(map));
        directiveDTO.setTaskId(taskId);
        directiveDTO.setMoxieTaskId(moxieTaskId);
        moxieDirectiveService.process(directiveDTO);

    }

    public void loginSuccess(MoxieTaskEventNoticeDTO eventNoticeDTO) {
        MoxieDirectiveDTO directiveDTO = new MoxieDirectiveDTO();
        directiveDTO.setMoxieTaskId(eventNoticeDTO.getMoxieTaskId());
        directiveDTO.setDirective(EMoxieDirective.LOGIN_SUCCESS.getText());
        moxieDirectiveService.process(directiveDTO);
    }

    public void loginFail(MoxieTaskEventNoticeDTO eventNoticeDTO) {
        MoxieDirectiveDTO directiveDTO = new MoxieDirectiveDTO();
        directiveDTO.setMoxieTaskId(eventNoticeDTO.getMoxieTaskId());
        directiveDTO.setDirective(EMoxieDirective.LOGIN_FAIL.getText());
        directiveDTO.setRemark(eventNoticeDTO.getMessage());
        moxieDirectiveService.process(directiveDTO);
    }

    /**
     * 魔蝎账单通知业务处理
     *
     * @param eventNoticeDTO
     */
    @Transactional(rollbackFor = Exception.class)
    public void bill(MoxieTaskEventNoticeDTO eventNoticeDTO) {
        String moxieTaskId = eventNoticeDTO.getMoxieTaskId();
        if (StringUtils.isBlank(moxieTaskId)) {
            logger.error("handle moxie business error: moxieTaskId={} is null", moxieTaskId);
            return;
        }
        TaskAttribute taskAttribute = taskAttributeService.findByNameAndValue(ETaskAttribute.FUND_MOXIE_TASKID.getAttribute(), moxieTaskId, false);
        if (taskAttribute == null) {
            logger.error("handle moxie business error: moxieTaskId={} doesn't have taskId matched in task_attribute", moxieTaskId);
            return;
        }
        long taskId = taskAttribute.getTaskId();

        // 任务已经完成,不再继续后续处理.(当任务超时时,会发生魔蝎回调接口重试)
        boolean flag = isTaskDone(taskId);
        if (flag) {
            return;
        }

        // 获取魔蝎数据,调用洗数,传递账单数据
        Boolean result = true;
        String message = null;
        String processResult = null;
        try {
            processResult = this.billAndProcess(taskId, moxieTaskId);
        } catch (Exception e) {
            logger.error("handle moxie business error:bill and process fail.taskId={},moxieTaskId={}", taskId, moxieTaskId, e);
            result = false;
            message = e.getMessage();
        }
        // 3.根据洗数返回结果,发送任务成功或失败指令
        if (result) {
            MoxieDirectiveDTO directiveDTO = new MoxieDirectiveDTO();
            directiveDTO.setMoxieTaskId(moxieTaskId);
            directiveDTO.setTaskId(taskId);
            directiveDTO.setDirective(EMoxieDirective.TASK_SUCCESS.getText());
            directiveDTO.setRemark(processResult);
            moxieDirectiveService.process(directiveDTO);
        } else {
            MoxieDirectiveDTO directiveDTO = new MoxieDirectiveDTO();
            directiveDTO.setMoxieTaskId(moxieTaskId);
            directiveDTO.setTaskId(taskId);
            directiveDTO.setDirective(EMoxieDirective.TASK_FAIL.getText());
            Map<String, Object> map = Maps.newHashMap();
            map.put("taskErrorMsg", message);
            directiveDTO.setRemark(JSON.toJSONString(map));
            moxieDirectiveService.process(directiveDTO);
        }
    }

    /**
     * 任务是否已经结束(即:任务是否已经为成功,失败或取消)
     *
     * @param taskId
     * @return
     */
    private boolean isTaskDone(long taskId) {
        TaskDTO task = taskService.getById(taskId);
        if (task == null) {
            logger.error("taskId={}不存在", taskId);
            return true;
        }
        Byte taskStatus = task.getStatus();
        if (ETaskStatus.CANCEL.getStatus().equals(taskStatus) || ETaskStatus.SUCCESS.getStatus().equals(taskStatus) || ETaskStatus.FAIL.getStatus().equals(taskStatus)) {
            logger.info("taskId={}任务已经结束,魔蝎后续重试回调不再处理", taskId);
            return true;
        }
        return false;
    }

    private String billAndProcess(Long taskId, String moxieTaskId) throws Exception {
        String moxieResult = null;
        try {
            SaasResult<String> result = fundMoxieFacade.queryFundsEx(moxieTaskId);
            moxieResult = result.getData();
            // 记录抓取日志
            taskLogService.insertTaskLog(taskId, ETaskStep.CRAWL_SUCCESS.getText(), new Date(), null);
            taskLogService.insertTaskLog(taskId, ETaskStep.CRAWL_COMPLETE.getText(), new Date(), null);
            logger.info("handle moxie business moxieResult,taskId={},moxieTaskId={},result={}", taskId, moxieTaskId, moxieResult);
        } catch (Exception e) {
            logger.error("handle moxie business error:bill fail", e);
            taskLogService.insertTaskLog(taskId, ETaskStep.CRAWL_FAIL.getText(), new Date(), e.getMessage());
            throw new Exception("获取公积金信息失败");
        }
        try {
            String processResult = fundService.fund(taskId, moxieResult);
            // 记录数据保存日志
            taskLogService.insertTaskLog(taskId, ETaskStep.DATA_SAVE_SUCCESS.getText(), new Date(), null);
            logger.info("handle moxie business processResult,taskId={},moxieTaskId={},result={}", taskId, moxieTaskId, processResult);
            return processResult;
        } catch (Exception e) {
            logger.error("handle moxie business error:process fail", e);
            taskLogService.insertTaskLog(taskId, ETaskStep.DATA_SAVE_FAIL.getText(), new Date(), e.getMessage());
            throw new Exception("洗数失败");
        }
    }

}

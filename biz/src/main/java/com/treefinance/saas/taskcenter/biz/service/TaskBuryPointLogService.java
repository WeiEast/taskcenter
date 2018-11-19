package com.treefinance.saas.taskcenter.biz.service;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.treefinance.basicservice.security.crypto.facade.EncryptionIntensityEnum;
import com.treefinance.basicservice.security.crypto.facade.ISecurityCryptoService;
import com.treefinance.commonservice.uid.UidService;
import com.treefinance.saas.taskcenter.dao.entity.TaskBuryPointLog;
import com.treefinance.saas.taskcenter.dao.entity.TaskBuryPointLogCriteria;
import com.treefinance.saas.taskcenter.dao.entity.TaskOperatorMaintainUserLog;
import com.treefinance.saas.taskcenter.dao.mapper.TaskBuryPointLogMapper;
import com.treefinance.saas.taskcenter.dao.mapper.TaskOperatorMaintainUserLogMapper;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by haojiahong on 2017/8/17.
 */
@Service
public class TaskBuryPointLogService {

    private static final Logger logger = LoggerFactory.getLogger(TaskBuryPointLogService.class);

    private final ConcurrentLinkedQueue<TaskBuryPointLog> logQueue = new ConcurrentLinkedQueue<>();

    @Autowired
    protected TaskBuryPointLogMapper taskBuryPointLogMapper;
    @Autowired
    private TaskOperatorMaintainUserLogMapper taskOperatorMaintainUserLogMapper;
    @Autowired
    private ISecurityCryptoService iSecurityCryptoService;
    @Autowired
    private UidService uidService;


    @Scheduled(fixedRate = 1000)
    public void insert() {
        List<TaskBuryPointLog> list = Lists.newArrayList();
        while (!logQueue.isEmpty()) {
            TaskBuryPointLog log = logQueue.poll();
            if (log != null) {
                list.add(log);
            }
        }
        if (CollectionUtils.isEmpty(list)) {
            return;
        }
        try {
            for (List<TaskBuryPointLog> logList : Lists.partition(list, 100)) {
                taskBuryPointLogMapper.batchInsert(logList);
            }
            logger.debug("batchInsert taskBuryPointLog: list={}", JSON.toJSONString(list));
        } catch (Exception e) {
            logger.error("batchInsert taskBuryPointLog error: list={}", JSON.toJSONString(list), e);
            // 失败重新放入队列,埋点信息并不是很重要,去掉重试.
//            logQueue.addAll(list);
        }


    }

    public void pushTaskBuryPointLog(Long taskId, String appId, String code) {
        TaskBuryPointLog log = new TaskBuryPointLog();
        log.setId(uidService.getId());
        log.setTaskId(taskId);
        log.setAppId(appId);
        log.setCode(code);
        log.setCreateTime(new Date());
        logQueue.offer(log);
    }

    public List<TaskBuryPointLog> queryTaskBuryPointLogByCode(Long taskId, String... codes) {
        TaskBuryPointLogCriteria criteria = new TaskBuryPointLogCriteria();
        TaskBuryPointLogCriteria.Criteria _criteria = criteria.createCriteria();
        _criteria.andTaskIdEqualTo(taskId);
        if (codes != null && codes.length >= 0) {
            _criteria.andCodeIn(Arrays.asList(codes));
        }
        List<TaskBuryPointLog> list = taskBuryPointLogMapper.selectByExample(criteria);
        if (CollectionUtils.isEmpty(list)) {
            return Lists.newArrayList();
        }
        return list;
    }


    public void logTaskOperatorMaintainUser(Long taskId, String appId, String extra) {
        if (StringUtils.isBlank(extra)) {
            logger.error("运营商正在维护,记录用户信息,传入信息为空extra={}", extra);
            return;
        }
        ObjectMapper objectMapper = new ObjectMapper();
        Map map = null;
        try {
            map = objectMapper.readValue(extra, Map.class);
        } catch (IOException e) {
            logger.error("运营商正在维护,记录用户信息,extra={}解析出错", extra, e);
            e.printStackTrace();
        }
        if (MapUtils.isEmpty(map)) {
            return;
        }
        String mobile = map.get("mobile") == null ? "" : String.valueOf(map.get("mobile"));
        String operatorName = map.get("operatorName") == null ? "" : String.valueOf(map.get("operatorName"));
        if (StringUtils.isBlank(mobile)) {
            logger.error("运营商正在维护,记录用户信息,extra={}中未传入mobile信息", extra);
            return;
        }
        TaskOperatorMaintainUserLog log = new TaskOperatorMaintainUserLog();
        log.setId(uidService.getId());
        log.setTaskId(taskId);
        log.setAppId(appId);
        log.setMobile(iSecurityCryptoService.encrypt(mobile, EncryptionIntensityEnum.NORMAL));
        log.setOperatorName(operatorName);
        taskOperatorMaintainUserLogMapper.insertSelective(log);

    }
}

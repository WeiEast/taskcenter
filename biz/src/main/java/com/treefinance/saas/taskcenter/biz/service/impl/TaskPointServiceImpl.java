package com.treefinance.saas.taskcenter.biz.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.treefinance.commonservice.uid.UidService;
import com.treefinance.saas.taskcenter.biz.service.TaskPointService;
import com.treefinance.saas.taskcenter.context.config.DiamondConfig;
import com.treefinance.saas.taskcenter.context.enums.CodeStepEnum;
import com.treefinance.saas.taskcenter.dao.entity.Task;
import com.treefinance.saas.taskcenter.dao.entity.TaskPoint;
import com.treefinance.saas.taskcenter.dao.mapper.TaskMapper;
import com.treefinance.saas.taskcenter.dao.mapper.TaskPointMapper;
import com.treefinance.saas.taskcenter.facade.request.TaskPointRequest;
import com.treefinance.saas.taskcenter.share.cache.redis.RedisDao;
import com.treefinance.saas.taskcenter.util.HttpClientUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @author 张琰佳
 * @since 8:39 PM 2019/1/24
 */
@Service
public class TaskPointServiceImpl implements TaskPointService {

    private static final Logger logger = LoggerFactory.getLogger(TaskPointService.class);

    @Autowired
    private TaskPointMapper taskPointMapper;
    @Autowired
    private TaskMapper taskMapper;
    @Autowired
    private RedisDao redisDao;
    @Autowired
    private DiamondConfig diamondConfig;
    @Autowired
    private UidService uidService;

    @Override
    public void addTaskPoint(TaskPointRequest taskPointRequest) {
        TaskPoint taskPoint = new TaskPoint();
        BeanUtils.copyProperties(taskPointRequest, taskPoint);
        taskPoint.setOccurTime(new Date());
        String str = redisDao.get("UniqueId_bizType_" + taskPointRequest.getTaskId());
        if (str == null) {
            Task task = taskMapper.selectByPrimaryKey(taskPointRequest.getTaskId());
            String uniqueId = task.getUniqueId();
            redisDao.setEx("UniqueId_bizType_" + taskPointRequest.getTaskId(), task.getUniqueId() + "," + task.getBizType(), 10, TimeUnit.MINUTES);
            taskPoint.setUniqueId(uniqueId);
            taskPoint.setBizType(task.getBizType());
        } else {
            List<String> list = Arrays.asList(str.split(","));
            taskPoint.setUniqueId(list.get(0));
            taskPoint.setBizType((Byte.valueOf(list.get(1))));
        }
        int bizType = taskPoint.getBizType();
        if (taskPoint.getType() == 1) {
            if (bizType == 1) {
                taskPoint.setCode("20" + taskPoint.getCode());
            } else if (bizType == 2) {
                taskPoint.setCode("30" + taskPoint.getCode());
            } else if (bizType == 3) {
                taskPoint.setCode("10" + taskPoint.getCode());
            } else {
                taskPoint.setCode("00" + taskPoint.getCode());
            }
        }
        taskPoint.setStep(CodeStepEnum.getStep(taskPoint.getCode()));
        taskPoint.setSubStep(CodeStepEnum.getSubStep(taskPoint.getCode()));
        taskPoint.setMsg(CodeStepEnum.getMsg(taskPoint.getCode()));
        taskPoint.setId(uidService.getId());
        int i = taskPointMapper.insertSelective(taskPoint);
        if (i == 1) {
            if (bizType == 1 || bizType == 2 || bizType == 3) {
                Map<String, Object> map = new HashMap<>(9);
                map.put("taskId", taskPoint.getTaskId());
                map.put("uniqueId", taskPoint.getUniqueId());
                map.put("type", taskPoint.getType());
                map.put("code", taskPoint.getCode());
                map.put("step", taskPoint.getStep());
                map.put("subStep", taskPoint.getSubStep());
                map.put("msg", taskPoint.getMsg());
                map.put("ip", taskPoint.getIp());
                map.put("occurTime", taskPoint.getOccurTime());
                String result = HttpClientUtils.doPost(diamondConfig.getHttpUrl(), map);
                if (result == null) {
                    logger.error("埋点调用功夫贷返回结果为空，taskId={}", taskPoint.getTaskId());
                } else {
                    JSONObject jsonObject = JSON.parseObject(result);
                    if ((int)jsonObject.get("code") != 0) {
                        logger.error("埋点调用功夫贷返回错误，taskId={}，errorMsg", taskPoint.getTaskId(), jsonObject.get("errorMsg"));
                    }
                }
            }
        }
    }
}

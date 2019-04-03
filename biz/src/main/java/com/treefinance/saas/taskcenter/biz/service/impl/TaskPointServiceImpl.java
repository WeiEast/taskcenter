package com.treefinance.saas.taskcenter.biz.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.treefinance.commonservice.uid.UidService;
import com.treefinance.saas.taskcenter.biz.service.TaskPointService;
import com.treefinance.saas.taskcenter.context.config.DiamondConfig;
import com.treefinance.saas.taskcenter.context.enums.CodeStepEnum;
import com.treefinance.saas.taskcenter.dao.entity.Task;
import com.treefinance.saas.taskcenter.dao.entity.TaskAttribute;
import com.treefinance.saas.taskcenter.dao.entity.TaskAttributeCriteria;
import com.treefinance.saas.taskcenter.dao.entity.TaskPoint;
import com.treefinance.saas.taskcenter.dao.mapper.TaskAttributeMapper;
import com.treefinance.saas.taskcenter.dao.mapper.TaskMapper;
import com.treefinance.saas.taskcenter.dao.mapper.TaskPointMapper;
import com.treefinance.saas.taskcenter.facade.request.TaskPointRequest;
import com.treefinance.saas.taskcenter.share.cache.redis.RedisDao;
import com.treefinance.saas.taskcenter.util.HttpClientUtils;
import com.treefinance.toolkit.util.Objects;
import com.treefinance.toolkit.util.net.NetUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.DateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

/**
 * @author 张琰佳
 * @since 8:39 PM 2019/1/24
 */
@Service
public class TaskPointServiceImpl implements TaskPointService {

    private static final Logger logger = LoggerFactory.getLogger(TaskPointService.class);

    private static final String sourceId = "sourceId";

    @Autowired
    private TaskPointMapper taskPointMapper;
    @Autowired
    private TaskAttributeMapper taskAttributeMapper;
    @Autowired
    private TaskMapper taskMapper;
    @Autowired
    private RedisDao redisDao;
    @Autowired
    private DiamondConfig diamondConfig;
    @Autowired
    private UidService uidService;

    @Override
    public void addTaskPoint(Long taskId, String pointCode) {
        addTaskPoint(taskId, pointCode, NetUtils.getLocalHost());
    }

    @Override
    public void addTaskPoint(Long taskId, String pointCode, String ip) {
        TaskPointRequest taskPointRequest = new TaskPointRequest();
        taskPointRequest.setTaskId(taskId);
        taskPointRequest.setType((byte)1);
        taskPointRequest.setCode(pointCode);
        taskPointRequest.setIp(ip);
        addTaskPoint(taskPointRequest);
    }

    @Override
    public void addTaskPoint(TaskPointRequest taskPointRequest) {
        try {
            TaskPoint taskPoint = new TaskPoint();
            BeanUtils.copyProperties(taskPointRequest, taskPoint);
            taskPoint.setOccurTime(new Date());
            String str = redisDao.get("UniqueId_bizType_appId_sourceId" + taskPointRequest.getTaskId());
            String appId;
            String sourceid = "";
            if (str == null) {

                Task task = taskMapper.selectByPrimaryKey(taskPointRequest.getTaskId());

                TaskAttributeCriteria criteria = new TaskAttributeCriteria();
                criteria.createCriteria().andTaskIdEqualTo(taskPointRequest.getTaskId()).andNameEqualTo(sourceId);
                List<TaskAttribute> taskAttributes = taskAttributeMapper.selectByExample(criteria);
                if (!Objects.isEmpty(taskAttributes)) {
                    sourceid = taskAttributes.get(0).getValue();
                }
                String uniqueId = task.getUniqueId();
                redisDao.setEx("UniqueId_bizType_appId_sourceId" + taskPointRequest.getTaskId(), task.getUniqueId() + "," + task.getBizType() + "," + task.getAppId() + "," + sourceid, 10,
                    TimeUnit.MINUTES);
                taskPoint.setUniqueId(uniqueId);
                taskPoint.setBizType(task.getBizType());
                appId = task.getAppId();
            } else {
                List<String> list = Arrays.asList(str.split(","));
                taskPoint.setUniqueId(list.get(0));
                taskPoint.setBizType((Byte.valueOf(list.get(1))));
                appId = list.get(2);
                sourceid = list.get(3);
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
            taskPoint.setAppId(appId);
            int i = taskPointMapper.insertSelective(taskPoint);
            // String appIds = diamondConfig.getGfdAppId();
            // List<String> list = Arrays.asList(appIds.split(","));
            if (i == 1) {
                if (bizType == 1 || bizType == 2 || bizType == 3) {
                    logger.info("开始封装参数，taskId={}", taskPoint.getTaskId());
                    Map<String, Object> map = new HashMap<>(9);
                    map.put("taskId", taskPoint.getTaskId());
                    map.put("uniqueId", taskPoint.getUniqueId());
                    map.put("type", taskPoint.getType());
                    map.put("code", taskPoint.getCode());
                    map.put("step", taskPoint.getStep());
                    map.put("subStep", taskPoint.getSubStep());
                    map.put("msg", taskPoint.getMsg());
                    map.put("ip", taskPoint.getIp());
                    map.put("appId", appId);
                    map.put("sourceId", sourceid);
                    DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.FULL, DateFormat.FULL);
                    dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
                    map.put("occurTime", dateFormat.format(taskPoint.getOccurTime()));
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
        } catch (Exception e) {
            logger.error("埋点调用功夫贷异常，taskId={}", taskPointRequest.getTaskId(), e);
        }
    }

}

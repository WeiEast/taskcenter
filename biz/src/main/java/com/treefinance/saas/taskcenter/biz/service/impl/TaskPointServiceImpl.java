package com.treefinance.saas.taskcenter.biz.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.treefinance.commonservice.uid.UidService;
import com.treefinance.saas.merchant.facade.request.console.MerchantFunctionRequest;
import com.treefinance.saas.merchant.facade.result.console.MerchantFunctionResult;
import com.treefinance.saas.merchant.facade.result.console.MerchantResult;
import com.treefinance.saas.merchant.facade.service.MerchantFunctionFacade;
import com.treefinance.saas.taskcenter.biz.service.TaskPointService;
import com.treefinance.saas.taskcenter.context.config.DiamondConfig;
import com.treefinance.saas.taskcenter.dao.entity.Task;
import com.treefinance.saas.taskcenter.dao.entity.TaskAttribute;
import com.treefinance.saas.taskcenter.dao.entity.TaskAttributeCriteria;
import com.treefinance.saas.taskcenter.dao.entity.TaskPoint;
import com.treefinance.saas.taskcenter.dao.mapper.TaskAttributeMapper;
import com.treefinance.saas.taskcenter.dao.mapper.TaskMapper;
import com.treefinance.saas.taskcenter.dao.mapper.TaskPointMapper;
import com.treefinance.saas.taskcenter.facade.request.TaskPointRequest;
import com.treefinance.saas.taskcenter.facade.validate.Preconditions;
import com.treefinance.saas.taskcenter.share.cache.redis.RedisDao;
import com.treefinance.saas.taskcenter.util.HttpClientUtils;
import com.treefinance.toolkit.util.Objects;
import com.treefinance.toolkit.util.net.NetUtils;
import org.apache.commons.lang3.StringUtils;
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

import static com.treefinance.saas.taskcenter.context.enums.CodeStepEnum.createSystemTaskPointCode;
import static com.treefinance.saas.taskcenter.context.enums.CodeStepEnum.getMsg;
import static com.treefinance.saas.taskcenter.context.enums.CodeStepEnum.getStep;
import static com.treefinance.saas.taskcenter.context.enums.CodeStepEnum.getSubStep;

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

    @Autowired
    private MerchantFunctionFacade merchantFunctionFacade;

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
            Preconditions.notNull("request", taskPointRequest);
            Preconditions.notBlank("request.code", taskPointRequest.getCode());
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
                if(StringUtils.isNotEmpty(sourceid)) {
                    sourceid = list.get(3);
                }
            }
            int bizType = taskPoint.getBizType();
            if (taskPoint.getType() == 1) {
                taskPoint.setCode(createSystemTaskPointCode(bizType, taskPoint.getCode()));
            }
            taskPoint.setStep(getStep(taskPoint.getCode()));
            taskPoint.setSubStep(getSubStep(taskPoint.getCode()));
            taskPoint.setMsg(getMsg(taskPoint.getCode()));
            taskPoint.setId(uidService.getId());
            taskPoint.setAppId(appId);
            int i = taskPointMapper.insertSelective(taskPoint);
            MerchantFunctionRequest request = new MerchantFunctionRequest();
            request.setAppId(appId);
            MerchantResult<MerchantFunctionResult> merchantResult = merchantFunctionFacade.getMerchantFunctionByAppId(request);
            if (!merchantResult.isSuccess()) {
                logger.error("根据appId获取商户是否通知埋点信息失败,appId={},errorMsg={}", appId, merchantResult.getRetMsg());
            } else {
                success(taskPoint, appId, bizType, i, merchantResult,sourceid);
            }
        } catch (Exception e) {
            logger.error("埋点通知商户异常，taskId={}", taskPointRequest.getTaskId(), e);
        }
    }

    private void success(TaskPoint taskPoint, String appId, int bizType, int i, MerchantResult<MerchantFunctionResult> merchantResult,String sourceid) {
        MerchantFunctionResult merchantFunctionResult = merchantResult.getData();
        if (merchantFunctionResult == null) {
            logger.warn("根据appId没有获取商户是否通知埋点信息，appId={}", appId);
        } else {
            if (i == 1 && merchantFunctionResult.getSync() == 1) {
                if (bizType == 1 || bizType == 2 || bizType == 3) {
                    logger.info("开始封装参数，taskId={}", taskPoint.getTaskId());
                    Map<String, Object> map = getStringObjectMap(taskPoint, appId,sourceid);
                    String result = HttpClientUtils.doPost(merchantFunctionResult.getSyncUrl(), map);
                    if (result == null) {
                        logger.error("埋点通知商户返回结果为空，taskId={},appId={}", taskPoint.getTaskId(), appId);
                    } else {
                        logger.warn("埋点通知商户返回结果，taskId={}，result={}", taskPoint.getTaskId(), result);
                    }
                }
            }
        }
    }

    private Map<String, Object> getStringObjectMap(TaskPoint taskPoint, String appId,String sourceid) {
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
        return map;
    }

}

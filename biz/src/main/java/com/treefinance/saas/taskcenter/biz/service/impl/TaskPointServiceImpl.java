package com.treefinance.saas.taskcenter.biz.service.impl;

import com.treefinance.commonservice.uid.UidService;
import com.treefinance.saas.merchant.facade.request.console.MerchantFunctionRequest;
import com.treefinance.saas.merchant.facade.result.console.MerchantFunctionResult;
import com.treefinance.saas.merchant.facade.result.console.MerchantResult;
import com.treefinance.saas.merchant.facade.service.MerchantFunctionFacade;
import com.treefinance.saas.taskcenter.biz.domain.TaskPointInner;
import com.treefinance.saas.taskcenter.biz.service.TaskPointService;
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
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.DateFormat;
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
    private static final int[] NOTIFY_BIZ_TYPE = {1, 2, 3};

    @Autowired
    private TaskPointMapper taskPointMapper;
    @Autowired
    private TaskAttributeMapper taskAttributeMapper;
    @Autowired
    private TaskMapper taskMapper;
    @Autowired
    private RedisDao redisDao;
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

            TaskPointInner str = (TaskPointInner)redisDao.getObject("UniqueId_bizType_appId_sourceId" + taskPointRequest.getTaskId());
            String appId;
            String sourceid = "";
            // 判断是否读缓存还是取数据库
            if (Objects.isEmpty(str)) {
                TaskPointInner taskPointInner = new TaskPointInner();
                // 获取任务所有信息
                taskPointInner = setTaskPoint(taskPointInner, taskPointRequest.getTaskId());
                // 存入缓存
                redisDao.setObject("UniqueId_bizType_appId_sourceId" + taskPointRequest.getTaskId(), taskPointInner, (long)10, TimeUnit.MINUTES);
                taskPoint.setUniqueId(taskPointInner.getUniqueId());
                taskPoint.setBizType(taskPointInner.getBizType());
                sourceid = taskPointInner.getSourceId();
                appId = taskPointInner.getAppId();
            } else {
                taskPoint.setUniqueId(str.getUniqueId());
                taskPoint.setBizType(str.getBizType());
                appId = str.getAppId();
                if (StringUtils.isNotEmpty(str.getSourceId())) {
                    sourceid = str.getSourceId();
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
            if (i > 0 && supportNotifyBizType(bizType)) {
                MerchantFunctionRequest request = new MerchantFunctionRequest();
                request.setAppId(appId);
                MerchantResult<MerchantFunctionResult> merchantResult = merchantFunctionFacade.getMerchantFunctionByAppId(request);
                if (!merchantResult.isSuccess()) {
                    logger.error("根据appId获取商户是否通知埋点信息失败,appId={},errorMsg={}", appId, merchantResult.getRetMsg());
                } else {
                    success(taskPoint, appId, sourceid, merchantResult.getData());
                }
            }
        } catch (Exception e) {
            logger.error("埋点通知商户异常，taskId={}", taskPointRequest.getTaskId(), e);
        }
    }

    private TaskPointInner setTaskPoint(TaskPointInner taskPointInner, Long taskId) {
        String sourceid = "";
        Task task = taskMapper.selectByPrimaryKey(taskId);
        TaskAttributeCriteria criteria = new TaskAttributeCriteria();
        criteria.createCriteria().andTaskIdEqualTo(taskId).andNameEqualTo(sourceId);
        List<TaskAttribute> taskAttributes = taskAttributeMapper.selectByExample(criteria);
        if (!Objects.isEmpty(taskAttributes)) {
            sourceid = taskAttributes.get(0).getValue();
        }
        taskPointInner.setAppId(task.getAppId());
        taskPointInner.setUniqueId(task.getUniqueId());
        taskPointInner.setBizType(task.getBizType());
        taskPointInner.setSourceId(sourceid);

        return taskPointInner;

    }

    private void success(TaskPoint taskPoint, String appId, String sourceId, MerchantFunctionResult merchantFunctionResult) {
        if (merchantFunctionResult == null) {
            logger.warn("根据appId没有获取商户是否通知埋点信息，appId={}", appId);
        } else {
            if (merchantFunctionResult.getSync() == 1) {
                logger.info("开始封装参数，taskId={}", taskPoint.getTaskId());
                Map<String, Object> map = getStringObjectMap(taskPoint, appId, sourceId);
                String result = HttpClientUtils.doPost(merchantFunctionResult.getSyncUrl(), map);
                if (result == null) {
                    logger.error("埋点通知商户返回结果为空，taskId={},appId={}", taskPoint.getTaskId(), appId);
                } else {
                    logger.warn("埋点通知商户返回结果，taskId={}，result={}", taskPoint.getTaskId(), result);
                }
            }
        }
    }

    private Map<String, Object> getStringObjectMap(TaskPoint taskPoint, String appId, String sourceId) {
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
        map.put("sourceId", sourceId);
        DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.FULL, DateFormat.FULL);
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        map.put("occurTime", dateFormat.format(taskPoint.getOccurTime()));
        return map;
    }

    private boolean supportNotifyBizType(int bizType) {
        if (ArrayUtils.isNotEmpty(NOTIFY_BIZ_TYPE)) {
            for (int type : NOTIFY_BIZ_TYPE) {
                if (type == bizType) {
                    return true;
                }
            }
        }
        return false;
    }
}

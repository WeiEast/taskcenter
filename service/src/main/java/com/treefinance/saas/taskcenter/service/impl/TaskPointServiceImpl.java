/*
 * Copyright © 2015 - 2017 杭州大树网络技术有限公司. All Rights Reserved
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.treefinance.saas.taskcenter.service.impl;

import com.treefinance.saas.taskcenter.common.enums.ETaskAttribute;
import com.treefinance.saas.taskcenter.dao.entity.Task;
import com.treefinance.saas.taskcenter.dao.entity.TaskAttribute;
import com.treefinance.saas.taskcenter.dao.entity.TaskPoint;
import com.treefinance.saas.taskcenter.dao.param.TaskPointInsertParams;
import com.treefinance.saas.taskcenter.dao.repository.TaskAttributeRepository;
import com.treefinance.saas.taskcenter.dao.repository.TaskPointRepository;
import com.treefinance.saas.taskcenter.dao.repository.TaskRepository;
import com.treefinance.saas.taskcenter.interation.manager.MerchantFunctionManager;
import com.treefinance.saas.taskcenter.interation.manager.domain.MerchantFunctionBO;
import com.treefinance.saas.taskcenter.service.TaskPointService;
import com.treefinance.saas.taskcenter.service.param.TaskPointCreateObject;
import com.treefinance.saas.taskcenter.share.cache.redis.RedisDao;
import com.treefinance.saas.taskcenter.util.HttpClientUtils;
import com.treefinance.toolkit.util.net.NetUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Nonnull;

import java.io.Serializable;
import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
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

    private static final int[] NOTIFY_BIZ_TYPE = {1, 2, 3};

    @Autowired
    private TaskRepository taskRepository;
    @Autowired
    private TaskAttributeRepository taskAttributeRepository;
    @Autowired
    private TaskPointRepository taskPointRepository;
    @Autowired
    private RedisDao redisDao;
    @Autowired
    private MerchantFunctionManager merchantFunctionManager;

    @Override
    public void addTaskPoint(Long taskId, String pointCode) {
        String localHost;
        try {
            localHost = NetUtils.getLocalHost();
        } catch (Exception e) {
            logger.warn("获取本机host失败！", e);
            localHost = StringUtils.EMPTY;
        }
        addTaskPoint(taskId, pointCode, localHost);
    }

    @Override
    public void addTaskPoint(Long taskId, String pointCode, String ip) {
        TaskPointCreateObject taskPointRequest = new TaskPointCreateObject();
        taskPointRequest.setTaskId(taskId);
        taskPointRequest.setType((byte)1);
        taskPointRequest.setCode(pointCode);
        taskPointRequest.setIp(ip);
        addTaskPoint(taskPointRequest);
    }

    @Override
    public void addTaskPoint(TaskPointCreateObject createObject) {
        try {
            TaskPointInner info = this.readTaskPointInner(createObject.getTaskId());

            TaskPointInsertParams params = new TaskPointInsertParams();
            BeanUtils.copyProperties(createObject, params);
            params.setOccurTime(new Date());
            params.setAppId(info.getAppId());
            params.setUniqueId(info.getUniqueId());
            params.setBizType(info.getBizType());

            int bizType = params.getBizType();
            if (params.getType() == 1) {
                params.setCode(createSystemTaskPointCode(bizType, params.getCode()));
            }
            params.setStep(getStep(params.getCode()));
            params.setSubStep(getSubStep(params.getCode()));
            params.setMsg(getMsg(params.getCode()));

            TaskPoint taskPoint = taskPointRepository.insert(params);
            if (supportNotifyBizType(bizType)) {
                MerchantFunctionBO function = null;
                try {
                    function = merchantFunctionManager.getMerchantFunctionByAppId(taskPoint.getAppId());
                } catch (Exception e) {
                    logger.error("根据appId获取商户埋点信息同步配置失败!", e);
                }
                if (function != null) {
                    notifyMerchant(function, taskPoint, StringUtils.defaultString(info.getSourceId()));
                }
            }
        } catch (Exception e) {
            logger.error("埋点通知商户异常，taskId={}", createObject.getTaskId(), e);
        }
    }

    private void notifyMerchant(MerchantFunctionBO merchantFunction, TaskPoint taskPoint, String sourceId) {
        if (merchantFunction.getSync() == 1) {
            logger.info("开始封装参数，taskId={}", taskPoint.getTaskId());
            Map<String, Object> map = getStringObjectMap(taskPoint, sourceId);
            String result = HttpClientUtils.doPost(merchantFunction.getSyncUrl(), map);
            if (result == null) {
                logger.error("埋点通知商户返回结果为空，taskId={},appId={}", taskPoint.getTaskId(), taskPoint.getAppId());
            } else {
                logger.warn("埋点通知商户返回结果，taskId={}，result={}", taskPoint.getTaskId(), result);
            }
        }
    }

    private Map<String, Object> getStringObjectMap(TaskPoint taskPoint, String sourceId) {
        Map<String, Object> map = new HashMap<>(11);
        map.put("taskId", taskPoint.getTaskId());
        map.put("uniqueId", taskPoint.getUniqueId());
        map.put("type", taskPoint.getType());
        map.put("code", taskPoint.getCode());
        map.put("step", taskPoint.getStep());
        map.put("subStep", taskPoint.getSubStep());
        map.put("msg", taskPoint.getMsg());
        map.put("ip", taskPoint.getIp());
        map.put("appId", taskPoint.getAppId());
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

    private TaskPointInner readTaskPointInner(@Nonnull Long taskId) {
        TaskPointInner taskPointInner = (TaskPointInner)redisDao.getObject("UniqueId_bizType_appId_sourceId:" + taskId);

        if (taskPointInner == null) {
            Task task = taskRepository.getTaskById(taskId);

            String sourceId = null;
            TaskAttribute attribute = taskAttributeRepository.queryAttributeByTaskIdAndName(taskId, ETaskAttribute.SOURCE_ID.getAttribute());
            if (attribute != null) {
                sourceId = attribute.getValue();
            }

            taskPointInner = new TaskPointInner();
            taskPointInner.setAppId(task.getAppId());
            taskPointInner.setUniqueId(task.getUniqueId());
            taskPointInner.setBizType(task.getBizType());
            taskPointInner.setSourceId(sourceId);

            redisDao.setObject("UniqueId_bizType_appId_sourceId:" + taskId, taskPointInner, (long)10, TimeUnit.MINUTES);
        }

        return taskPointInner;
    }

    private static class TaskPointInner implements Serializable {
        private String uniqueId;
        private Byte bizType;
        private String appId;
        private String sourceId;

        public String getUniqueId() {
            return uniqueId;
        }

        public void setUniqueId(String uniqueId) {
            this.uniqueId = uniqueId;
        }

        public Byte getBizType() {
            return bizType;
        }

        public void setBizType(Byte bizType) {
            this.bizType = bizType;
        }

        public String getAppId() {
            return appId;
        }

        public void setAppId(String appId) {
            this.appId = appId;
        }

        public String getSourceId() {
            return sourceId;
        }

        public void setSourceId(String sourceId) {
            this.sourceId = sourceId;
        }
    }
}

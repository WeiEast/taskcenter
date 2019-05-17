package com.treefinance.saas.taskcenter.biz.service.directive.process;

import com.alibaba.fastjson.JSON;
import com.treefinance.b2b.saas.util.RemoteDataUtils;
import com.treefinance.saas.taskcenter.biz.callback.CallbackResultMonitor;
import com.treefinance.saas.taskcenter.biz.service.MonitorService;
import com.treefinance.saas.taskcenter.biz.service.TaskPointService;
import com.treefinance.saas.taskcenter.biz.service.directive.process.CallbackResponse.CallbackData;
import com.treefinance.saas.taskcenter.common.enums.EBizType;
import com.treefinance.saas.taskcenter.common.enums.EDirective;
import com.treefinance.saas.taskcenter.common.enums.ETaskAttribute;
import com.treefinance.saas.taskcenter.common.enums.ETaskStatus;
import com.treefinance.saas.taskcenter.context.Constants;
import com.treefinance.saas.taskcenter.context.enums.EDataType;
import com.treefinance.saas.taskcenter.context.enums.EGrapStatus;
import com.treefinance.saas.taskcenter.dao.entity.TaskLog;
import com.treefinance.saas.taskcenter.exception.CryptoException;
import com.treefinance.saas.taskcenter.exception.RequestFailedException;
import com.treefinance.saas.taskcenter.interation.manager.domain.CallbackConfigBO;
import com.treefinance.saas.taskcenter.service.AppCallbackConfigService;
import com.treefinance.saas.taskcenter.service.TaskCallbackLogService;
import com.treefinance.saas.taskcenter.service.domain.TaskInfo;
import com.treefinance.saas.taskcenter.service.param.CallbackRecordObject;
import com.treefinance.saas.taskcenter.service.param.CallbackRecordObject.CallbackResult;
import com.treefinance.saas.taskcenter.util.CallbackDataUtils;
import com.treefinance.saas.taskcenter.util.HttpClientUtils;
import com.treefinance.saas.taskcenter.util.SystemUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 支持回调的指令处理
 * 
 * @author yh-treefinance
 * @date 2017/7/11.
 */
public abstract class AbstractCallbackDirectiveProcessor extends AbstractDirectiveProcessor {

    @Autowired
    private AppCallbackConfigService appCallbackConfigService;
    @Autowired
    private CallbackResultMonitor callbackResultMonitor;
    @Autowired
    private MonitorService monitorService;
    @Autowired
    private TaskPointService taskPointService;
    @Autowired
    protected TaskCallbackLogService taskCallbackLogService;


    @Override
    protected void validate(DirectiveContext context) {
        super.validate(context);
        if (!context.supportLicenseManager()) {
            throw new IllegalArgumentException("Not support license manager in directive context!");
        }
    }

    /**
     * 生成回调数据实体，包含uniqueId、taskId、taskStatus、sourceId等信息。
     *
     * @param context 指令处理上下文
     * @return 回调数据实体 {@link CallbackEntity}
     */
    protected CallbackEntity buildCallbackEntity(DirectiveContext context) {
        // 1. 初始化回调数据 并填充uniqueId、taskId、taskStatus、sourceId
        final Map<String, Object> initialAttrs = context.getAttributes();

        CallbackEntity entity = initialAttrs == null ? new CallbackEntity() : new CallbackEntity(initialAttrs);

        entity.setTaskIdIfAbsent(context.getTaskId());
        entity.setUniqueIdIfAbsent(context.getTaskUniqueId());
        entity.computeIfAbsent(ETaskAttribute.SOURCE_ID.getAttribute(), context::getTaskAttributeValue);

        // 此次任务状态：001-抓取成功，002-抓取失败，003-抓取结果为空,004-任务取消
        final Byte status = context.getTaskStatus();
        if (ETaskStatus.FAIL.getStatus().equals(status)) {
            // 任务失败消息
            TaskLog log = taskLogService.queryLastErrorLog(context.getTaskId());
            entity.failure(log != null ? log.getMsg() : EGrapStatus.FAIL.getName());
        } else if (ETaskStatus.CANCEL.getStatus().equals(status)) {
            entity.cancel("用户取消");
        } else if (entity.getCrawlerStatus()) {
            // 此时针对工商无需爬取时处理
            logger.info("工商回调，回调code设置为005，taskId={}", context.getTaskId());
            entity.setStatus(EGrapStatus.NO_NEED_CRAWLER, "");
        } else {
            entity.success();
        }

        // 如果是运营商数据
        if (EBizType.OPERATOR.getCode().equals(context.getBizType())) {
            String groupCodeAttribute = ETaskAttribute.OPERATOR_GROUP_CODE.getAttribute();
            String groupNameAttribute = ETaskAttribute.OPERATOR_GROUP_NAME.getAttribute();

            Map<String, String> attributeMap =
                taskAttributeService.getAttributeMapByTaskIdAndInNames(context.getTaskId(), new String[] {groupCodeAttribute, groupNameAttribute}, false);

            entity.put(groupCodeAttribute, StringUtils.defaultString(attributeMap.get(groupCodeAttribute)));
            entity.put(groupNameAttribute, StringUtils.defaultString(attributeMap.get(groupNameAttribute)));
        }

        logger.info("回调数据生成 >> {}, directive={}", entity, context);
        return entity;
    }

    /**
     * 执行回调(无需预处理)
     *
     * @param context 指令处理上下文
     */
    protected int callback(DirectiveContext context) {
        CallbackEntity callbackEntity = this.buildCallbackEntity(context);

        return callback(callbackEntity, context);
    }

    /**
     * 执行回调
     *
     * @param callbackEntity 回调数据
     * @param context 指令处理上下文
     * @return 0-无需回调，1-回调成功，-1-回调失败
     */
    protected int callback(CallbackEntity callbackEntity, DirectiveContext context) {
        Long taskId = context.getTaskId();

        // 查询回调配置
        List<CallbackConfigBO> configList = appCallbackConfigService.queryConfigsByAppIdAndBizType(context.getAppId(), context.getBizType(), EDataType.MAIN_STREAM);
        logger.info("根据业务类型匹配回调配置结果:taskId={}, configList={}", taskId, configList);
        if (CollectionUtils.isEmpty(configList)) {
            logger.info("callback exit: callback config was empty. context={}", context);
            monitorService.sendTaskCallbackMsgMonitorMessage(context.getTask(), null);
            return 0;
        }

        // 校验是否需要回调
        configList = configList.stream().filter(config -> needCallback(config, context)).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(configList)) {
            logger.info("callback exit: not found available callback config. context={}", context);
            monitorService.sendTaskCallbackMsgMonitorMessage(context.getTask(), null);
            return 0;
        }

        // 埋点-开始回调
        taskPointService.addTaskPoint(taskId, "900401");

        // 执行回调，支持一个任务回调多方
        int failure = 0;
        for (CallbackConfigBO config : configList) {
            try {
                // 执行回调
                doCallback(callbackEntity, config, context);
            } catch (Throwable e) {
                failure++;
                String errorMsg = "回调通知失败：" + e.getMessage();
                logger.error(errorMsg + "，config=" + config, e);
                this.saveTaskLog(taskId, "回调通知失败", errorMsg);
            }
        }
        // 有回调失败的整个任务算失败
        if (failure > 0) {
            callbackEntity.failure("回调通知失败");
            return -1;
        }

        callbackEntity.success();

        // 埋点-回调成功
        taskPointService.addTaskPoint(taskId, "900402");

        this.saveTaskLog(taskId, "回调通知成功", null);

        return 1;
    }

    /**
     * 判断是否需要回调
     *
     * @param config 回调配置
     * @param context 指令处理上下文对象
     * @return true if to callback
     */
    private boolean needCallback(CallbackConfigBO config, DirectiveContext context) {
        if (config == null) {
            return false;
        }

        EDirective directive = context.getDirective();

        logger.debug("Check task notify config! - taskId: {}, directive: {}, callback-config: {}", context.getTaskId(), directive, config);

        // 当前任务成功，但成功不通知
        if (EDirective.isTaskSuccess(directive) && SystemUtils.isNotTrue(config.getIsNotifySuccess())) {
            return false;
        }
        // 当前任务失败，但失败不通知
        else if (EDirective.isTaskFailure(directive) && SystemUtils.isNotTrue(config.getIsNotifyFailure())) {
            return false;
        }
        // 当前任务取消，但取消不通知
        else if (EDirective.isTaskCancel(directive) && SystemUtils.isNotTrue(config.getIsNotifyCancel())) {
            return false;
        }
        return true;
    }

    /**
     * 执行回调
     *
     * @param entity 回调数据实体
     * @param config 回调配置
     * @param context 指令处理上下文对象
     */
    private void doCallback(CallbackEntity entity, CallbackConfigBO config, DirectiveContext context) throws Throwable {
        // 回调数据实体备份
        CallbackEntity callbackEntity = (CallbackEntity)entity.clone();

        // 准备回调数据
        this.prepareCallbackData(callbackEntity, config, context);

        long startTime = System.currentTimeMillis();
        CallbackResponse response = null;
        try {
            response = this.sendCallbackRequest(callbackEntity, config, context);

            // 处理回调结果
            triggerAfterCallback(response, context);
        } finally {
            long consumeTime = System.currentTimeMillis() - startTime;
            final CallbackRecordObject callbackRecord = new CallbackRecordObject();
            callbackRecord.setCost(consumeTime);
            callbackRecord.setType(Constants.CALLBACK_TYPE_BACKEND);
            callbackRecord.setConfig(config);
            callbackRecord.setRequestParameters(JSON.toJSONString(entity));
            if (response == null) {
                callbackRecord.setResponseStatusCode(0);
                callbackRecord.setCallbackResult(new CallbackResult("", "回调执行错误"));
            } else {
                callbackRecord.setResponseStatusCode(response.getStatusCode());
                callbackRecord.setResponseData(response.getData());
                CallbackData callbackData = response.getResult();
                if (callbackData != null) {
                    callbackRecord.setCallbackResult(new CallbackResult(callbackData.getCode(), callbackData.getErrorMsg()));
                }
                callbackRecord.setException(response.getException());
            }

            // 记录回调日志
            taskCallbackLogService.insert(context.getTaskId(), callbackRecord);
            final TaskInfo task = context.getTask();
            // 主流程回调做监控
            if (Constants.DATA_TYPE_0.equals(config.getDataType())) {
                monitorService.sendTaskCallbackMsgMonitorMessage(task, callbackRecord);
            }
            // 回调处理
            callbackResultMonitor.sendMessage(task, callbackRecord);
        }

        final Throwable exception = response.getException();
        if (exception != null) {
            throw exception;
        }
    }

    private void triggerAfterCallback(CallbackResponse response, DirectiveContext context) {
        final int statusCode = response.getStatusCode();
        if (statusCode != HttpStatus.SC_OK) {
            if (statusCode == 0) {
                context.putAttribute(Constants.ERROR_MSG_NAME, "回调执行错误");
            } else {
                final CallbackData result = response.getResult();
                String errorMsg = result == null ? null : result.getErrorMsg();
                if (StringUtils.isNotEmpty(errorMsg)) {
                    context.putAttribute(Constants.ERROR_MSG_NAME, errorMsg);
                }
            }
        }
    }

    private CallbackResponse sendCallbackRequest(CallbackEntity callbackEntity, CallbackConfigBO config, DirectiveContext context) {
        // 回调请求地址
        String callbackUrl = config.getUrl();
        // 超时时间（秒）
        Byte timeout = config.getTimeOut();
        // 重试次数，3次
        Byte retryTimes = config.getRetryTimes();
        try {
            Map<String, Object> paramMap = this.buildCallbackRequestParameters(callbackEntity, config, context);
            logger.info("回调执行：taskId={}, callbackUrl={}, callbackEntity={}, params={}", context.getTaskId(), callbackUrl, callbackEntity, JSON.toJSONString(paramMap));
            String result;
            if (SystemUtils.isDataNotifyModel(config.getNotifyModel())) {
                result = HttpClientUtils.doPostWithTimeoutAndRetryTimes(callbackUrl, timeout, retryTimes, paramMap);
            } else {
                result = HttpClientUtils.doGetWithTimeoutAndRetryTimes(callbackUrl, timeout, retryTimes, paramMap);
            }
            return new CallbackResponse(result);
        } catch (RequestFailedException e) {
            // e.statusCode可能为0,表示客户端错误
            logger.error("doCallback exception: callbackUrl={},callbackEntity={}", callbackUrl, callbackEntity, e);
            return new CallbackResponse(e.getStatusCode(), e.getResult(), e);
        } catch (Throwable e) {
            return new CallbackResponse(0, StringUtils.EMPTY, e);
        }
    }

    /**
     * 创建回调请求参数
     *
     * @param callbackEntity 回调数据实体
     * @param config 回调配置
     * @param context 指令处理上下文
     * @return 回调请求参数
     * @throws CryptoException 回调数据加密异常
     */
    private Map<String, Object> buildCallbackRequestParameters(CallbackEntity callbackEntity, CallbackConfigBO config, DirectiveContext context) throws CryptoException {
        String params = this.safeEncryptCallbackEntity(callbackEntity, config, context);

        Map<String, Object> paramMap = new HashMap<>(2);
        paramMap.put("appId", context.getAppId());
        paramMap.put("params", params);

        return paramMap;
    }

    /**
     * 安全加密回调数据实体
     *
     * @param callbackEntity 回调数据实体
     * @param config 回调配置
     * @param context 指令处理上下文
     * @return 回调数据加密后的字符串
     * @throws CryptoException 加密异常
     */
    private String safeEncryptCallbackEntity(CallbackEntity callbackEntity, CallbackConfigBO config, DirectiveContext context) throws CryptoException {
        byte version = config.getVersion();
        if (version > 0) {
            // 默认使用AES方式
            // 是否使用新密钥，0-否，1-是
            Byte isNewKey = config.getIsNewKey();
            String aesDataKey;
            if (SystemUtils.isTrue(isNewKey)) {
                aesDataKey = context.getNewDataSecretKeyForCallback(config.getId());
            } else {
                // 获取商户密钥
                aesDataKey = context.getDataSecretKey();
            }
            return CallbackDataUtils.encryptByAES(callbackEntity, aesDataKey);
        }

        return encryptByRSA(callbackEntity, context);
    }

    /**
     * RSA加密回调数据
     *
     * @param callbackEntity 回调数据实体
     * @param context 指令处理上下文
     * @return 回调数据RSA加密后的字符串
     * @throws CryptoException 加密异常
     */
    private String encryptByRSA(CallbackEntity callbackEntity, DirectiveContext context) throws CryptoException {
        // 获取商户密钥
        String rsaPublicKey = context.getServerPublicKey();
        try {
            // 兼容老版本，使用RSA
            String params = CallbackDataUtils.encryptByRSA(callbackEntity, rsaPublicKey);

            return URLEncoder.encode(params, "utf-8");
        } catch (Exception e) {
            throw new CryptoException("encryptByRSA error : " + callbackEntity + ", key=" + context.getServerPublicKey(), e);
        }
    }

    /**
     * 准备回调数据
     *
     * @param callbackEntity 回调数据实体
     * @param config 回调配置
     * @param context 指令处理上下文对象
     */
    private void prepareCallbackData(CallbackEntity callbackEntity, CallbackConfigBO config, DirectiveContext context) {
        // 如果是数据传输，则需先下载数据
        Byte notifyModel = config.getNotifyModel();
        if (SystemUtils.isDataNotifyModel(notifyModel)) {
            Object dataUrlObj = callbackEntity.remove("dataUrl");
            if (dataUrlObj != null) {
                boolean success = true;
                Map<String, Object> downloadDataMap = null;
                try {
                    // oss 下载数据
                    byte[] result = RemoteDataUtils.download(dataUrlObj.toString(), byte[].class);
                    if (ArrayUtils.isNotEmpty(result)) {
                        // 数据体默认使用商户密钥加密
                        downloadDataMap = CallbackDataUtils.decryptAsMapByAES(result, context.getDataSecretKey());
                    }
                } catch (Exception e) {
                    logger.error("download data failed : data={}", callbackEntity, e);
                    success = false;
                }

                if (success) {
                    if (MapUtils.isNotEmpty(downloadDataMap)) {
                        callbackEntity.setData(downloadDataMap);
                    } else {
                        callbackEntity.emptyData();
                    }
                } else {
                    callbackEntity.put("dataUrl", dataUrlObj);
                    callbackEntity.failure("下载数据失败");
                }
            } else {
                callbackEntity.setData(StringUtils.EMPTY);
            }
        }
    }
}

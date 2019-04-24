package com.treefinance.saas.taskcenter.biz.service.directive.process;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.treefinance.b2b.saas.util.RemoteDataUtils;
import com.treefinance.saas.knife.result.SimpleResult;
import com.treefinance.saas.taskcenter.biz.callback.CallbackResultMonitor;
import com.treefinance.saas.taskcenter.biz.service.MonitorService;
import com.treefinance.saas.taskcenter.biz.service.TaskPointService;
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
import com.treefinance.saas.taskcenter.util.CallbackDataUtils;
import com.treefinance.saas.taskcenter.util.HttpClientUtils;
import com.treefinance.saas.taskcenter.util.SystemUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
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
    private TaskCallbackLogService taskCallbackLogService;

    /**
     * 保存回调操作日志
     * 
     */
    protected void saveCallbackLog(byte type, String result, int httpCode, long cost, CallbackEntity callbackEntity, CallbackConfigBO config, DirectiveContext context) {
        final Long taskId = context.getTaskId();
        try {
            taskCallbackLogService.insert(config, taskId, type, JSON.toJSONString(callbackEntity), result, cost, httpCode);
        } catch (Exception e) {
            logger.warn("保存回调操作日志发生错误！- taskId：{}, type: {}", taskId, type, e);
        }
    }


    @Override
    protected void validate(DirectiveContext context) {
        super.validate(context);
        if (!context.supportLicenseManager()) {
            throw new IllegalArgumentException("Not support license manager in directive context!");
        }
    }

    /**
     * 回调前处理
     *
     * @param callbackEntity 回调数据
     * @param context 指令处理上下文
     */
    protected void precallback(CallbackEntity callbackEntity, DirectiveContext context) {
        // 使用商户密钥加密数据，返回给前端
        Map<String, Object> paramMap = new HashMap<>(2);
        String remark = context.getRemark();
        if (StringUtils.isNotEmpty(remark)) {
            try {
                Map<String, Object> jsonObject = JSON.parseObject(remark);
                if (MapUtils.isNotEmpty(jsonObject)) {
                    paramMap.put(Constants.ERROR_MSG_NAME, jsonObject.get(Constants.ERROR_MSG_NAME));

                }
            } catch (Exception e) {
                logger.warn(e.getMessage(), e);
            }
        }

        try {

            String params = encryptByRSA(callbackEntity, context);
            paramMap.put("params", params);
            context.setRemark(JSON.toJSONString(paramMap));
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            context.setRemark(JSON.toJSONString("指令信息处理失败"));
        }
    }

    /**
     * 执行回调(无需预处理)
     *
     * @param context 指令处理上下文
     * @return
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
        logger.info("根据业务类型匹配回调配置结果:taskId={}, configList={}", taskId, JSON.toJSONString(configList));
        if (CollectionUtils.isEmpty(configList)) {
            logger.info("callback exit: callback-config is empty, directive={}", context);
            monitorService.sendTaskCallbackMsgMonitorMessage(taskId, null, null, false);
            return 0;
        }

        // 校验是否需要回调
        configList = configList.stream().filter(config -> needCallback(config, context)).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(configList)) {
            logger.info("callback exit: the task no callback required, directive={}", JSON.toJSONString(context));
            monitorService.sendTaskCallbackMsgMonitorMessage(taskId, null, null, false);
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
            } catch (Exception e) {
                failure++;
                String errorMsg = "回调通知失败：" + e.getMessage();
                logger.error(errorMsg + "，config=" + config, e);
                this.saveTaskLog(taskId, "回调通知失败", errorMsg);
            }
        }
        // 有回调失败的整个任务算失败
        if (failure > 0) {
            callbackEntity.failure("回调通知失败");
            flushData(callbackEntity, context);
            return -1;
        }

        callbackEntity.success();

        // 埋点-回调成功
        taskPointService.addTaskPoint(taskId, "900402");

        this.saveTaskLog(taskId, "回调通知成功", null);

        return 1;
    }

    /**
     * 生成回调数据实体，包含uniqueId、taskId、taskStatus、sourceId等信息。
     *
     * @param context 指令处理上下文
     * @return 回调数据实体 {@link CallbackEntity}
     */
    protected CallbackEntity buildCallbackEntity(DirectiveContext context) {
        // 1. 初始化回调数据 并填充uniqueId、taskId、taskStatus、sourceId
        final JSONObject initialAttrs = JSON.parseObject(context.getRemark());

        CallbackEntity entity = initialAttrs == null ? new CallbackEntity() : new CallbackEntity(initialAttrs);

        entity.setTaskIdIfAbsent(context.getTaskId());
        entity.setUniqueIdIfAbsent(context.getTaskUniqueId());
        entity.computeIfAbsent(ETaskAttribute.SOURCE_ID.getAttribute(), context::getTaskAttributeValue);

        // 此次任务状态：001-抓取成功，002-抓取失败，003-抓取结果为空,004-任务取消
        final Byte status = context.getTaskStatus();
        if (ETaskStatus.SUCCESS.getStatus().equals(status)) {
            entity.success();
        } else if (ETaskStatus.FAIL.getStatus().equals(status)) {
            // 任务失败消息
            TaskLog log = taskLogService.queryLastErrorLog(context.getTaskId());
            entity.failure(log != null ? log.getMsg() : EGrapStatus.FAIL.getName());
        } else if (ETaskStatus.CANCEL.getStatus().equals(status)) {
            entity.cancel("用户取消");
        } else {
            entity.success();
        }
        logger.info("回调数据生成 >> {}, directive={}", entity, context);
        return entity;
    }

    /**
     * 刷新数据
     *
     * @param callbackEntity
     * @param context
     */
    private void flushData(CallbackEntity callbackEntity, DirectiveContext context) {
        this.precallback(callbackEntity, context);
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
     * @param callbackEntity 回调数据实体
     * @param config 回调配置
     * @param context 指令处理上下文对象
     */
    private void doCallback(CallbackEntity callbackEntity, CallbackConfigBO config, DirectiveContext context) throws Exception {
        // 回调数据实体备份
        CallbackEntity backupCallbackEntity = (CallbackEntity)callbackEntity.clone();

        // 准备回调数据
        this.prepareCallbackData(callbackEntity, config, context);

        long startTime = System.currentTimeMillis();
        String result = "";
        int httpCode = 200;
        // 回调请求地址
        String callbackUrl = config.getUrl();
        // 超时时间（秒）
        Byte timeout = config.getTimeOut();
        // 重试次数，3次
        Byte retryTimes = config.getRetryTimes();
        try {
            Map<String, Object> paramMap = this.buildCallbackRequestParameters(callbackEntity, config, context);
            logger.info("回调执行：taskId={}, callbackUrl={}, callbackEntity={}, params={}", context.getTaskId(), callbackUrl, callbackEntity, JSON.toJSONString(paramMap));
            if (SystemUtils.isDataNotifyModel(config.getNotifyModel())) {
                result = HttpClientUtils.doPostWithTimeoutAndRetryTimes(callbackUrl, timeout, retryTimes, paramMap);
            } else {
                result = HttpClientUtils.doGetWithTimeoutAndRetryTimes(callbackUrl, timeout, retryTimes, paramMap);
            }
        } catch (RequestFailedException e) {
            logger.error("doCallback exception: callbackUrl={},callbackEntity={}", callbackUrl, callbackEntity, e);
            result = e.getResult();
            httpCode = e.getStatusCode();
            throw e;
        } finally {
            // 处理返回结果
            this.handleRequestResult(result, context);

            long consumeTime = System.currentTimeMillis() - startTime;
            // 记录回调日志
            this.saveCallbackLog((byte)1, result, httpCode, consumeTime, backupCallbackEntity, config, context);
            // 主流程回调做监控
            if (Constants.DATA_TYPE_0.equals(config.getDataType())) {
                monitorService.sendTaskCallbackMsgMonitorMessage(context.getTaskId(), httpCode, result, true);
            }
            // 回调处理
            callbackResultMonitor.sendMessage(context.getTask(), result, config, httpCode);
        }
    }

    /**
     * 处理请求失败异常
     *
     * @param requestResult 回调请求结果
     * @param context 指令处理上下文对象
     */
    private void handleRequestResult(String requestResult, DirectiveContext context) {
        try {
            String result = StringUtils.trimToEmpty(requestResult);
            if (StringUtils.isNotEmpty(result) && result.startsWith("{") && result.endsWith("}")) {
                logger.info("handle callback result : result={}, directiveContext={}", result, context);
                SimpleResult simpleResult = JSON.parseObject(result, SimpleResult.class);
                String errorMsg = simpleResult == null ? null : simpleResult.getErrorMsg();
                if (StringUtils.isNotEmpty(errorMsg)) {
                    Map<String, Object> remarkMap;
                    String remark = StringUtils.trim(context.getRemark());
                    if (StringUtils.isNotEmpty(remark)) {
                        remarkMap = JSON.parseObject(remark);
                    } else {
                        remarkMap = new HashMap<>(1);
                    }
                    remarkMap.put(Constants.ERROR_MSG_NAME, errorMsg);

                    context.setRemark(JSON.toJSONString(remarkMap));
                }
            }
        } catch (Exception e) {
            logger.error("handle result failed : directiveContext={}, result={}", context, requestResult, e);
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
                        flushData(callbackEntity, context);
                    }
                } else {
                    callbackEntity.put("dataUrl", dataUrlObj);
                    callbackEntity.failure("下载数据失败");
                    flushData(callbackEntity, context);
                }
            } else {
                callbackEntity.setData(StringUtils.EMPTY);
            }
        }

        // 此时针对工商无需爬取时处理
        if (callbackEntity.getCrawlerStatus()) {
            logger.info("工商回调，回调code设置为005，taskId={}", context.getTaskId());
            callbackEntity.setStatus(EGrapStatus.NO_NEED_CRAWLER, "");
        }

        // 如果是运营商数据
        if (EBizType.OPERATOR.getCode().equals(context.getBizType())) {
            String groupCodeAttribute = ETaskAttribute.OPERATOR_GROUP_CODE.getAttribute();
            String groupNameAttribute = ETaskAttribute.OPERATOR_GROUP_NAME.getAttribute();

            Map<String, String> attributeMap =
                taskAttributeService.getAttributeMapByTaskIdAndInNames(context.getTaskId(), new String[] {groupCodeAttribute, groupNameAttribute}, false);

            callbackEntity.put(groupCodeAttribute, StringUtils.defaultString(attributeMap.get(groupCodeAttribute)));
            callbackEntity.put(groupNameAttribute, StringUtils.defaultString(attributeMap.get(groupNameAttribute)));
        }
    }
}

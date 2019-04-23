package com.treefinance.saas.taskcenter.biz.service.directive.process;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Maps;
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
import com.treefinance.saas.taskcenter.exception.CallbackEncryptException;
import com.treefinance.saas.taskcenter.exception.CryptoException;
import com.treefinance.saas.taskcenter.exception.RequestFailedException;
import com.treefinance.saas.taskcenter.interation.manager.domain.CallbackConfigBO;
import com.treefinance.saas.taskcenter.service.AppCallbackConfigService;
import com.treefinance.saas.taskcenter.service.TaskCallbackLogService;
import com.treefinance.saas.taskcenter.service.domain.AttributedTaskInfo;
import com.treefinance.saas.taskcenter.util.CallbackDataUtils;
import com.treefinance.saas.taskcenter.util.HttpClientUtils;
import com.treefinance.saas.taskcenter.util.SystemUtils;
import com.treefinance.toolkit.util.http.exception.HttpException;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
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
     * 回调前处理
     *
     * @param context
     */
    protected void precallback(Map<String, Object> dataMap, DirectiveContext context) {
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

        logger.info("回调数据生成： {}", JSON.toJSONString(dataMap));

        try {

            String params = encryptByRSA(dataMap, context);
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
     * @param context
     * @return
     */
    protected int callback(DirectiveContext context) {
        // 生成数据map
        Map<String, Object> dataMap = generateDataMap(context);
        // 回调
        return callback(dataMap, context);
    }

    /**
     * 执行回调
     *
     * @param dataMap
     * @param context
     * @return 0-无需回调，1-回调成功，-1-回调失败
     */
    protected int callback(Map<String, Object> dataMap, DirectiveContext context) {
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
        List<Boolean> callbackFlags = new ArrayList<>(configList.size());
        for (CallbackConfigBO config : configList) {
            Boolean callbackSuccess;
            try {
                // 执行回调
                callbackSuccess = doCallBack(dataMap, config, context);
            } catch (Exception e) {
                dataMap.put("taskStatus", EGrapStatus.FAIL.getCode());
                dataMap.put("taskErrorMsg", "回调通知失败");
                flushData(dataMap, context);
                callbackSuccess = Boolean.FALSE;
                String errorMsg = "回调通知失败：" + e.getMessage();
                logger.error(errorMsg + "，config=" + config, e);
                taskLogService.insertTaskLog(taskId, "回调通知失败", new Date(), StringUtils.substring(errorMsg, 0, 1000));
            }
            callbackFlags.add(callbackSuccess);

        }
        // 有回调失败的整个任务算失败
        if (callbackFlags.contains(Boolean.FALSE)) {
            return -1;
        }

        // 埋点-回调成功
        taskPointService.addTaskPoint(taskId, "900402");

        dataMap.put("taskStatus", EGrapStatus.SUCCESS.getCode());
        dataMap.put("taskErrorMsg", "");
        taskLogService.insertTaskLog(taskId, "回调通知成功", new Date(), null);
        return 1;

    }

    /**
     * 生成数据Map
     *
     * @param context
     * @return
     */
    protected Map<String, Object> generateDataMap(DirectiveContext context) {
        AttributedTaskInfo task = context.getTask();
        // 1. 初始化回调数据 并填充uniqueId、taskId、taskStatus、sourceId
        Map<String, Object> dataMap = ifNull(JSON.parseObject(context.getRemark()), Maps.newHashMap());
        dataMap.putIfAbsent("uniqueId", task.getUniqueId());
        dataMap.putIfAbsent("taskId", task.getId());

        Map<String, String> attributes = task.getAttributes();
        if (attributes != null) {
            String attrName = ETaskAttribute.SOURCE_ID.getAttribute();
            String sourceId = attributes.get(attrName);
            if (sourceId != null) {
                dataMap.putIfAbsent(attrName, sourceId);
            }
        }

        dataMap.put("taskStatus", EGrapStatus.SUCCESS.getCode());
        dataMap.put("taskErrorMsg", "");
        // 此次任务状态：001-抓取成功，002-抓取失败，003-抓取结果为空,004-任务取消
        if (ETaskStatus.SUCCESS.getStatus().equals(task.getStatus())) {
            dataMap.put("taskStatus", EGrapStatus.SUCCESS.getCode());
            dataMap.put("taskErrorMsg", "");
        } else if (ETaskStatus.FAIL.getStatus().equals(task.getStatus())) {
            dataMap.put("taskStatus", EGrapStatus.FAIL.getCode());
            // 任务失败消息
            TaskLog log = taskLogService.queryLastErrorLog(task.getId());
            if (log != null) {
                dataMap.put("taskErrorMsg", log.getMsg());
            } else {
                dataMap.put("taskErrorMsg", EGrapStatus.FAIL.getName());
            }
        } else if (ETaskStatus.CANCEL.getStatus().equals(task.getStatus())) {
            dataMap.put("taskStatus", EGrapStatus.CANCEL.getCode());
            dataMap.put("taskErrorMsg", "用户取消");
        }
        logger.info("generateDataMap: data={}, directive={}", JSON.toJSONString(dataMap), JSON.toJSONString(context));
        return dataMap;
    }

    /**
     * 初始化回调参数
     *
     * @param dataMap
     * @param config
     * @param context
     * @throws Exception
     */
    private void initDataMap(Map<String, Object> dataMap, CallbackConfigBO config, DirectiveContext context) throws Exception {
        // 如果是数据传输，则需先下载数据
        Byte notifyModel = config.getNotifyModel();
        if (SystemUtils.isDataNotifyModel(notifyModel)) {
            dataMap.put("data", StringUtils.EMPTY);
            Object dataUrlObj = dataMap.remove("dataUrl");
            if (dataUrlObj != null) {
                try {
                    String dataUrl = dataUrlObj.toString();
                    // oss 下载数据
                    byte[] result = RemoteDataUtils.download(dataUrl, byte[].class);
                    // 数据体默认使用商户密钥加密
                    // 获取商户密钥
                    String appDataKey = context.getDataSecretKey();
                    Map<String, Object> downloadDataMap = CallbackDataUtils.decryptAsMapByAES(result, appDataKey);
                    dataMap.put("data", downloadDataMap);
                    if (MapUtils.isEmpty(downloadDataMap)) {
                        dataMap.put("taskErrorMsg", EGrapStatus.RESULT_EMPTY.getName());
                        dataMap.put("taskStatus", EGrapStatus.RESULT_EMPTY.getCode());
                        flushData(dataMap, context);
                    }
                } catch (HttpException e) {
                    logger.error("download data failed : data={}", JSON.toJSONString(dataMap));
                    dataMap.put("taskErrorMsg", "下载数据失败");
                    dataMap.put("taskStatus", EGrapStatus.FAIL.getCode());
                    dataMap.put("dataUrl", dataUrlObj);
                    flushData(dataMap, context);
                }
            }
        }
        // 此时针对工商无需爬取时处理
        Object crawlerStatus = dataMap.get("crawlerStatus");
        if (crawlerStatus != null && (int)crawlerStatus == 1) {
            logger.info("工商回调，回调code设置为005，taskId={}", context.getTaskId());
            dataMap.put("taskStatus", EGrapStatus.NO_NEED_CRAWLER.getCode());
            dataMap.put("taskErrorMsg", "");
        }
        // 如果是运营商数据
        if (context.getTask() != null && EBizType.OPERATOR.getCode().equals(context.getTask().getBizType())) {
            Long taskId = context.getTask().getId();
            String groupCodeAttribute = ETaskAttribute.OPERATOR_GROUP_CODE.getAttribute();
            String groupNameAttribute = ETaskAttribute.OPERATOR_GROUP_NAME.getAttribute();

            Map<String, String> attributeMap = taskAttributeService.getAttributeMapByTaskIdAndInNames(taskId, new String[] {groupCodeAttribute, groupNameAttribute}, false);

            dataMap.put(groupCodeAttribute, StringUtils.defaultString(attributeMap.get(groupCodeAttribute)));

            dataMap.put(groupNameAttribute, StringUtils.defaultString(attributeMap.get(groupNameAttribute)));
        }
    }

    /**
     * null值判断,
     *
     * @return <code>defaultValue</code> if given <code>value</code> was null.
     */
    private <T> T ifNull(T value, T defaultValue) {
        return value == null ? defaultValue : value;
    }

    /**
     * 刷新数据
     *
     * @param dataMap
     * @param context
     */
    private void flushData(Map<String, Object> dataMap, DirectiveContext context) {
        this.precallback(dataMap, context);
    }

    /**
     * 校验是否需要回调
     *
     * @param config
     * @param context
     * @return
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
     * 初始化回调参数
     *
     * @param dataMap
     * @param config
     * @param context
     * @return
     * @throws Exception
     */
    private String encryptParams(Map<String, Object> dataMap, CallbackConfigBO config, DirectiveContext context) throws Exception {
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
            return encryptByAES(dataMap, aesDataKey);
        }

        return encryptByRSA(dataMap, context);
    }

    /**
     * AES 加密
     *
     * @param dataMap
     * @param aesDataKey
     * @return
     * @throws CallbackEncryptException
     */
    private String encryptByAES(Map<String, Object> dataMap, String aesDataKey) throws CallbackEncryptException {
        return CallbackDataUtils.encryptByAES(dataMap, aesDataKey);
    }

    /**
     * RSA 加密
     *
     * @param dataMap
     * @param context
     * @return
     * @throws CryptoException
     */
    private String encryptByRSA(Map<String, Object> dataMap, DirectiveContext context) throws CryptoException {
        // 获取商户密钥
        String rsaPublicKey = context.getServerPublicKey();
        try {
            // 兼容老版本，使用RSA
            String params = CallbackDataUtils.encryptByRSA(dataMap, rsaPublicKey);

            return URLEncoder.encode(params, "utf-8");
        } catch (Exception e) {
            throw new CryptoException("encryptByRSA error : " + dataMap + ", key=" + context.getServerPublicKey(), e);
        }
    }

    /**
     * 执行回调
     *
     * @param dataMap
     * @param config
     * @param context
     * @return
     */
    private boolean doCallBack(Map<String, Object> dataMap, CallbackConfigBO config, DirectiveContext context) throws Exception {
        // 1.备份数据
        Map<String, Object> originalDataMap = Maps.newHashMap(dataMap);
        // 2.初始化数据
        this.initDataMap(dataMap, config, context);
        String params = this.encryptParams(dataMap, config, context);
        Map<String, Object> paramMap = Maps.newHashMap();
        paramMap.put("params", params);
        paramMap.put("appId", context.getAppId());
        String callbackUrl = config.getUrl();
        // 超时时间（秒）
        Byte timeOut = config.getTimeOut();
        // 重试次数，3次
        Byte retryTimes = config.getRetryTimes();
        logger.info("回调执行：taskId={},dataMap={}, params={}", context.getTaskId(), JSON.toJSONString(dataMap), JSON.toJSONString(paramMap));
        String result = "";
        int httpCode = 200;
        long startTime = System.currentTimeMillis();
        try {
            if (SystemUtils.isDataNotifyModel(config.getNotifyModel())) {
                result = HttpClientUtils.doPostWithTimeoutAndRetryTimes(callbackUrl, timeOut, retryTimes, paramMap);
            } else {
                result = HttpClientUtils.doGetWithTimeoutAndRetryTimes(callbackUrl, timeOut, retryTimes, paramMap);
            }
        } catch (RequestFailedException e) {
            logger.error("doCallBack exception: callbackUrl={},dataMap={}", callbackUrl, JSON.toJSONString(dataMap), e);
            result = e.getResult();
            httpCode = e.getStatusCode();
            throw e;
        } finally {
            long consumeTime = System.currentTimeMillis() - startTime;
            // 记录回调日志
            taskCallbackLogService.insert(config, context.getTaskId(), (byte)1, JSON.toJSONString(originalDataMap), result, consumeTime, httpCode);
            // 主流程回调做监控
            if (config.getDataType() != null && config.getDataType() == 0) {
                monitorService.sendTaskCallbackMsgMonitorMessage(context.getTaskId(), httpCode, result, true);
            }
            // 处理返回结果
            handleRequestResult(context, result);
            // 回调处理
            callbackResultMonitor.sendMessage(context.getTask(), result, config, httpCode);
        }
        return true;
    }

    /**
     * 处理请求失败异常
     *
     * @param context
     * @param result
     */
    private void handleRequestResult(DirectiveContext context, String result) {
        try {
            result = result.trim();
            if (StringUtils.isNotEmpty(result) && result.startsWith("{") && result.endsWith("}")) {
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
                    logger.info("handle callback result : result={},directiveContext={}", result, JSON.toJSONString(context));
                }
            }
        } catch (Exception e) {
            logger.info("handle result failed : directiveContext={}, result={}", JSON.toJSONString(context), result, e);
        }
    }
}

/*
 * Copyright © 2015 - 2017 杭州大树网络技术有限公司. All Rights Reserved
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package com.treefinance.saas.taskcenter.biz.service.directive.process;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.ImmutableMap;
import com.treefinance.saas.taskcenter.common.enums.EDirective;
import com.treefinance.saas.taskcenter.common.enums.ETaskStatus;
import com.treefinance.saas.taskcenter.context.Constants;
import com.treefinance.saas.taskcenter.exception.CryptoException;
import com.treefinance.saas.taskcenter.interation.manager.LicenseManager;
import com.treefinance.saas.taskcenter.interation.manager.domain.AppLicense;
import com.treefinance.saas.taskcenter.interation.manager.domain.CallbackLicense;
import com.treefinance.saas.taskcenter.service.domain.AttributedTaskInfo;
import com.treefinance.saas.taskcenter.util.CallbackDataUtils;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;

import java.io.Serializable;
import java.net.URLEncoder;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 指令处理上下文对象
 *
 * @author Jerry
 * @date 2019-04-21 22:19
 */
public class DirectiveContext implements Serializable {
    private static final Logger LOGGER = LoggerFactory.getLogger(DirectiveContext.class);

    /**
     * 指令
     */
    @Getter
    private EDirective directive;
    @Setter
    @Getter
    private String alias;

    /**
     * 指令ID
     */
    @Setter
    @Getter
    private String directiveId;

    /**
     * 任务ID
     */
    @Setter
    @Getter
    private Long taskId;

    /**
     * 备注信息
     */
    @Setter
    @Getter
    private String remark;

    /**
     * 额外信息
     */
    @Setter
    @Getter
    private Map<String, Object> extra;

    /**
     * directive processing 内部使用参数
     */
    @Setter
    @Getter
    private AttributedTaskInfo task;

    /**
     * 是否是魔蝎导入任务的指令
     */
    @Setter
    @Getter
    private boolean fromMoxie;

    /**
     * 商户的授权许可管理器
     */
    @Setter
    private LicenseManager licenseManager;

    /**
     * 商户的授权许可
     */
    private AppLicense appLicense;

    /**
     * 初始化信息来源于remark的json解析
     */
    private Map<String, Object> attributes;

    private String backup;

    private DirectiveContext(EDirective directive) {
        this.directive = Objects.requireNonNull(directive);
    }

    private DirectiveContext(EDirective directive, String alias) {
        this.directive = Objects.requireNonNull(directive);
        this.alias = alias;
    }

    public static DirectiveContext create(@Nonnull EDirective directive) {
        return new DirectiveContext(directive);
    }

    public static DirectiveContext create(@Nonnull EDirective directive, String alias) {
        return new DirectiveContext(directive, alias);
    }

    public void putExtra(String key, Object value) {
        if (this.extra == null) {
            this.extra = new HashMap<>(16);
        }
        this.extra.put(key, value);
    }

    /**
     * @return 指令的字符表示
     */
    public String getDirectiveString() {
        return directive == null ? "" : directive.equals(EDirective.CUSTOM) ? alias : directive.value();
    }

    /**
     * 更新指令
     * 
     * @param directive 新的指令
     */
    public void updateDirective(EDirective directive) {
        this.directive = directive;
        this.directiveId = null;
    }

    /**
     * 更新任务状态
     * 
     * @param status 任务状态
     */
    public void updateTaskStatus(@Nonnull ETaskStatus status) {
        task.setStatus(status.getStatus());
    }

    /**
     * 更新最近的任务步骤编号
     * 
     * @param stepCode 任务步骤编号
     */
    public void updateStepCode(String stepCode) {
        task.setStepCode(stepCode);
    }

    /**
     * @return 商户ID
     */
    public String getAppId() {
        return task.getAppId();
    }

    /**
     * @return 导入任务的业务类型
     */
    public Byte getBizType() {
        return task.getBizType();
    }

    /**
     * @return 用户唯一标识
     */
    public String getTaskUniqueId() {
        return task.getUniqueId();
    }

    /**
     * @return 任务状态
     */
    public Byte getTaskStatus() {
        return task.getStatus();
    }

    /**
     * 返回任务附带的属性值
     * 
     * @param attrName 属性名
     * @return 属性值
     */
    public Object getTaskAttributeValue(String attrName) {
        Map<String, String> attributes = task.getAttributes();
        if (attributes != null) {
            return attributes.get(attrName);
        }
        return null;
    }

    /**
     * @return true if license manager was not null.
     */
    public boolean supportLicenseManager() {
        return licenseManager != null;
    }

    /**
     * 获取商户的授权许可
     * 
     * @return 商户授权许可 {@link AppLicense}
     */
    public AppLicense getAppLicense() {
        if (appLicense == null) {
            appLicense = Objects.requireNonNull(licenseManager).getAppLicenseByAppId(this.getAppId());
        }
        return appLicense;
    }

    /**
     * 获取新版回调数据加密许可
     * 
     * @param callbackId 回调配置ID
     * @return 回调数据加密许可对象 {@link CallbackLicense}
     */
    public CallbackLicense getCallbackLicenseByCallbackId(@Nonnull Integer callbackId) {
        return Objects.requireNonNull(licenseManager).getCallbackLicenseByCallbackId(callbackId);
    }

    public String getNewDataSecretKeyForCallback(@Nonnull Integer callbackId) {
        return this.getCallbackLicenseByCallbackId(callbackId).getDataSecretKey();
    }

    public String getDataSecretKey() {
        return this.getAppLicense().getDataSecretKey();
    }

    public String getServerPublicKey() {
        return this.getAppLicense().getServerPublicKey();
    }

    public Map<String, Object> getAttributes() {
        return Collections.unmodifiableMap(getInnerAttributes());
    }

    private Map<String, Object> getInnerAttributes() {
        if (this.attributes == null) {
            final String remark = StringUtils.trim(this.getRemark());
            JSONObject remarks = null;
            try {
                if (StringUtils.isNotEmpty(remark)) {
                    remarks = JSON.parseObject(remark);
                }
            } catch (Exception e) {
                LOGGER.error("不正确的json格式，解析remark失败！>> {}", remark, e);
            }
            this.attributes = remarks == null ? new ConcurrentHashMap<>(16) : new ConcurrentHashMap<>(remarks);
        }

        return this.attributes;
    }

    public Object getAttributeValue(String name) {
        final Map<String, Object> innerExtra = getInnerAttributes();
        if (MapUtils.isNotEmpty(innerExtra)) {
            return innerExtra.get(name);
        }
        return null;
    }

    public void putAttribute(String key, Object value) {
        this.getInnerAttributes().put(key, value);
    }

    public void putAttributeIfAbsent(String key, Object value) {
        this.getInnerAttributes().putIfAbsent(key, value);
    }

    public void resetAttributes(Map<String, Object> attributes) {
        if (this.attributes != null) {
            this.attributes.clear();
            if (MapUtils.isNotEmpty(attributes)) {
                this.attributes.putAll(attributes);
            }
        } else if (MapUtils.isNotEmpty(attributes)) {
            this.attributes = new HashMap<>(attributes);
        }
    }

    public String getAttributesAsString() {
        if (this.attributes == null) {
            return this.remark;
        }
        return JSON.toJSONString(this.attributes);
    }

    public String getBackup() {
        if (backup == null) {
            return getAttributesAsString();
        }

        return backup;
    }

    public void backupCallbackEntity(CallbackEntity callbackEntity) {
        // 使用商户密钥加密数据，返回给前端
        try {
            Map<String, Object> paramMap = new HashMap<>(2);
            final Object attrValue = this.getAttributeValue(Constants.ERROR_MSG_NAME);
            if (attrValue != null) {
                paramMap.put(Constants.ERROR_MSG_NAME, attrValue);
            }

            String params = encryptByRSA(callbackEntity);
            paramMap.put("params", params);
            this.backup = JSON.toJSONString(paramMap);
        } catch (Exception e) {
            LOGGER.error("备份回调数据失败！", e);
            this.backup = JSON.toJSONString(ImmutableMap.of(Constants.ERROR_MSG_NAME, "备份回调数据失败"));
        }
    }

    /**
     * RSA加密回调数据
     *
     * @param callbackEntity 回调数据实体
     * @return 回调数据RSA加密后的字符串
     * @throws CryptoException 加密异常
     */
    private String encryptByRSA(CallbackEntity callbackEntity) throws CryptoException {
        // 获取商户密钥
        String rsaPublicKey = this.getServerPublicKey();
        try {
            // 兼容老版本，使用RSA
            String params = CallbackDataUtils.encryptByRSA(callbackEntity, rsaPublicKey);

            return URLEncoder.encode(params, "utf-8");
        } catch (Exception e) {
            throw new CryptoException("加密回调数据失败！- data : " + callbackEntity + ", key=" + rsaPublicKey, e);
        }
    }

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }
}
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
import com.treefinance.saas.taskcenter.common.enums.EDirective;
import com.treefinance.saas.taskcenter.common.enums.ETaskStatus;
import com.treefinance.saas.taskcenter.interation.manager.LicenseManager;
import com.treefinance.saas.taskcenter.interation.manager.domain.AppLicense;
import com.treefinance.saas.taskcenter.interation.manager.domain.CallbackLicense;
import com.treefinance.saas.taskcenter.service.domain.AttributedTaskInfo;
import lombok.Getter;
import lombok.Setter;

import javax.annotation.Nonnull;

import java.io.Serializable;
import java.util.Map;
import java.util.Objects;

/**
 * 指令处理上下文对象
 *
 * @author Jerry
 * @date 2019-04-21 22:19
 */
public class DirectiveContext implements Serializable {
    @Getter
    private EDirective directive;

    @Setter
    @Getter
    private String directiveId;

    @Setter
    @Getter
    private Long taskId;

    @Setter
    @Getter
    private String remark;

    /**
     * directive processing 内部使用参数
     */
    @Setter
    @Getter
    private AttributedTaskInfo task;

    @Setter
    @Getter
    private boolean fromMoxie;

    @Setter
    private LicenseManager licenseManager;

    private AppLicense appLicense;

    private DirectiveContext(EDirective directive) {
        this.directive = Objects.requireNonNull(directive);
    }

    public static DirectiveContext create(@Nonnull EDirective directive) {
        return new DirectiveContext(directive);
    }

    public String getDirectiveString() {
        return directive == null ? "" : directive.getText();
    }

    public void updateDirective(EDirective directive) {
        this.directive = directive;
        this.directiveId = null;
    }

    public void updateTaskStatus(@Nonnull ETaskStatus status) {
        task.setStatus(status.getStatus());
    }

    public void updateStepCode(String stepCode) {
        task.setStepCode(stepCode);
    }

    public String getAppId() {
        return task.getAppId();
    }

    public Byte getBizType() {
        return task.getBizType();
    }

    public String getTaskUniqueId() {
        return task.getUniqueId();
    }

    public Byte getTaskStatus() {
        return task.getStatus();
    }

    public Object getTaskAttributeValue(String attrName) {
        Map<String, String> attributes = task.getAttributes();
        if (attributes != null) {
            return attributes.get(attrName);
        }
        return null;
    }

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

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }

}
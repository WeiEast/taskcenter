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

import com.treefinance.saas.taskcenter.common.enums.EDirective;
import com.treefinance.saas.taskcenter.common.enums.ETaskStatus;
import com.treefinance.saas.taskcenter.interation.manager.LicenseManager;
import com.treefinance.saas.taskcenter.interation.manager.domain.AppLicense;
import com.treefinance.saas.taskcenter.interation.manager.domain.CallbackLicense;
import com.treefinance.saas.taskcenter.service.domain.AttributedTaskInfo;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.annotation.Nonnull;

import java.io.Serializable;
import java.util.Objects;

/**
 * 指令处理上下文对象
 *
 * @author Jerry
 * @date 2019-04-21 22:19
 */
@ToString
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

    public void updateTaskStatus(ETaskStatus status) {
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

    public Byte getTaskStatus() {
        return task.getStatus();
    }

    public boolean supportLicenseManager() {
        return licenseManager != null;
    }

    public AppLicense getAppLicense() {
        if (appLicense == null) {
            appLicense = Objects.requireNonNull(licenseManager).getAppLicenseByAppId(task.getAppId());
        }
        return appLicense;
    }

    public CallbackLicense getCallbackLicenseByCallbackId(Integer callbackId) {
        return Objects.requireNonNull(licenseManager).getCallbackLicenseByCallbackId(callbackId);
    }

    public String getDataSecretKey() {
        return getAppLicense().getDataSecretKey();
    }

    public String getServerPublicKey() {
        return getAppLicense().getServerPublicKey();
    }
}
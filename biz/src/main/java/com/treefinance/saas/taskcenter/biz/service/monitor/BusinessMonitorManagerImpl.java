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

package com.treefinance.saas.taskcenter.biz.service.monitor;

import com.treefinance.saas.taskcenter.common.enums.EBizType;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Jerry
 * @date 2019-03-06 12:19
 */
@Component
public class BusinessMonitorManagerImpl implements BusinessMonitorManager{
    private final List<BusinessMonitor> monitors;

    @Autowired
    public BusinessMonitorManagerImpl(List<BusinessMonitor> monitors) {
        // 对monitors按{@link Order} 或 {@link Ordered}进行排序
        if (CollectionUtils.size(monitors) > 1) {
            AnnotationAwareOrderComparator.sort(monitors);
        }
        this.monitors = monitors;
    }

    @Override
    public List<BusinessMonitor> getMonitors(EBizType bizType) {
        if (CollectionUtils.isNotEmpty(monitors)) {
            return monitors.stream().filter(monitor -> monitor.support(bizType)).collect(Collectors.toList());
        }

        return Collections.emptyList();
    }
}

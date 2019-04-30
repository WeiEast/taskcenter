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

package com.treefinance.saas.taskcenter.biz.service.directive.process.interceptor;

import com.treefinance.saas.taskcenter.biz.service.directive.process.DirectiveContext;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author Jerry
 * @date 2019-03-05 01:33
 */
@Component
public class ProcessorInterceptorChain {
    private List<ProcessorInterceptor> interceptors;

    @Autowired
    public ProcessorInterceptorChain(List<ProcessorInterceptor> interceptors) {
        if (CollectionUtils.size(interceptors) > 1) {
            AnnotationAwareOrderComparator.sort(interceptors);
        }
        this.interceptors = interceptors;
    }

    public void applyAfterCompletion(DirectiveContext context) {
        if (CollectionUtils.isNotEmpty(interceptors)) {
            for (ProcessorInterceptor interceptor : interceptors) {
                interceptor.afterCompletion(context);
            }
        }
    }
}

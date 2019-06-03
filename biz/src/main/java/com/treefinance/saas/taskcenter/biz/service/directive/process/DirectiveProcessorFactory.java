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
import com.treefinance.saas.taskcenter.exception.UnexpectedException;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Jerry
 * @date 2019-03-05 16:39
 */
@Component
public class DirectiveProcessorFactory {

    private Map<String, DirectiveProcessor> processorMap;

    @Autowired
    public DirectiveProcessorFactory(List<DirectiveProcessor> directiveProcessors) {
        if (CollectionUtils.isNotEmpty(directiveProcessors)) {
            HashMap<String, DirectiveProcessor> processorMap = new HashMap<>(directiveProcessors.size());

            for (DirectiveProcessor processor : directiveProcessors) {
                String key = key(processor.getSpecifiedDirective(), processor instanceof MoxieDirectiveProcessor);
                processorMap.put(key, processor);
            }

            this.processorMap = processorMap;
        } else {
            this.processorMap = Collections.emptyMap();
        }
    }

    private String key(EDirective directive, boolean isMoxie) {
        String content = directive == null ? "default" : directive.name();
        return isMoxie ? "moxie_" + content : content;
    }

    public DirectiveProcessor getDirectiveProcessor(@Nonnull DirectiveContext context) {
        EDirective directive = context.getDirective();

        String key = key(directive, context.isFromMoxie());
        DirectiveProcessor processor = processorMap.get(key);
        if (processor == null) {
            key = key(null, context.isFromMoxie());
            processor = processorMap.get(key);
        }

        if (processor == null) {
            throw new UnexpectedException("Can not find available directive processor! - context: " + context);
        }

        return processor;
    }
}

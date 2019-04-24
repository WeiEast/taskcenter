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

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Jerry
 * @date 2019-04-24 15:17
 */
public class CallbackEntityTest {

    @Test
    public void testClone() {
        final CallbackEntity callbackEntity = new CallbackEntity();
        callbackEntity.cancel("用户取消");
        callbackEntity.setUniqueIdIfAbsent("test");
        callbackEntity.setTaskIdIfAbsent(1L);
        callbackEntity.put("dataUrl", "https://www.baidu.com");

        System.out.println(callbackEntity);

        final CallbackEntity clone = (CallbackEntity)callbackEntity.clone();
        Assert.assertNotSame(clone, callbackEntity);
        System.out.println(clone);
        Assert.assertEquals(clone, callbackEntity);

    }
}
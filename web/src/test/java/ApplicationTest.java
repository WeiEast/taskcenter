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

import com.treefinance.saas.merchant.facade.service.MerchantBaseInfoFacade;
import com.treefinance.saas.taskcenter.interation.manager.CallbackConfigManager;
import com.treefinance.saas.taskcenter.web.TaskCenterApplication;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @author chengtong
 * @date 18/4/12 16:25
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = TaskCenterApplication.class, webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class ApplicationTest {

    @Autowired
    private CallbackConfigManager callbackConfigManager;
    @Autowired
    private MerchantBaseInfoFacade merchantBaseInfoFacade;

    @Test
    public void testDubbo() {
        callbackConfigManager.getAllCallbackConfigs();
        merchantBaseInfoFacade.queryValidMerchantByBizType((byte)3);
    }
}
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

package com.treefinance.saas.taskcenter.interation.manager.dubbo;

import com.treefinance.saas.processor.thirdparty.facade.fund.FundService;
import com.treefinance.saas.taskcenter.interation.manager.FundManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Jerry
 * @date 2019-02-28 21:23
 */
@Service
public class FundServiceAdapter implements FundManager {

    private final FundService fundService;

    @Autowired
    public FundServiceAdapter(FundService fundService) {
        this.fundService = fundService;
    }

    @Override
    public String fund(Long taskId, String fundDetailStr) throws Exception {
        return fundService.fund(taskId, fundDetailStr);
    }
}

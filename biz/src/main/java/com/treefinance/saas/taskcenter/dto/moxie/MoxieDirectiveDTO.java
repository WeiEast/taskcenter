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

package com.treefinance.saas.taskcenter.dto.moxie;

import com.treefinance.saas.taskcenter.dto.DirectiveDTO;

/**
 * Created by haojiahong on 2017/9/14.
 */
public class MoxieDirectiveDTO extends DirectiveDTO {

    private static final long serialVersionUID = 6201076878996673264L;

    private String moxieTaskId;

    public String getMoxieTaskId() {
        return moxieTaskId;
    }

    public void setMoxieTaskId(String moxieTaskId) {
        this.moxieTaskId = moxieTaskId;
    }
}

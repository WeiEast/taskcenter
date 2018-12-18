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

package com.treefinance.saas.taskcenter.exception;

import com.treefinance.saas.taskcenter.context.ErrorCode;

/**
 * @author Jerry
 * @date 2018/12/13 19:59
 */
public class IllegalParameterException extends BadServiceException {

    public IllegalParameterException(String message) {
        super(ErrorCode.INVALID_PARAMETER, message);
    }

    public IllegalParameterException(String message, Throwable cause) {
        super(ErrorCode.INVALID_PARAMETER, message, cause);
    }

    public IllegalParameterException(Throwable cause) {
        super(ErrorCode.INVALID_PARAMETER, cause);
    }
}

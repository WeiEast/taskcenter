package com.treefinance.saas.taskcenter.facade.request;

import lombok.Getter;
import lombok.Setter;

/**
 * @author haojiahong
 * @date 2018/9/21
 */
@Getter
@Setter
public class TaskUpdateRequest extends BaseRequest {

    private Long id;
    private String uniqueId;
    private String appId;
    private String accountNo;
    private String website;
    private Byte bizType;
    private String stepCode;
    private Byte status;
    private Byte saasEnv;

}

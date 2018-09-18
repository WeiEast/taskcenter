package com.treefinance.saas.taskcenter.facade.request;

import com.treefinance.saas.taskcenter.facade.result.common.BaseResult;

import java.util.Date;

/**
 * @author:guoguoyun
 * @date:Created in 2018/9/18下午2:04
 */
public class TaskCallbackLogRequest extends BaseRequest{

    private Long id;


    private Long taskId;


    private Long configId;


    private Byte type;

    private String url;


    private String requestParam;

    private String responseData;

    private Integer consumeTime;


    private Integer httpCode;


    private String callbackCode;


    private String callbackMsg;


    private Byte failureReason;


    private Date createTime;


    private Date lastUpdateTime;


}

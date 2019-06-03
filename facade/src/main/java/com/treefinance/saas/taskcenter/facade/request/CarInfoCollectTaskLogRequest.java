package com.treefinance.saas.taskcenter.facade.request;

import java.io.Serializable;
import java.util.Date;

/**
 * Good Luck Bro , No Bug !
 *
 * @author haojiahong
 * @date 2018/6/3
 */
@Deprecated
public class CarInfoCollectTaskLogRequest implements Serializable {
    private static final long serialVersionUID = -3558126448590095421L;
    private String msg;
    private Date occurTime;
    private String errorMsg;

    public CarInfoCollectTaskLogRequest() {

    }

    public CarInfoCollectTaskLogRequest(String msg, String errorMsg, Date occurTime) {
        this.msg = msg;
        this.errorMsg = errorMsg;
        this.occurTime = occurTime;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Date getOccurTime() {
        return occurTime;
    }

    public void setOccurTime(Date occurTime) {
        this.occurTime = occurTime;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }
}

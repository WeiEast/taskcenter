package com.treefinance.saas.taskcenter.facade.request;

/**
 * @author haojiahong
 * @date 2018/9/21
 */
public class TaskCreateRequest extends BaseRequest {
    private static final long serialVersionUID = -9030897967134485560L;

    private String appId;
    private String uniqueId;
    private Byte bizType;
    private String accountNo;
    private String website;
    private String stepCode;
    private Byte status;
    private Byte saasEnv;
    private String source;
    private String extra;

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(String uniqueId) {
        this.uniqueId = uniqueId;
    }

    public Byte getBizType() {
        return bizType;
    }

    public void setBizType(Byte bizType) {
        this.bizType = bizType;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getExtra() {
        return extra;
    }

    public void setExtra(String extra) {
        this.extra = extra;
    }

    public String getAccountNo() {
        return accountNo;
    }

    public void setAccountNo(String accountNo) {
        this.accountNo = accountNo;
    }

    public String getStepCode() {
        return stepCode;
    }

    public void setStepCode(String stepCode) {
        this.stepCode = stepCode;
    }

    public Byte getStatus() {
        return status;
    }

    public void setStatus(Byte status) {
        this.status = status;
    }

    public Byte getSaasEnv() {
        return saasEnv;
    }

    public void setSaasEnv(Byte saasEnv) {
        this.saasEnv = saasEnv;
    }
}

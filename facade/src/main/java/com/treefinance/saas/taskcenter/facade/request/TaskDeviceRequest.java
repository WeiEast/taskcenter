package com.treefinance.saas.taskcenter.facade.request;

import java.io.Serializable;
import java.util.Date;

/**
 * @author haojiahong
 * @date 2018/9/29
 */
public class TaskDeviceRequest implements Serializable {
    private static final long serialVersionUID = -6378687911461136114L;
    private Long id;
    private Long taskId;
    private String provinceName;
    private String cityName;
    private String positionData;
    private Double positionX;
    private Double positionY;
    private String appVersion;
    private Integer platformId;
    private String phoneBrand;
    private String phoneModel;
    private String operatorName;
    private String phoneVersion;
    private String netModel;
    private String ipAddress;
    private String ipPosition;
    private String idfa;
    private String openudid;
    private String imei;
    private String macAddress;
    private Date createTime;
    private Date lastUpdateTime;
    private String comment;
    private String operatorCode;
    private String cpuabi;
    private Boolean isEmulator;
    private Boolean isJailbreak;
    private String imsi;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    public String getProvinceName() {
        return provinceName;
    }

    public void setProvinceName(String provinceName) {
        this.provinceName = provinceName;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getPositionData() {
        return positionData;
    }

    public void setPositionData(String positionData) {
        this.positionData = positionData;
    }

    public Double getPositionX() {
        return positionX;
    }

    public void setPositionX(Double positionX) {
        this.positionX = positionX;
    }

    public Double getPositionY() {
        return positionY;
    }

    public void setPositionY(Double positionY) {
        this.positionY = positionY;
    }

    public String getAppVersion() {
        return appVersion;
    }

    public void setAppVersion(String appVersion) {
        this.appVersion = appVersion;
    }

    public Integer getPlatformId() {
        return platformId;
    }

    public void setPlatformId(Integer platformId) {
        this.platformId = platformId;
    }

    public String getPhoneBrand() {
        return phoneBrand;
    }

    public void setPhoneBrand(String phoneBrand) {
        this.phoneBrand = phoneBrand;
    }

    public String getPhoneModel() {
        return phoneModel;
    }

    public void setPhoneModel(String phoneModel) {
        this.phoneModel = phoneModel;
    }

    public String getOperatorName() {
        return operatorName;
    }

    public void setOperatorName(String operatorName) {
        this.operatorName = operatorName;
    }

    public String getPhoneVersion() {
        return phoneVersion;
    }

    public void setPhoneVersion(String phoneVersion) {
        this.phoneVersion = phoneVersion;
    }

    public String getNetModel() {
        return netModel;
    }

    public void setNetModel(String netModel) {
        this.netModel = netModel;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getIpPosition() {
        return ipPosition;
    }

    public void setIpPosition(String ipPosition) {
        this.ipPosition = ipPosition;
    }

    public String getIdfa() {
        return idfa;
    }

    public void setIdfa(String idfa) {
        this.idfa = idfa;
    }

    public String getOpenudid() {
        return openudid;
    }

    public void setOpenudid(String openudid) {
        this.openudid = openudid;
    }

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    public String getMacAddress() {
        return macAddress;
    }

    public void setMacAddress(String macAddress) {
        this.macAddress = macAddress;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getLastUpdateTime() {
        return lastUpdateTime;
    }

    public void setLastUpdateTime(Date lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getOperatorCode() {
        return operatorCode;
    }

    public void setOperatorCode(String operatorCode) {
        this.operatorCode = operatorCode;
    }

    public String getCpuabi() {
        return cpuabi;
    }

    public void setCpuabi(String cpuabi) {
        this.cpuabi = cpuabi;
    }

    public Boolean getEmulator() {
        return isEmulator;
    }

    public void setEmulator(Boolean emulator) {
        isEmulator = emulator;
    }

    public Boolean getJailbreak() {
        return isJailbreak;
    }

    public void setJailbreak(Boolean jailbreak) {
        isJailbreak = jailbreak;
    }

    public String getImsi() {
        return imsi;
    }

    public void setImsi(String imsi) {
        this.imsi = imsi;
    }
}

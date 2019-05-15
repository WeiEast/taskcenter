package com.treefinance.saas.taskcenter.context.enums;

/**
 * @author yh-treefinance
 * @date 2017/12/19.
 */
public enum EGrabStatus {
    /**
     * 抓取成功
     */
    SUCCESS("001", "抓取成功"),
    /**
     * 抓取失败
     */
    FAIL("002", "抓取失败"),
    /**
     * 抓取结果为空
     */
    RESULT_EMPTY("003", "抓取结果为空"),
    /**
     * 取消任务
     */
    CANCEL("004", "任务取消"),
    /**
     * 无需爬取
     */
    NO_NEED_CRAWLER("005", "无需爬取");

    private final String code;
    private final String name;

    EGrabStatus(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }
}

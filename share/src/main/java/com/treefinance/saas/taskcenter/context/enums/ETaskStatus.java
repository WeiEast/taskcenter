package com.treefinance.saas.taskcenter.context.enums;

/**
 * @author yh-treefinance
 * @date 2017/6/19.
 */
public enum ETaskStatus {

    /**
     * 运行中
     */
    RUNNING((byte)0, "进行中"),
    /**
     * 已取消
     */
    CANCEL((byte)1, "取消"),
    /**
     * 成功
     */
    SUCCESS((byte)2, "成功"),
    /**
     * 失败
     */
    FAIL((byte)3, "失败");

    private Byte status;
    private String name;

    ETaskStatus(Byte status, String name) {
        this.status = status;
        this.name = name;
    }

    public static boolean isRunning(Byte status) {
        return RUNNING.getStatus().equals(status);
    }

    public Byte getStatus() {
        return status;
    }

    public String getName() {
        return name;
    }
}

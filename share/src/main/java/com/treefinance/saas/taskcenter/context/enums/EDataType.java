package com.treefinance.saas.taskcenter.context.enums;

/**
 * Created by yh-treefinance on 2017/9/28.
 */
public enum EDataType {
    MAIN_STREAM((byte)0, "主数据"),
    DELIVERY_ADDRESS((byte)1, "收货地址"),
    OPERATOR_FLOW((byte)2, "运营商流量");

    private final Byte type;
    private final String name;

    EDataType(Byte type, String name) {
        this.type = type;
        this.name = name;
    }

    /**
     * type 转化
     *
     * @param type
     * @return
     */
    public static EDataType typeOf(Byte type) {
        for (EDataType dataType : values()) {
            if (dataType.getType().equals(type)) {
                return dataType;
            }
        }
        return null;
    }

    public Byte getType() {
        return type;
    }

    public String getName() {
        return name;
    }
}

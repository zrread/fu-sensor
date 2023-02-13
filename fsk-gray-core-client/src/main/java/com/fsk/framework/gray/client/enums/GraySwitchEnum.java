package com.fsk.framework.gray.client.enums;

public enum GraySwitchEnum {
    TRAFFIC_SWITCH("1", "灰度节点开关"),
    NORMAL_SWITCH("2", "非灰度节点开关"),
    AB_SWITCH("3", "AB测试开关"),
    MOD_RANGE("4", "灰度MOD范围"),
    ACTIVE_STEP("5", "灰度进度");

    private String index;
    private String value;

    GraySwitchEnum(String index, String value) {
        this.index = index;
        this.value = value;
    }

    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }


    public static String getDisplayIndex(String value) {
        for (GraySwitchEnum graySwitchEnum : GraySwitchEnum.values()) {
            if (graySwitchEnum.value.equals(value)) {
                return graySwitchEnum.index;
            }
        }
        return null;
    }

    public static String getDisplayValue(String index) {
        for (GraySwitchEnum graySwitchEnum : GraySwitchEnum.values()) {
            if (graySwitchEnum.index.equals(index)) {
                return graySwitchEnum.value;
            }
        }
        return null;
    }

}

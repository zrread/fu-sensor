package com.fsk.framework.gray.enums;

public enum YesNoEnum {
    YES("1", "是"),
    NO("0", "否");

    private String index;
    private String value;

    YesNoEnum(String index, String value) {
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
        for (YesNoEnum yesNoEnum : YesNoEnum.values()) {
            if (yesNoEnum.value.equals(value)) {
                return yesNoEnum.index;
            }
        }
        return null;
    }

    public static String getDisplayValue(String index) {
        for (YesNoEnum yesNoEnum : YesNoEnum.values()) {
            if (yesNoEnum.index.equals(index)) {
                return yesNoEnum.value;
            }
        }
        return null;
    }

}

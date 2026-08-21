package com.demo.utils.insert.enums;

import lombok.Getter;

import java.util.EnumSet;
import java.util.Set;


/**
 * 实际数据饿对应类型
 */
@Getter
public enum ActualDataTypeEnum {

    INT("整型"),
    DECIMAL("数字格式"),
    STRING("字符串"),
    BOOLEAN("布尔型"),
    DATETIME("日期格式"),
    JSON("JSON对象");

    private final String desc;

    ActualDataTypeEnum(String desc) {
        this.desc = desc;
    }

    private static final Set<ActualDataTypeEnum> NUMBER_TYPES = EnumSet.of(INT, DECIMAL);


    public boolean isNumberType() {
        return NUMBER_TYPES.contains(this);
    }

    public static boolean isNumberType(ActualDataTypeEnum dataType) {
        return dataType != null && dataType.isNumberType();
    }

}

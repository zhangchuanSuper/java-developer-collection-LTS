package com.demo.utils.query.enums;

import lombok.Getter;

/**
 * iot-db查询的逻辑操作
 */
@Getter
public enum IotDBLogicOperationEnum {

    /**
     * 逻辑与
     */
    AND("and"),

    /**
     * 逻辑或
     */
    OR("or"),

    /**
     * 逻辑非
     */
    NOT("not");


    private final String operator;

    IotDBLogicOperationEnum(String operator) {
        this.operator = operator;
    }

}

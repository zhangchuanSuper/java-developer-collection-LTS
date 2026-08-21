package com.demo.utils.query.enums;

import lombok.Getter;
import org.apache.tsfile.enums.TSDataType;

import java.util.Arrays;
import java.util.List;

@Getter
public enum IOTDBConditionEnum {

    /**
     * 大于
     */
    GT(">", Arrays.asList(TSDataType.INT32, TSDataType.INT64, TSDataType.FLOAT, TSDataType.DOUBLE, TSDataType.TIMESTAMP)),

    /**
     * 大于等于
     */
    GTE(">=", Arrays.asList(TSDataType.INT32, TSDataType.INT64, TSDataType.FLOAT, TSDataType.DOUBLE, TSDataType.TIMESTAMP)),

    /**
     * 小于
     */
    LT("<", Arrays.asList(TSDataType.INT32, TSDataType.INT64, TSDataType.FLOAT, TSDataType.DOUBLE, TSDataType.TIMESTAMP)),

    /**
     * 小于等于
     */
    LTE("<=", Arrays.asList(TSDataType.INT32, TSDataType.INT64, TSDataType.FLOAT, TSDataType.DOUBLE, TSDataType.TIMESTAMP)),

    /**
     * 等于
     */
    EQUAL("=", Arrays.asList(TSDataType.INT32, TSDataType.INT64, TSDataType.FLOAT, TSDataType.DOUBLE, TSDataType.TIMESTAMP, TSDataType.TEXT, TSDataType.STRING)),

    /**
     * 区间范围内，左闭右闭区 [40,50]
     */
    BETWEEN("between value1 and value2", Arrays.asList(TSDataType.INT32, TSDataType.INT64, TSDataType.FLOAT, TSDataType.DOUBLE)),

    /**
     * 区间范围外，左闭右闭区 [40,50]
     */
    NOT_BETWEEN("not between value1 and value2", Arrays.asList(TSDataType.INT32, TSDataType.INT64, TSDataType.FLOAT, TSDataType.DOUBLE)),

    /**
     * in (value1,value2,value3) 在特定范围内
     */
    IN_RANGE("in", Arrays.asList(TSDataType.INT32, TSDataType.INT64, TSDataType.FLOAT, TSDataType.DOUBLE, TSDataType.TIMESTAMP, TSDataType.TEXT, TSDataType.STRING)),

    /**
     * not in (value1,value2,value3) 在特定范围外
     */
    NOT_IN_RANGE("not in ", Arrays.asList(TSDataType.INT32, TSDataType.INT64, TSDataType.FLOAT, TSDataType.DOUBLE, TSDataType.TIMESTAMP, TSDataType.TEXT, TSDataType.STRING)),

    /**
     * 值为空
     */
    IS_NULL("is null", Arrays.asList(TSDataType.INT32, TSDataType.INT64, TSDataType.FLOAT, TSDataType.DOUBLE, TSDataType.TIMESTAMP, TSDataType.TEXT, TSDataType.STRING)),

    /**
     * 值非空
     */
    IS_NOT_NULL("is not null", Arrays.asList(TSDataType.INT32, TSDataType.INT64, TSDataType.FLOAT, TSDataType.DOUBLE, TSDataType.TIMESTAMP, TSDataType.TEXT, TSDataType.STRING));

    private final String operator;
    private final List<TSDataType> supportTypes;

    IOTDBConditionEnum(String operator, List<TSDataType> supportTypes) {
        this.operator = operator;
        this.supportTypes = supportTypes;
    }
}

package com.demo.utils.query.enums;

import lombok.Getter;
import lombok.Setter;


/**
 * 用于单个测点值的查询拼接
 */
@Getter
@Setter
public class BaseFieldCondition {
    private IOTDBConditionEnum condition;
    private String fieldKey;
    private Object value;
}

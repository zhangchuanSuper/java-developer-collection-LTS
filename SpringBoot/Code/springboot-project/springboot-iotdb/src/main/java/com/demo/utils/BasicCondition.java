package com.demo.utils;

import com.demo.utils.enums.IOTDBConditionEnum;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BasicCondition {

    private IOTDBConditionEnum condition;
    private String fieldKey;
    private Object value;
}

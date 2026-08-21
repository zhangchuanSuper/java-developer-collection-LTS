package com.demo.utils.insert.dto;

import com.demo.utils.insert.enums.ActualDataTypeEnum;
import lombok.Getter;
import lombok.Setter;


/**
 * 插入的数据对象
 */
@Getter
@Setter
public class AttributeDataResponse {
    private String sourceAttributeKey;
    private String targetAttributeKey;
    private Object attributeValue;
    private ActualDataTypeEnum dataType;
}

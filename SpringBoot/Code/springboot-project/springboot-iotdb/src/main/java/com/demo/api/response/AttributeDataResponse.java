package com.demo.api.response;

import com.demo.api.enums.ProductAttributeDataTypeEnum;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;

/**
 * 基础数据实体，原始数据最底层都会转换成下面格式
 */
@Getter
@Setter
public class AttributeDataResponse {

    private String sourceAttributeKey;
    private String targetAttributeKey;
    private String targetAttributeName;
    private Object attributeValue;
    private ProductAttributeDataTypeEnum dataType;
    private LocalDateTime pushTime;
    private ZonedDateTime zonedPushTime;
    private Long targetAttributeId;
    private String uuid;
    private Long currentTimestamp;
    private Boolean retransmit = false;
}

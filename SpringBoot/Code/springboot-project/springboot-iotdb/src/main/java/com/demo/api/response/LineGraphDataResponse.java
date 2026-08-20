package com.demo.api.response;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;

@Getter
@Setter
public class LineGraphDataResponse {

    private String sourceAttributeKey;
    private String targetAttributeName;
    private Object attributeValue;
    private Object minValue;
    private Object maxValue;
    private LocalDateTime pushTime;
    private ZonedDateTime zonedPushTime;
}

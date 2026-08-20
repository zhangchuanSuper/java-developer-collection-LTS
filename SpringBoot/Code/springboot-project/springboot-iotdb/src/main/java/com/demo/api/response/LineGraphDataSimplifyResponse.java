package com.demo.api.response;

import lombok.Getter;
import lombok.Setter;

import java.time.ZonedDateTime;

@Getter
@Setter
public class LineGraphDataSimplifyResponse {

    private Object value;
    private Object minValue;
    private Object maxValue;
    private ZonedDateTime zonedPushTime;
}

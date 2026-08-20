package com.demo.api.response;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class MonitorAttributeRelationSimplifyData {

    private String targetAttributeName;
    private List<LineGraphDataSimplifyResponse> data = List.of();
}

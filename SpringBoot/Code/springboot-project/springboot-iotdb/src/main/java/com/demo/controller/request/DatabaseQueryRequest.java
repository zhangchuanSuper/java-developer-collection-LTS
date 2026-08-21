package com.demo.controller.request;

import com.demo.utils.query.enums.ConditionGroup;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class DatabaseQueryRequest {

    private List<ConditionGroup> groupConditions = new ArrayList<>();

}

package com.demo.utils;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class ConditionGroup {

    private List<BasicCondition> conditions = new ArrayList<>();
}

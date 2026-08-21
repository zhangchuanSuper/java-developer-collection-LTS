package com.demo.utils.query.enums;

import com.demo.utils.query.enums.BaseFieldCondition;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;


/**
 * 单个查询组
 */
@Getter
@Setter
public class ConditionGroup {
    private List<BaseFieldCondition> conditions = new ArrayList<>();
}

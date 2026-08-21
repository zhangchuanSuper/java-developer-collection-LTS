package com.demo.utils;

import com.demo.utils.query.enums.IOTDBConditionEnum;
import com.demo.utils.query.enums.IotDBLogicOperationEnum;
import com.demo.utils.query.enums.BaseFieldCondition;
import com.demo.utils.query.enums.ConditionGroup;
import org.springframework.util.Assert;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public final class WhereConditionUtils {

    private WhereConditionUtils() {}

    /**
     * 外部访问的入口：一个条件分组存在多个条件，一个查询条件可能存在多个条件分组
     */
    public static String buildCondition(List<ConditionGroup> multipleConditionGroups) {
        if (multipleConditionGroups == null || multipleConditionGroups.isEmpty()) {
            return "";
        }
        List<String> groupSqlList = multipleConditionGroups.stream()
                .map(WhereConditionUtils::buildGroupCondition)
                .filter(sql -> sql != null && !sql.isBlank())
                .toList();
        if (groupSqlList.isEmpty()) {
            return "";
        }
        return groupSqlList.stream().collect(Collectors.joining(" or ", "(", ")"));
    }

    private static String buildGroupCondition(ConditionGroup conditionGroup) {
        if (conditionGroup == null || conditionGroup.getConditions() == null || conditionGroup.getConditions().isEmpty()) {
            return "";
        }
        return conditionGroup.getConditions().stream()
                .map(WhereConditionUtils::generateBasicCondition)
                .filter(sql -> sql != null && !sql.isBlank())
                .collect(Collectors.joining(" " + IotDBLogicOperationEnum.AND.getOperator() + " "));
    }

    private static String generateBasicCondition(BaseFieldCondition basicCondition) {
        IOTDBConditionEnum condition = basicCondition.getCondition();
        Assert.notNull(condition, "condition must not be null");
        return switch (condition) {
            case GT, GTE, LT, LTE, EQUAL -> generateCommonSql(basicCondition);
            case IS_NULL, IS_NOT_NULL -> generateNullSql(basicCondition);
            case BETWEEN, NOT_BETWEEN -> generateBetweenSql(basicCondition);
            case IN_RANGE, NOT_IN_RANGE -> generateInSql(basicCondition);
        };
    }

    private static String generateCommonSql(BaseFieldCondition basicCondition) {
        commonValidate(basicCondition);
        Object currentValue = basicCondition.getValue();
        if (currentValue instanceof LocalDateTime localDateTime) {
            currentValue = localDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        }
        return " %s %s %s ".formatted(
                basicCondition.getFieldKey(),
                basicCondition.getCondition().getOperator(),
                currentValue
        );
    }

    private static String generateNullSql(BaseFieldCondition basicCondition) {
        nullValidate(basicCondition);
        return " %s %s ".formatted(
                basicCondition.getFieldKey(),
                basicCondition.getCondition().getOperator()
        );
    }

    private static String generateBetweenSql(BaseFieldCondition basicCondition) {
        commonValidate(basicCondition);
        Assert.isTrue(
                Arrays.asList(IOTDBConditionEnum.BETWEEN, IOTDBConditionEnum.NOT_BETWEEN).contains(basicCondition.getCondition()),
                "condition must be BETWEEN or NOT_BETWEEN"
        );
        Assert.isTrue(
                basicCondition.getValue() instanceof List<?> valueList && valueList.size() == 2,
                "between value must be a list with 2 elements"
        );
        List<?> rangeValues = (List<?>) basicCondition.getValue();
        Object startValue = rangeValues.get(0);
        Object endValue = rangeValues.get(1);
        if (basicCondition.getCondition() == IOTDBConditionEnum.NOT_BETWEEN) {
            return "%s not between %s and %s".formatted(basicCondition.getFieldKey(), startValue, endValue);
        }
        return "%s between %s and %s".formatted(basicCondition.getFieldKey(), startValue, endValue);
    }

    private static String generateInSql(BaseFieldCondition basicCondition) {
        commonValidate(basicCondition);
        Assert.isTrue(basicCondition.getValue() instanceof List<?>, "in value must be a list");
        List<?> values = (List<?>) basicCondition.getValue();
        String conditionValues = "(%s)".formatted(values.stream().map(Object::toString).collect(Collectors.joining(",")));
        return "%s %s %s".formatted(
                basicCondition.getFieldKey(),
                basicCondition.getCondition().getOperator(),
                conditionValues
        );
    }

    private static void commonValidate(BaseFieldCondition basicCondition) {
        Assert.notNull(basicCondition.getCondition(), "condition must not be null");
        Assert.hasText(basicCondition.getFieldKey(), "fieldKey must not be blank");
        Assert.notNull(basicCondition.getValue(), "value must not be null");
    }

    private static void nullValidate(BaseFieldCondition basicCondition) {
        Assert.notNull(basicCondition.getCondition(), "condition must not be null");
        Assert.hasText(basicCondition.getFieldKey(), "fieldKey must not be blank");
    }
}

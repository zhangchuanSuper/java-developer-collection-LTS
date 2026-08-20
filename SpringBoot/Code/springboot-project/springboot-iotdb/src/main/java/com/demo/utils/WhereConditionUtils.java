package com.demo.utils;

import com.demo.common.enums.BizCodeEnum;
import com.demo.common.exception.BizException;
import com.demo.utils.enums.IOTDBConditionEnum;
import com.demo.utils.enums.IotDBLogicOperationEnum;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public final class WhereConditionUtils {

    private WhereConditionUtils() {
    }

    /**
     * 外部访问的入口：一个条件分组存在多个条件，一个查询条件可能存在多个条件分组
     */
    public static String buildCondition(List<ConditionGroup> multipleConditionGroups) {
        return multipleConditionGroups.stream()
                .map(WhereConditionUtils::buildGroupCondition)
                .filter(Objects::nonNull)
                .collect(Collectors.joining("or", "(", ")"));
    }

    private static String buildGroupCondition(ConditionGroup conditionGroup) {
        return conditionGroup.getConditions().stream()
                .map(WhereConditionUtils::generateBasicCondition)
                .filter(Objects::nonNull)
                .collect(Collectors.joining(IotDBLogicOperationEnum.AND.getOperator()));
    }

    private static String generateBasicCondition(BasicCondition basicCondition) {
        IOTDBConditionEnum condition = basicCondition.getCondition();
        if (condition == null) {
            throw new BizException(BizCodeEnum.COMMON_BUSINESS_ERROR);
        }
        return switch (condition) {
            case GT, GTE, LT, LTE, EQUAL -> generateCommonSql(basicCondition);
            case IS_NULL, IS_NOT_NULL -> generateNullSql(basicCondition);
            case BETWEEN, NOT_BETWEEN -> generateBetweenSql(basicCondition);
            case IN_RANGE, NOT_IN_RANGE -> generateInSql(basicCondition);
        };
    }

    private static String generateCommonSql(BasicCondition basicCondition) {
        commonValidate(basicCondition);
        Object currentValue = basicCondition.getValue();
        if (currentValue instanceof LocalDateTime localDateTime) {
            currentValue = localDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        }
        return " " + basicCondition.getFieldKey() + " " + basicCondition.getCondition().getOperator() + " " + currentValue + " ";
    }

    private static String generateNullSql(BasicCondition basicCondition) {
        nullValidate(basicCondition);
        return " " + basicCondition.getFieldKey() + " " + basicCondition.getCondition().getOperator() + " ";
    }

    private static String generateBetweenSql(BasicCondition basicCondition) {
        commonValidate(basicCondition);
        BizException.checkArgument(
                Arrays.asList(IOTDBConditionEnum.BETWEEN, IOTDBConditionEnum.NOT_BETWEEN).contains(basicCondition.getCondition()),
                BizCodeEnum.COMMON_PARAMETER_ERROR
        );
        BizException.checkArgument(
                basicCondition.getValue() instanceof List<?> valueList && valueList.size() == 2,
                BizCodeEnum.COMMON_PARAMETER_ERROR
        );
        List<?> rangeValues = (List<?>) basicCondition.getValue();
        Object startValue = rangeValues.get(0);
        Object endValue = rangeValues.get(1);
        if (basicCondition.getCondition() == IOTDBConditionEnum.NOT_BETWEEN) {
            return basicCondition.getFieldKey() + " not between " + startValue + " and " + endValue;
        }
        return basicCondition.getFieldKey() + "  between " + startValue + " and " + endValue;
    }

    private static String generateInSql(BasicCondition basicCondition) {
        commonValidate(basicCondition);
        BizException.checkArgument(basicCondition.getValue() instanceof List<?>, BizCodeEnum.COMMON_PARAMETER_ERROR);
        List<?> values = (List<?>) basicCondition.getValue();
        String conditionValues = "(" + values.stream().map(Object::toString).collect(Collectors.joining(",")) + ")";
        return basicCondition.getFieldKey() + " " + basicCondition.getCondition().getOperator() + " " + conditionValues;
    }

    private static void commonValidate(BasicCondition basicCondition) {
        BizException.checkArgument(
                basicCondition.getCondition() != null && basicCondition.getFieldKey() != null && basicCondition.getValue() != null,
                BizCodeEnum.COMMON_PARAMETER_ERROR
        );
    }

    private static void nullValidate(BasicCondition basicCondition) {
        BizException.checkArgument(
                basicCondition.getCondition() != null && basicCondition.getFieldKey() != null,
                BizCodeEnum.COMMON_PARAMETER_ERROR
        );
    }
}

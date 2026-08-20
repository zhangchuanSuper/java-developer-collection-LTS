package com.demo.common.enums;

import lombok.Getter;

/**
 * 状态码定义约束，共6位数，前三位代表服务，后3位代表接口
 */
@Getter
public enum BizCodeEnum {

    SYSTEM_ERROR(100, ""),

    /**
     * 110 通用操作码
     */
    COMMON_PARAMETER_ERROR(110004, "参数异常"),

    COMMON_CONFIG_ERROR(110005, "配置异常"),
    COMMON_BUSINESS_ERROR(110006, "业务异常"),
    COMMON_DATA_ABNORMAL(110007, "数据异常"),
    COMMON_CONVERT_ERROR(110009, "转换异常"),
    COMMON_OPERATION_ERROR(110010, "不可执行此操作"),
    COMMON_SHARE_CODE_ERROR(110012, "提取码错误");








    private final int code;
    private final String msg;

    BizCodeEnum(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}

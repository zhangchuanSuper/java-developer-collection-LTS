package com.demo.common.exception;

import com.demo.common.enums.BizCodeEnum;
import lombok.Getter;

@Getter
public class BizException extends RuntimeException {

    private final int code;

    public BizException(String message) {
        super(message);
        this.code = -1;
    }

    public BizException(int code, String message) {
        super(message);
        this.code = code;
    }

    public BizException(BizCodeEnum bizCodeEnum) {
        super(bizCodeEnum.getMsg());
        this.code = bizCodeEnum.getCode();
    }

    public static void checkArgument(boolean expression, String message) {
        checkArgument(expression, -1, message);
    }

    public static void checkArgument(boolean expression, BizCodeEnum bizCodeEnum) {
        checkArgument(expression, bizCodeEnum.getCode(), bizCodeEnum.getMsg());
    }

    public static void checkArgument(boolean expression, int code, String message) {
        if (!expression) {
            throw new BizException(code, message);
        }
    }
}

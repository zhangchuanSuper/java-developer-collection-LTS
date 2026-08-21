package com.demo.utils;

public final class JsonUtils {

    private JsonUtils() {}

    public static String toJsonString(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof String stringValue) {
            return stringValue;
        }
        return value.toString();
    }

}

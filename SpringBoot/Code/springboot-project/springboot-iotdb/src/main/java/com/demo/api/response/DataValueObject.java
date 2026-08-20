package com.demo.api.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.tsfile.enums.TSDataType;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class DataValueObject {

    private String key;
    private Object value;
    private TSDataType dataType;
    private LocalDateTime pushTime;

    public DataValueObject(String key, Object value, TSDataType dataType) {
        this.key = key;
        this.value = value;
        this.dataType = dataType;
    }
}

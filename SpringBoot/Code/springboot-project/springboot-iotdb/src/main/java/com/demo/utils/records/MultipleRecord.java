package com.demo.utils.records;

import lombok.NoArgsConstructor;
import org.apache.tsfile.enums.TSDataType;

import java.util.List;

@NoArgsConstructor
public class MultipleRecord {

    public record PathTriple(String attributeKey, String tableColumnName, String fullPath) { }

    public record DataTypeValue(TSDataType dataType, Object value) { }

    public record DataTypeValueHolder(List<TSDataType> dataTypes, List<Object> values) { }


}

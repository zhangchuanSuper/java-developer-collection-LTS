package com.demo.service;

import com.demo.api.enums.ProductAttributeDataTypeEnum;
import com.demo.api.response.AttributeDataResponse;
import com.demo.api.response.DataValueObject;
import com.demo.utils.ConditionGroup;
import com.demo.utils.JsonUtils;
import com.demo.utils.WhereConditionUtils;
import org.apache.iotdb.isession.SessionDataSet;
import org.apache.iotdb.isession.pool.SessionDataSetWrapper;
import org.apache.iotdb.session.pool.SessionPool;
import org.apache.tsfile.enums.TSDataType;
import org.apache.tsfile.write.schema.IMeasurementSchema;
import org.apache.tsfile.write.schema.MeasurementSchema;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.demo.utils.StringPair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class IotDBCommonService {

    private static final Logger logger = LoggerFactory.getLogger(IotDBCommonService.class);
    private static final String WHERE_KEY = "where";
    private static final String DEVICE_PREFIX = "device_";
    public static final String DATABASE_NAME = "root.db";
    private static final String QUERY_DATABASE_SQL_TEMPLATE = "select * from " + DATABASE_NAME + ".**";
    private static final String QUERY_DEVICE_SQL_TEMPLATE = "select * from " + DATABASE_NAME + ".%s";
    public static final String SEPARATOR = "_separator_";
    public static final String QUERY_DEVICE_TEMPLATE = " SHOW DEVICES " + DATABASE_NAME + ".** ";
    public static final String DEVICE_COLUMN = "Device";
    public static final String TIME = "Time";
    public static final String EARLIEST_TIME_ORDER_SQL = "order by " + TIME + " ASC";
    public static final String LATEST_TIME_ORDER_SQL = "order by " + TIME + " DESC";
    public static final String LATEST_POINT_SQL = "select last * from " + DATABASE_NAME + ".%s order by timeseries desc ";

    @Autowired
    private SessionPool iotDbSessionPool;

    public record PathTriple(String attributeKey, String tableColumnName, String fullPath) {
    }

    public PathTriple generatePathTriple(String deviceUniqueCode, String typeKey, String attributeKey) {
        String tableColumnName = typeKey + SEPARATOR + attributeKey;
        String fullPath = DATABASE_NAME + "." + genDeviceTableName(deviceUniqueCode) + "." + tableColumnName;
        return new PathTriple(attributeKey, tableColumnName, fullPath);
    }

    /**
     * 获取设备的全路径：root.db.device_2F69084100000069
     */
    private String generateDeviceFullPath(String deviceUniqueCode) {
        return DATABASE_NAME + "." + genDeviceTableName(deviceUniqueCode);
    }

    /**
     * 获取设备表名称：注意iot表名称不能是纯数字，格式位：device_deviceCode
     */
    private String genDeviceTableName(String deviceUniqueCode) {
        return DEVICE_PREFIX + deviceUniqueCode;
    }

    /**
     * 此处路径在iotdb中不真实存在：root.db.device_1F2423110000011B.L1_JS_1_separator_gZ （真实路径）
     */
    public String genProductMonitorTypePath(String deviceUniqueCode, String productMonitorTypeKey) {
        return DATABASE_NAME + "." + genDeviceTableName(deviceUniqueCode) + "." + productMonitorTypeKey;
    }

    /**
     * 单个设备插入单行数据
     *
     * @param dataMap Pair&lt;attributeKey,fullPathKey&gt; -&gt; value
     */
    public void insertTabletExample(String deviceUniqueCode, Map<StringPair, AttributeDataResponse> dataMap, long pushTime) {
        List<IMeasurementSchema> schemaList = new ArrayList<>();
        dataMap.forEach((keyPair, attributeDataResponse) ->
                schemaList.add(new MeasurementSchema(keyPair.first(), dataTypeConvert(attributeDataResponse).dataType())));

        DataTypeValueHolder dataTypeValuePair = convertData(dataMap);

        boolean successFlag = true;
        String insertMessage = "insert success";
        try {
            List<String> measurements = dataMap.keySet().stream()
                    .map(StringPair::first)
                    .collect(Collectors.toList());
            iotDbSessionPool.insertRecord(
                    generateDeviceFullPath(deviceUniqueCode),
                    pushTime,
                    measurements,
                    dataTypeValuePair.dataTypes(),
                    dataTypeValuePair.values()
            );
        } catch (Exception exception) {
            successFlag = false;
            insertMessage = exception.getMessage() == null ? "" : exception.getMessage();
        }
        logger.info("insert data deviceCode:{},pushTime:{},result is {},detail is {}", deviceUniqueCode, pushTime, successFlag, insertMessage);
    }

    private DataTypeValueHolder convertData(Map<StringPair, AttributeDataResponse> dataMap) {
        List<TSDataType> dataTypeList = new ArrayList<>();
        List<Object> dataValueList = new ArrayList<>();
        dataMap.forEach((keyPair, attributeDataResponse) -> {
            DataTypeValue convertPair = dataTypeConvert(attributeDataResponse);
            dataTypeList.add(convertPair.dataType());
            dataValueList.add(convertPair.value());
        });
        return new DataTypeValueHolder(dataTypeList, dataValueList);
    }

    private DataTypeValue dataTypeConvert(AttributeDataResponse attributeResponse) {
        ProductAttributeDataTypeEnum dataType = attributeResponse.getDataType();
        if (dataType == null) {
            throw new IllegalArgumentException("Invalid data type");
        }
        return switch (dataType) {
            case INT -> new DataTypeValue(TSDataType.INT64, Long.parseLong(attributeResponse.getAttributeValue().toString()));
            case DECIMAL -> new DataTypeValue(TSDataType.DOUBLE, Double.parseDouble(attributeResponse.getAttributeValue().toString()));
            case STRING, JSON, DATETIME -> new DataTypeValue(TSDataType.STRING, JsonUtils.toJsonString(attributeResponse.getAttributeValue()));
            case BOOLEAN -> new DataTypeValue(TSDataType.STRING, attributeResponse.getAttributeValue());
        };
    }

    private record DataTypeValue(TSDataType dataType, Object value) {
    }

    private record DataTypeValueHolder(List<TSDataType> dataTypes, List<Object> values) {
    }

    public List<List<DataValueObject>> executeDatabaseQuery(List<ConditionGroup> groupConditions) {
        String fullSql = generateFullCondition(QUERY_DATABASE_SQL_TEMPLATE, groupConditions);
        return executeBasicQuery(fullSql);
    }

    public List<List<DataValueObject>> executeDeviceQuery(
            String deviceUniqueKey,
            List<ConditionGroup> groupConditions,
            List<String> filterColumnNameList
    ) {
        String querySql = String.format(QUERY_DEVICE_SQL_TEMPLATE, genDeviceTableName(deviceUniqueKey));
        String fullSql = generateFullCondition(querySql, groupConditions);
        return executeBasicQuery(fullSql, filterColumnNameList);
    }

    public List<List<DataValueObject>> executeDeviceQuery(String deviceUniqueKey, List<ConditionGroup> groupConditions) {
        return executeDeviceQuery(deviceUniqueKey, groupConditions, new ArrayList<>());
    }

    /**
     * 基础数据查询逻辑
     *
     * @param sql iot-db的查询sql
     */
    public List<List<DataValueObject>> executeBasicQuery(String sql) {
        return executeBasicQuery(sql, new ArrayList<>());
    }

    public List<List<DataValueObject>> executeBasicQuery(String sql, List<String> filterColumnNameList) {
        logger.info("iot query sql is:{}", sql);
        List<List<DataValueObject>> resultDataList = new ArrayList<>();

        try (SessionDataSetWrapper sessionDataSetWrapper = iotDbSessionPool.executeQueryStatement(sql)) {
            List<String> columnNameList = sessionDataSetWrapper.getColumnNames();
            List<String> columnTypeList = sessionDataSetWrapper.getColumnTypes();
            SessionDataSet.DataIterator dataIterator = sessionDataSetWrapper.iterator();
            while (dataIterator.next()) {
                List<DataValueObject> rowDataList = new ArrayList<>();
                for (int index = 0; index < columnNameList.size(); index++) {
                    String currentColumnName = columnNameList.get(index);
                    TSDataType currentDataType = TSDataType.valueOf(columnTypeList.get(index));
                    Object value = getValue(dataIterator, currentDataType, currentColumnName);
                    if (value == null) {
                        continue;
                    }
                    if (filterColumnNameList.isEmpty()) {
                        rowDataList.add(new DataValueObject(currentColumnName, value, currentDataType));
                    } else if (filterColumnNameList.contains(currentColumnName) || TIME.equals(currentColumnName)) {
                        rowDataList.add(new DataValueObject(currentColumnName, value, currentDataType));
                    }
                }
                DataValueObject timeData = rowDataList.stream()
                        .filter(currentData -> TIME.equals(currentData.getKey()))
                        .findFirst()
                        .orElse(null);
                if (timeData != null && timeData.getValue() instanceof Long pushTime) {
                    for (DataValueObject currentData : rowDataList) {
                        currentData.setPushTime(Instant.ofEpochMilli(pushTime).atZone(ZoneId.systemDefault()).toLocalDateTime());
                    }
                }
                resultDataList.add(rowDataList);
            }
        } catch (Exception exception) {
            throw new RuntimeException("Failed to execute IoTDB query", exception);
        }
        return resultDataList;
    }

    /**
     * 查询设备每个点位最新数据
     */
    public List<DataValueObject> executeLastQuery(String deviceUniqueCode) {
        String lastSql = String.format(LATEST_POINT_SQL, genDeviceTableName(deviceUniqueCode));
        List<DataValueObject> rowDataList = new ArrayList<>();
        try (SessionDataSetWrapper sessionDataSetWrapper = iotDbSessionPool.executeQueryStatement(lastSql)) {
            List<String> columnNameList = sessionDataSetWrapper.getColumnNames();
            List<String> columnTypeList = sessionDataSetWrapper.getColumnTypes();
            SessionDataSet.DataIterator dataIterator = sessionDataSetWrapper.iterator();
            while (dataIterator.next()) {
                DataValueObject dataValueObject = new DataValueObject();
                for (int index = 0; index < columnNameList.size(); index++) {
                    String currentColumnName = columnNameList.get(index);
                    TSDataType currentDataType = TSDataType.valueOf(columnTypeList.get(index));
                    Object value = getValue(dataIterator, currentDataType, currentColumnName);
                    if (value == null) {
                        continue;
                    }
                    switch (currentColumnName) {
                        case "Time" -> dataValueObject.setPushTime(
                                Instant.ofEpochMilli((Long) value).atZone(ZoneId.systemDefault()).toLocalDateTime()
                        );
                        case "DataType" -> dataValueObject.setDataType(TSDataType.valueOf(value.toString()));
                        case "Value" -> dataValueObject.setValue(value);
                        case "Timeseries" -> dataValueObject.setKey(value.toString());
                        default -> {
                        }
                    }
                }
                rowDataList.add(dataValueObject);
            }
        } catch (Exception exception) {
            throw new RuntimeException("Failed to execute IoTDB last query", exception);
        }
        return rowDataList;
    }

    private Object getValue(SessionDataSet.DataIterator currentIterator, TSDataType dataType, String columnName) {
        try {
            if (currentIterator.isNull(columnName)) {
                return null;
            }
            return switch (dataType) {
                case BOOLEAN -> currentIterator.getBoolean(columnName);
                case INT32 -> currentIterator.getInt(columnName);
                case INT64 -> currentIterator.getLong(columnName);
                case FLOAT -> currentIterator.getFloat(columnName);
                case DOUBLE -> currentIterator.getDouble(columnName);
                case TEXT -> currentIterator.getString(columnName);
                default -> null;
            };
        } catch (Exception exception) {
            throw new RuntimeException("Failed to read column value: " + columnName, exception);
        }
    }

    private String generateFullCondition(String selectorSql, List<ConditionGroup> groupConditions) {
        String fullCondition = WhereConditionUtils.buildCondition(groupConditions);
        if (fullCondition.isBlank()) {
            return selectorSql;
        }
        return selectorSql + " " + WHERE_KEY + " " + fullCondition;
    }

    public DeviceInfoList findDatabaseDeviceInfoList() {
        List<List<DataValueObject>> deviceDataList = executeBasicQuery(QUERY_DEVICE_TEMPLATE, List.of(DEVICE_COLUMN));
        List<String> fullDevicePathList = deviceDataList.stream()
                .map(rowElement -> rowElement.stream()
                        .filter(element -> element.getValue() != null)
                        .findFirst()
                        .map(element -> element.getValue().toString())
                        .orElse(null))
                .filter(path -> path != null && !path.isBlank())
                .collect(Collectors.toCollection(ArrayList::new));
        List<String> deviceCodeList = fullDevicePathList.stream()
                .map(path -> path.substring(path.lastIndexOf('.') + 1).replace(DEVICE_PREFIX, ""))
                .collect(Collectors.toCollection(ArrayList::new));
        return new DeviceInfoList(fullDevicePathList, deviceCodeList);
    }

    public record DeviceInfoList(List<String> fullDevicePathList, List<String> deviceCodeList) {
    }

    /**
     * @param attributeFullKey 查询出来的数据KEY，格式如下： root.db.1F2423110000011B.L1_QJ_1_separator_X
     *                         获取设备Code
     */
    public String getUniqueDeviceCode(String attributeFullKey) {
        int lastDotIndex = attributeFullKey.lastIndexOf('.');
        String devicePath = attributeFullKey.substring(0, lastDotIndex);
        String databasePrefix = DATABASE_NAME + ".";
        return devicePath.substring(devicePath.indexOf(databasePrefix) + databasePrefix.length()).replace(DEVICE_PREFIX, "");
    }

    /**
     * 默认查询最早数据
     */
    public List<List<DataValueObject>> getEarliestOrLatestDeviceData(String deviceUniqueCode, boolean isEarliestData) {
        String orderSql = isEarliestData ? EARLIEST_TIME_ORDER_SQL : LATEST_TIME_ORDER_SQL;
        String sql = QUERY_DEVICE_SQL_TEMPLATE + " " + orderSql + " limit 1 ";
        return executeBasicQuery(String.format(sql, genDeviceTableName(deviceUniqueCode)));
    }

    public List<List<DataValueObject>> getEarliestOrLatestDeviceData(String deviceUniqueCode) {
        return getEarliestOrLatestDeviceData(deviceUniqueCode, true);
    }
}

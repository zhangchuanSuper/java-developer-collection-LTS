package com.demo.controller;

import com.demo.utils.insert.enums.ActualDataTypeEnum;
import com.demo.utils.insert.dto.AttributeDataResponse;
import com.demo.utils.query.response.DataValueObject;
import com.demo.controller.request.DatabaseQueryRequest;
import com.demo.controller.request.DeviceQueryRequest;
import com.demo.service.IotDBCommonService;
import com.demo.utils.StringPair;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/iotdb")
public class IOTDBController {

    private static final String MOCK_DEVICE_CODE = "2F69084100000069";
    private static final String MOCK_TYPE_KEY = "L1_JS_1";
    private static final String MOCK_ATTRIBUTE_KEY = "temperature";
    private static final double MOCK_TEMPERATURE = 26.8;

    private final IotDBCommonService iotDBCommonService;

    public IOTDBController(IotDBCommonService iotDBCommonService) {
        this.iotDBCommonService = iotDBCommonService;
    }

    /**
     * 通用插入接口：按设备插入一行点位数据
     */
    @PostMapping("/insert")
    public ResponseEntity<String> insertData(@RequestBody InsertRequest request) {
        long pushTime = request.getPushTime() == null ? Instant.now().toEpochMilli() : request.getPushTime();
        iotDBCommonService.insertTabletExample(request.getDeviceUniqueCode(), request.toServiceDataMap(), pushTime);
        return ResponseEntity.ok("insert success");
    }

    /**
     * 模拟插入一条数据
     */
    @PostMapping("/insert/mock")
    public ResponseEntity<String> insertMockData() {
        InsertRequest request = new InsertRequest();
        request.setDeviceUniqueCode(MOCK_DEVICE_CODE);
        request.setPushTime(Instant.now().toEpochMilli());

        InsertPoint point = new InsertPoint();
        point.setTypeKey(MOCK_TYPE_KEY);
        point.setAttributeKey(MOCK_ATTRIBUTE_KEY);
        point.setDataType(ActualDataTypeEnum.DECIMAL);
        point.setValue(MOCK_TEMPERATURE);
        request.getPoints().put(MOCK_ATTRIBUTE_KEY, point);

        iotDBCommonService.insertTabletExample(
                request.getDeviceUniqueCode(),
                request.toServiceDataMap(),
                request.getPushTime()
        );
        return ResponseEntity.ok("mock insert success");
    }

    /**
     * 按设备查询时序数据（支持 where 条件与列过滤）
     */
    @PostMapping("/query/device")
    public ResponseEntity<List<List<DataValueObject>>> queryDeviceData(@RequestBody DeviceQueryRequest request) {
        if (!StringUtils.hasText(request.getDeviceUniqueCode())) {
            return ResponseEntity.badRequest().build();
        }
        List<List<DataValueObject>> result = iotDBCommonService.executeDeviceQuery(
                request.getDeviceUniqueCode(),
                request.getGroupConditions(),
                request.getFilterColumnNameList()
        );
        return ResponseEntity.ok(result);
    }

    /**
     * 全库查询（root.db.**）
     */
    @PostMapping("/query/database")
    public ResponseEntity<List<List<DataValueObject>>> queryDatabaseData(@RequestBody(required = false) DatabaseQueryRequest request) {
        List<List<DataValueObject>> result = iotDBCommonService.executeDatabaseQuery(
                request == null ? List.of() : request.getGroupConditions()
        );
        return ResponseEntity.ok(result);
    }

    /**
     * 查询设备各点位最新值
     */
    @GetMapping("/query/device/{deviceUniqueCode}/last")
    public ResponseEntity<List<DataValueObject>> queryDeviceLastData(@PathVariable String deviceUniqueCode) {
        if (!StringUtils.hasText(deviceUniqueCode)) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(iotDBCommonService.executeLastQuery(deviceUniqueCode));
    }

    /**
     * 查询设备最早或最新一条完整记录
     */
    @GetMapping("/query/device/{deviceUniqueCode}/boundary")
    public ResponseEntity<List<List<DataValueObject>>> queryDeviceBoundaryData(
            @PathVariable String deviceUniqueCode,
            @RequestParam(defaultValue = "true") boolean earliest
    ) {
        if (!StringUtils.hasText(deviceUniqueCode)) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(iotDBCommonService.getEarliestOrLatestDeviceData(deviceUniqueCode, earliest));
    }

    /**
     * 查询库下全部设备
     */
    @GetMapping("/devices")
    public ResponseEntity<IotDBCommonService.DeviceInfoList> listDevices() {
        return ResponseEntity.ok(iotDBCommonService.findDatabaseDeviceInfoList());
    }

    @Getter
    @Setter
    public static class InsertRequest {
        private String deviceUniqueCode;
        private Long pushTime;
        private Map<String, InsertPoint> points = new LinkedHashMap<>();

        Map<StringPair, AttributeDataResponse> toServiceDataMap() {
            Map<StringPair, AttributeDataResponse> dataMap = new LinkedHashMap<>();
            points.forEach((ignored, point) -> {
                AttributeDataResponse response = new AttributeDataResponse();
                response.setDataType(point.getDataType());
                response.setAttributeValue(point.getValue());
                dataMap.put(new StringPair(point.getAttributeKey(), buildFullPath(deviceUniqueCode, point)), response);
            });
            return dataMap;
        }

        private String buildFullPath(String deviceCode, InsertPoint point) {
            return "%s.device_%s.%s%s%s".formatted(
                    IotDBCommonService.DATABASE_NAME,
                    deviceCode,
                    point.getTypeKey(),
                    IotDBCommonService.SEPARATOR,
                    point.getAttributeKey()
            );
        }
    }

    @Getter
    @Setter
    public static class InsertPoint {
        private String typeKey;
        private String attributeKey;
        private ActualDataTypeEnum dataType;
        private Object value;
    }



}

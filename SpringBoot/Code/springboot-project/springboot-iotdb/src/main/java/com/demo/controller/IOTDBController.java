package com.demo.controller;

import com.demo.api.enums.ProductAttributeDataTypeEnum;
import com.demo.api.response.AttributeDataResponse;
import com.demo.service.IotDBCommonService;
import com.demo.utils.StringPair;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/iotdb")
public class IOTDBController {

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
        request.setDeviceUniqueCode("2F69084100000069");
        request.setPushTime(Instant.now().toEpochMilli());

        InsertPoint point = new InsertPoint();
        point.setTypeKey("L1_JS_1");
        point.setAttributeKey("temperature");
        point.setDataType(ProductAttributeDataTypeEnum.DECIMAL);
        point.setValue(26.8);
        request.getPoints().put("temperature", point);

        iotDBCommonService.insertTabletExample(
                request.getDeviceUniqueCode(),
                request.toServiceDataMap(),
                request.getPushTime()
        );
        return ResponseEntity.ok("mock insert success");
    }

    @Getter
    @Setter
    public static class InsertRequest {
        private String deviceUniqueCode;
        private Long pushTime;
        private Map<String, InsertPoint> points = new LinkedHashMap<>();

        Map<StringPair, AttributeDataResponse> toServiceDataMap() {
            Map<StringPair, AttributeDataResponse> dataMap = new LinkedHashMap<>();
            points.forEach((key, point) -> {
                AttributeDataResponse response = new AttributeDataResponse();
                response.setDataType(point.getDataType());
                response.setAttributeValue(point.getValue());
                dataMap.put(new StringPair(point.getAttributeKey(), buildFullPath(deviceUniqueCode, point)), response);
            });
            return dataMap;
        }

        private String buildFullPath(String deviceCode, InsertPoint point) {
            return IotDBCommonService.DATABASE_NAME
                    + ".device_" + deviceCode
                    + "." + point.getTypeKey() + IotDBCommonService.SEPARATOR + point.getAttributeKey();
        }
    }

    @Getter
    @Setter
    public static class InsertPoint {
        private String typeKey;
        private String attributeKey;
        private ProductAttributeDataTypeEnum dataType;
        private Object value;
    }


}

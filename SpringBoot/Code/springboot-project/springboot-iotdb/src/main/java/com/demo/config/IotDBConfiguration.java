package com.demo.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * iot-db的相关配置
 */
@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "iot-db")
public class IotDBConfiguration {
    private String username;
    private String password;
    private String host;
    private Integer port;
    private Integer maxSize;
}

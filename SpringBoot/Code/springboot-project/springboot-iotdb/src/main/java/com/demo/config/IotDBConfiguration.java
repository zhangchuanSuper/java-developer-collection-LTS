package com.demo.config;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "iot-db")
@Getter
@Setter
@ToString
public class IotDBConfiguration {

    private String username;

    private String password;

    private String host;

    private Integer port;

    private Integer  maxSize;


}

package com.demo.component;

import com.demo.config.IotDBConfiguration;
import org.apache.iotdb.session.pool.SessionPool;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.ZoneId;

@Configuration
public class IotDbSessionPoolComponent {

    @Bean
    public SessionPool iotDbSessionPool(IotDBConfiguration iotConfig) {
        return new SessionPool.Builder()
                .user(iotConfig.getUsername())
                .password(iotConfig.getPassword())
                .host(iotConfig.getHost())
                .port(iotConfig.getPort())
                .maxSize(iotConfig.getMaxSize())
                .zoneId(ZoneId.systemDefault())
                .build();
    }


}

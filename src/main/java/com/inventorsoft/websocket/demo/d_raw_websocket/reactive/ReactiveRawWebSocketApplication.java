package com.inventorsoft.websocket.demo.d_raw_websocket.reactive;

import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

@SpringBootApplication(
        scanBasePackages = {
        "com.inventorsoft.websocket.demo.d_raw_websocket.reactive",
        "com.inventorsoft.websocket.demo.d_raw_websocket.common"
        },
        proxyBeanMethods = false
)
public class ReactiveRawWebSocketApplication {

    public static void main(String[] args) {
        new SpringApplicationBuilder(ReactiveRawWebSocketApplication.class)
                .web(WebApplicationType.REACTIVE)
                .run(args);
    }

}

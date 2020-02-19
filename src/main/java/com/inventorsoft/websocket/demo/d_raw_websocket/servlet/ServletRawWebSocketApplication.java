package com.inventorsoft.websocket.demo.d_raw_websocket.servlet;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {
        "com.inventorsoft.websocket.demo.config",
        "com.inventorsoft.websocket.demo.d_raw_websocket.common",
        "com.inventorsoft.websocket.demo.d_raw_websocket.servlet"
})
public class ServletRawWebSocketApplication {

    public static void main(String[] args) {
        SpringApplication.run(ServletRawWebSocketApplication.class, args);
    }

}

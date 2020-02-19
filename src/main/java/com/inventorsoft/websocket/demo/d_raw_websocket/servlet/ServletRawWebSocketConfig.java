package com.inventorsoft.websocket.demo.d_raw_websocket.servlet;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@EnableWebSocket
@RequiredArgsConstructor
@Configuration(proxyBeanMethods = false)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ServletRawWebSocketConfig implements WebSocketConfigurer {


    ServletRawWebSocketHandler servletRawWebSocketHandler;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry webSocketHandlerRegistry) {
        webSocketHandlerRegistry
                .addHandler(servletRawWebSocketHandler, "/web-socket")
                .setAllowedOrigins("*");
    }

}

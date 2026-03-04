package com.inventorsoft.websocket.demo.d_raw_websocket.servlet;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@EnableWebSocket
@Configuration(proxyBeanMethods = false)
class ServletRawWebSocketConfig implements WebSocketConfigurer {

    private final ServletRawWebSocketHandler servletRawWebSocketHandler;

    ServletRawWebSocketConfig(final ServletRawWebSocketHandler servletRawWebSocketHandler) {
        this.servletRawWebSocketHandler = servletRawWebSocketHandler;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry webSocketHandlerRegistry) {
        webSocketHandlerRegistry
                .addHandler(servletRawWebSocketHandler, "/web-socket")
                .setAllowedOrigins("*");
    }

}

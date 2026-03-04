package com.inventorsoft.websocket.demo.d_raw_websocket.reactive;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.HandlerMapping;
import org.springframework.web.reactive.config.EnableWebFlux;
import org.springframework.web.reactive.handler.SimpleUrlHandlerMapping;
import org.springframework.web.reactive.socket.server.support.WebSocketHandlerAdapter;
import org.springframework.web.socket.config.annotation.WebSocketConfigurationSupport;

import java.util.Map;

@EnableWebFlux
@Configuration(proxyBeanMethods = false)
public class ReactiveRawWebSocketConfig {

    private final ReactiveRawWebSocketHandler reactiveRawWebSocketHandler;

    public ReactiveRawWebSocketConfig(final ReactiveRawWebSocketHandler reactiveRawWebSocketHandler) {
        this.reactiveRawWebSocketHandler = reactiveRawWebSocketHandler;
    }

    /**
     * There is configuration support for blocking approach but not for reactive.
     *
     * @see WebSocketConfigurationSupport#webSocketHandlerMapping(WebSocketConfigurationSupport.DefaultSockJsSchedulerContainer)
     */
    @Bean
    HandlerMapping handlerMapping() {
        return new SimpleUrlHandlerMapping(Map.of("/web-socket/reactive", reactiveRawWebSocketHandler));
    }

    @Bean
    WebSocketHandlerAdapter webSocketHandlerAdapter() {
        return new WebSocketHandlerAdapter();
    }

}

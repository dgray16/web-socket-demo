package com.inventorsoft.websocket.demo.e_stomp;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.Value;
import lombok.experimental.FieldDefaults;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import java.util.List;

@RequiredArgsConstructor
@EnableWebSocketMessageBroker
@Configuration(proxyBeanMethods = false)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@SpringBootApplication(scanBasePackages = {
        "com.inventorsoft.websocket.demo.config", "com.inventorsoft.websocket.demo.e_stomp"
})

public class StompWebSocketConfig implements WebSocketMessageBrokerConfigurer {

    ObjectMapper objectMapper;

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry
                .addEndpoint("/web-socket")
                .setAllowedOrigins("*");
    }

    @Controller
    @RequiredArgsConstructor
    @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
    private class StompController {

        Service service = new Service();
        SimpMessagingTemplate simpMessagingTemplate;

        @SubscribeMapping("/get-data")
        public List<MessageDTO> getMessages() {
            return service.getMessages();
        }

        @MessageMapping("/send-data")
        @SneakyThrows
        public void sendMessage(Message<String> message) {
            simpMessagingTemplate.convertAndSend("/get-data", "Hello front-end!");
        }

    }

    private class Service {

        public List<MessageDTO> getMessages() {
            return List.of(new MessageDTO("Hello world!"), new MessageDTO("This, is, message!!!"));
        }

    }

    @Value
    private class MessageDTO {

        String message;

    }

    public static void main(String[] args) {
        SpringApplication.run(StompWebSocketConfig.class, args);
    }

}

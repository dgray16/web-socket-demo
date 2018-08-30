package com.inventorsoft.websocket.demo.e_stomp;

import com.google.gson.Gson;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
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

import java.util.ArrayList;
import java.util.List;


@SpringBootApplication(scanBasePackages = {"com.inventorsoft.websocket.demo.config", "com.inventorsoft.websocket.demo.e_stomp"})
@Configuration
@EnableWebSocketMessageBroker
public class StompWebSocketConfig implements WebSocketMessageBrokerConfigurer {


    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry
                .addEndpoint("/web-socket")
                .setAllowedOrigins("*");
    }

    @Controller
    private class StompController {

        private Service service = new Service();
        private SimpMessagingTemplate simpMessagingTemplate;

        public StompController(SimpMessagingTemplate simpMessagingTemplate) {
            this.simpMessagingTemplate = simpMessagingTemplate;
        }

        @SubscribeMapping("/get-data")
        public List<MessageDTO> getMessages() {
            return service.getMessages();
        }

        @MessageMapping("/send-data")
        public void sendMessage(Message message) {
            MessageRequest messageRequest = new Gson().fromJson(new String((byte[]) message.getPayload()), MessageRequest.class);

            List<MessageRequest> messages = new ArrayList<>();
            messages.add(messageRequest);

            simpMessagingTemplate.convertAndSend("/get-data", messages);
        }

        @NoArgsConstructor
        @AllArgsConstructor
        @Data
        private class MessageRequest {
            private String message;
        }

    }

    private class Service {
        public List<MessageDTO> getMessages() {
            List<MessageDTO> messages = new ArrayList<>();

            messages.add(new MessageDTO("Hello world!"));
            messages.add(new MessageDTO("This, is, message!!!"));

            return messages;
        }
    }

    @NoArgsConstructor
    @Data
    @AllArgsConstructor
    private class MessageDTO {

        private String message;

    }

    public static void main(String[] args) {
        SpringApplication.run(StompWebSocketConfig.class, args);
    }
}

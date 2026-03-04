package com.inventorsoft.websocket.demo.e_stomp;

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
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;

@EnableWebSocketMessageBroker
@Configuration(proxyBeanMethods = false)
@SpringBootApplication(
        scanBasePackages = {
                "com.inventorsoft.websocket.demo.config", "com.inventorsoft.websocket.demo.e_stomp"
        },
        proxyBeanMethods = false
)

class StompWebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry
                .addEndpoint("/web-socket")
                .setAllowedOrigins("*");
    }

    @Controller
    private class StompController {

        private final MessageService messageService = new MessageService();
        private final SimpMessagingTemplate simpMessagingTemplate;

        private StompController(final SimpMessagingTemplate simpMessagingTemplate) {
            this.simpMessagingTemplate = simpMessagingTemplate;
        }

        @SubscribeMapping("/get-data")
        List<MessageDto> getMessages() {
            return messageService.getMessages();
        }

        @MessageMapping("/send-data")
        void sendMessage(final Message<String> message) {
            simpMessagingTemplate.convertAndSend("/get-data", Collections.singletonList(new MessageDto("Hello front-end!")));
        }


        @SubscribeMapping("/get-data/reactive")
        void getMessagesReactive() {
            messageService
                    .getMessagesReactive()
                    .collectList()
                    .doOnNext(list -> simpMessagingTemplate.convertAndSend("/get-data/reactive", list))
                    .subscribe();
        }

        @MessageMapping("/send-data/reactive")
        void sendMessageReactive(final Message<String> message) {
            Mono
                    .just(new MessageDto("Hello front-end!"))
                    .map(List::of)
                    .doOnNext(value -> simpMessagingTemplate.convertAndSend("/get-data/reactive", value))
                    .subscribe();
        }

    }

    private class MessageService {

        public List<MessageDto> getMessages() {
            return List.of(new MessageDto("Hello world!"), new MessageDto("This, is, message!!!"));
        }

        public Flux<MessageDto> getMessagesReactive() {
            return Flux.fromIterable(getMessages());
        }

    }

    private record MessageDto(String message) {}

    static void main(String[] args) {
        SpringApplication.run(StompWebSocketConfig.class, args);
    }

}

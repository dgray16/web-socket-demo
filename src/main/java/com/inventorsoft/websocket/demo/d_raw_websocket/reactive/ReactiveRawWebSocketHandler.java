package com.inventorsoft.websocket.demo.d_raw_websocket.reactive;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.inventorsoft.websocket.demo.d_raw_websocket.common.Patient;
import com.inventorsoft.websocket.demo.d_raw_websocket.common.PatientService;
import com.inventorsoft.websocket.demo.d_raw_websocket.common.SendMessageRequest;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ReactiveRawWebSocketHandler implements WebSocketHandler {

    Set<WebSocketSession> sessions = new HashSet<>();

    ObjectMapper objectMapper;
    PatientService patientService;

    @Override
    public Mono<Void> handle(WebSocketSession session) {
        sessions.add(session);
        return session
                .receive().doOnNext(webSocketMessage -> processMessage(session, webSocketMessage))
                .doOnComplete(() -> sessions.remove(session))
                .then();
    }

    private void processMessage(WebSocketSession session, WebSocketMessage webSocketMessage) {
        /* Get all data from DB */
        Mono<WebSocketMessage> responseFromServer = patientService
                .findAllReactive()
                .map(Patient::getName)
                .collect(Collectors.joining(", "))
                .map(allNames -> "Response From Server: " + allNames)
                .map(session::textMessage);

        Mono<WebSocketMessage> yourMessage = parseMessage(webSocketMessage.getPayloadAsText())
                .map(SendMessageRequest::getMessage)
                .map(message -> session.textMessage("Your message is: " + message));

        session
                .send(Flux.concat(responseFromServer, yourMessage))
                .subscribe();
    }

    @SneakyThrows
    private Mono<SendMessageRequest> parseMessage(String message) {
        return Mono.just(objectMapper.readValue(message, SendMessageRequest.class));
    }

}
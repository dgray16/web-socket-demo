package com.inventorsoft.websocket.demo.d_raw_websocket.reactive;

import com.inventorsoft.websocket.demo.d_raw_websocket.common.Patient;
import com.inventorsoft.websocket.demo.d_raw_websocket.common.PatientService;
import com.inventorsoft.websocket.demo.d_raw_websocket.common.SendMessageRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tools.jackson.databind.json.JsonMapper;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class ReactiveRawWebSocketHandler implements WebSocketHandler {

    private final Set<WebSocketSession> sessions = new HashSet<>();

    private final JsonMapper jsonMapper;
    private final PatientService patientService;

    public ReactiveRawWebSocketHandler(final JsonMapper jsonMapper, final PatientService patientService) {
        this.jsonMapper = jsonMapper;
        this.patientService = patientService;
    }

    @Override
    public Mono<Void> handle(final WebSocketSession session) {
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
                .map(Patient::name)
                .collect(Collectors.joining(", "))
                .map(allNames -> "Response From Server: " + allNames)
                .map(session::textMessage);

        Mono<WebSocketMessage> yourMessage = parseMessage(webSocketMessage.getPayloadAsText())
                .map(SendMessageRequest::message)
                .map(message -> session.textMessage("Your message is: " + message));

        session
                .send(Flux.concat(responseFromServer, yourMessage))
                .subscribe();
    }

    private Mono<SendMessageRequest> parseMessage(String message) {
        return Mono.just(jsonMapper.readValue(message, SendMessageRequest.class));
    }

}
package com.inventorsoft.websocket.demo.d_raw_websocket.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.inventorsoft.websocket.demo.d_raw_websocket.common.Patient;
import com.inventorsoft.websocket.demo.d_raw_websocket.common.PatientService;
import com.inventorsoft.websocket.demo.d_raw_websocket.common.SendMessageRequest;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ServletRawWebSocketHandler extends TextWebSocketHandler {

    Set<WebSocketSession> sessions = new HashSet<>();

    PatientService patientService;
    ObjectMapper objectMapper;

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        /* Get all data from DB */
        List<Patient> foundPatients = patientService.findAll();

        String allNames = foundPatients.stream()
                .map(Patient::name)
                .collect(Collectors.joining(", "));

        SendMessageRequest request = objectMapper.readValue(message.getPayload(), SendMessageRequest.class);
        session.sendMessage(new TextMessage("Your message is: " + request.message()));
        session.sendMessage(new TextMessage("Response from server: " + allNames));
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        sessions.add(session);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        sessions.remove(session);
    }

}

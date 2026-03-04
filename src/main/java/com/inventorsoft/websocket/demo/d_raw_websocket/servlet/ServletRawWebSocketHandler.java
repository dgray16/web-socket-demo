package com.inventorsoft.websocket.demo.d_raw_websocket.servlet;

import com.inventorsoft.websocket.demo.d_raw_websocket.common.Patient;
import com.inventorsoft.websocket.demo.d_raw_websocket.common.PatientService;
import com.inventorsoft.websocket.demo.d_raw_websocket.common.SendMessageRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import tools.jackson.databind.json.JsonMapper;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
class ServletRawWebSocketHandler extends TextWebSocketHandler {

    private final Set<WebSocketSession> sessions = new HashSet<>();

    private final PatientService patientService;
    private final JsonMapper jsonMapper;

    ServletRawWebSocketHandler(final PatientService patientService, final JsonMapper jsonMapper) {
        this.patientService = patientService;
        this.jsonMapper = jsonMapper;
    }

    @Override
    protected void handleTextMessage(final WebSocketSession session, final TextMessage message) throws Exception {
        /* Get all data from DB */
        List<Patient> foundPatients = patientService.findAll();

        String allNames = foundPatients.stream()
                .map(Patient::name)
                .collect(Collectors.joining(", "));

        SendMessageRequest request = jsonMapper.readValue(message.getPayload(), SendMessageRequest.class);
        session.sendMessage(new TextMessage("Your message is: " + request.message()));
        session.sendMessage(new TextMessage("Response from server: " + allNames));
    }

    @Override
    public void afterConnectionEstablished(final WebSocketSession session) {
        sessions.add(session);
    }

    @Override
    public void afterConnectionClosed(final WebSocketSession session, final CloseStatus status) {
        sessions.remove(session);
    }

}

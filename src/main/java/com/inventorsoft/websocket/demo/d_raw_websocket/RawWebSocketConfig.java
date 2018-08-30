package com.inventorsoft.websocket.demo.d_raw_websocket;

import com.google.gson.Gson;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@SpringBootApplication(scanBasePackages = {"com.inventorsoft.websocket.demo.config", "com.inventorsoft.websocket.demo.d_raw_websocket"})
@Configuration
@EnableWebSocket
public class RawWebSocketConfig implements WebSocketConfigurer {

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry webSocketHandlerRegistry) {
        webSocketHandlerRegistry
                .addHandler(dataHandler(), "/web-socket")
                .setAllowedOrigins("*");
    }

    public WebSocketHandler dataHandler() {
        return new WebSocketDataHandler();
    }

    private class WebSocketDataHandler extends TextWebSocketHandler {

        private PatientRepository patientRepository = new PatientRepository();

        private Set<WebSocketSession> sessions = new HashSet<>();

        @Override
        protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
            /* Get all data from DB */
            List<Patient> foundPatients = patientRepository.findAll();

            String allNames = foundPatients.stream()
                    .map(Patient::getName)
                    .collect(Collectors.joining(", "));

            SendMessageRequest request = new Gson().fromJson(message.getPayload(), SendMessageRequest.class);
            session.sendMessage(new TextMessage("Your message is: " + request.getMessage()));
            session.sendMessage(new TextMessage("Response from server: " + allNames));


            /* Send message to another user */
            /*SendMessageRequest request = new Gson().fromJson(message.getPayload(), SendMessageRequest.class);
            sessions.stream()
                    .filter(ws -> request.getRecipient().equalsIgnoreCase(ws.getPrincipal().getName()))
                    .forEach(ws -> sendMessage(ws, request.getMessage()));*/
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

    private class PatientRepository {

        List<Patient> findAll() {
            List<Patient> patients = new ArrayList<>();

            patients.add(new Patient("Vova"));
            patients.add(new Patient("Antony"));

            return patients;
        }

    }

    @Data
    @AllArgsConstructor
    private class Patient {

        private String name;

    }

    @AllArgsConstructor
    @Data
    private class SendMessageRequest {
        private String message;
        private String recipient;
    }

    public static void main(String[] args) {
        SpringApplication.run(RawWebSocketConfig.class, args);
    }
}

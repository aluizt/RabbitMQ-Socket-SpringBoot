package com.example.socket.configuration;


import com.example.socket.model.WebSocketNotification;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Slf4j
public class WebsocketHandler extends TextWebSocketHandler {

    private Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>();
    private ObjectWriter objectWriter;

    public WebsocketHandler(ObjectMapper objectMapper) {
        this.objectWriter = objectMapper.writerWithDefaultPrettyPrinter();

    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable throwable) throws Exception {
        log.error("Erro no remetente" + session, throwable);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        log.info(String.format("Sessão %s fechada !!", session.getId()));
        sessions.remove(session.getId());
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        log.info("Conectado ... " + session.getId());
        sessions.put(session.getId(), session);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        log.info("Tratando a mensagem: {}", message);

        sendMessageToAll(createResponse(session,message),session);
    }

    private void sendMessageToAll(String message,WebSocketSession session) {
        TextMessage textMessage = new TextMessage(message);
        sessions.forEach((key, value) -> {
            if(key!=session.getId()) {
                try {
                    value.sendMessage(textMessage);
                    log.info("Enviado mensagem {} para socketId: {}", message, key);
                } catch (IOException e) {
                    log.error(e.toString());
                }
            }
        });
    }

    public void sendMessageTo(String message) {
        TextMessage textMessage = new TextMessage(message);
        sessions.forEach((key, value) -> {

                try {
                    value.sendMessage(textMessage);
                    log.info("Enviado mensagem {} para socketId: {}", message, key);
                } catch (IOException e) {
                    log.error(e.toString());
                }

        });
    }

    private String createResponse(WebSocketSession session, TextMessage message){
        return  "Enviada por : "+session.getId()+" "+message.getPayload();
    }


//    public boolean onApplicationEvent(WebSocketNotification notification) {
//
//        System.out.println("Message teste ***********");
//        try {
//            String msg = objectWriter.writeValueAsString(notification);
//            sendMessageToAll(msg);
//            return true;
//        } catch (JsonProcessingException e) {
//            log.error(e.toString());
//            throw new RuntimeException("error");
//        }
//    }
}
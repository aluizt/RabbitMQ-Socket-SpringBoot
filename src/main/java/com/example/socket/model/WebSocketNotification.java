package com.example.socket.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class WebSocketNotification {
  private String mensagemId;
  private ListenerInputModel notificacao;
}

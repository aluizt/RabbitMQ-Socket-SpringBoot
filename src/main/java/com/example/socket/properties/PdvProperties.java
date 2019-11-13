package com.example.socket.properties;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Component
public class PdvProperties {

  @Value("${filial}")
  private Long subsidiaryId;
}

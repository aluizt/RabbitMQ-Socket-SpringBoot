package com.example.socket.subscribe;

import com.example.socket.configuration.WebsocketHandler;
import com.rabbitmq.client.Channel;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@AllArgsConstructor
@Slf4j
public class SubscribeService {

    private final WebsocketHandler websocketHandler;
    private static final String QUEUE_NAME = "filial_";

    @RabbitListener(queues = QUEUE_NAME + "${filial}")
    @RabbitHandler
    public void onOrderMessage(@Payload String messageInputModel,
                               @Header(AmqpHeaders.DELIVERY_TAG) long tag,
                               Channel channel) {
        verifyMessage(channel, tag, messageInputModel);
    }

    public void verifyMessage(Channel channel, long tag, String listenerInputModel) {

        processMessage(listenerInputModel);
        ackNotification(listenerInputModel, tag, channel);

    }

    private void processMessage(String listenerInputModel) {
        websocketHandler.sendMessageTo(listenerInputModel);
    }


    private void ackNotification(String msg, long tag, Channel channel) {
        log.info("ACK confirmado para a notificação : " + msg);
        try {
            channel.basicAck(tag, false);
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private void nackNotification(long tag, Channel channel) {
        try {
            channel.basicNack(tag, false, true);
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}

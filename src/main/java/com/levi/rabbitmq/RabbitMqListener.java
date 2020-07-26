package com.levi.rabbitmq;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class RabbitMqListener {
    @RabbitListener(
            containerFactory = "testContainer",
            queues = "testQueue"
    )
    public void handle(Message message) throws Exception {
        log.info("testConsumer = {}" ,message.toString());
        throw new Exception();
    }

    @RabbitListener(
            containerFactory = "testContainer",
            queues = "DEAD_LETTER_QUEUE"
    )
    public void handleDlq(Message message) {
        log.info("dlqConsumer = {}", message.toString());
    }
}

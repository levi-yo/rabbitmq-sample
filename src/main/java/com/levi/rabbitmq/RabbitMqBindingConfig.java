package com.levi.rabbitmq;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class RabbitMqBindingConfig {
    private final RabbitMqProperty rabbitMqProperty;

    @Bean
    public TopicExchange testTopicExchange() {
        return ExchangeBuilder
                .topicExchange(rabbitMqProperty.getTest().getExchangeName())
                .durable(true)
                .build();
    }

    @Bean
    public TopicExchange dlExchange() {
        return ExchangeBuilder
                .topicExchange(rabbitMqProperty.getTest().getDeadLetterExchange())
                .durable(true)
                .build();
    }

    @Bean
    public Queue testQueue() {
        final RabbitMqProperty.RabbitMqDetailProperty testDetail = rabbitMqProperty.getTest();
        return QueueBuilder
                .durable(testDetail.getQueueName())
                .deadLetterExchange(testDetail.getDeadLetterExchange())
                .deadLetterRoutingKey(testDetail.getDeadLetterRouteKey())
                .build();
    }

    @Bean
    public Queue dlq() {
        return QueueBuilder
                .durable("DEAD_LETTER_QUEUE")
                .build();
    }

    @Bean
    public Binding testBinding(final Queue testQueue, final TopicExchange testTopicExchange) {
        return BindingBuilder
                .bind(testQueue)
                .to(testTopicExchange)
                .with(rabbitMqProperty.getTest().getRouteKey());
    }

    @Bean
    public Binding dlBinding(final Queue dlq, final TopicExchange dlExchange) {
        return BindingBuilder
                .bind(dlq)
                .to(dlExchange)
                .with(rabbitMqProperty.getTest().getDeadLetterRouteKey());
    }
}

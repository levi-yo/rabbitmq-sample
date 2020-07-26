package com.levi.rabbitmq;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class RabbitMqConfig {
    private final RabbitMqProperty rabbitMqProperty;

    @Bean
    public ConnectionFactory testConnectionFactory() {
        final RabbitMqProperty.RabbitMqDetailProperty test = rabbitMqProperty.getTest();
        final CachingConnectionFactory factory = new CachingConnectionFactory();
        factory.setHost(test.getHost());
        factory.setPort(test.getPort());
        factory.setUsername(test.getUsername());
        factory.setPassword(test.getPassword());
        factory.setVirtualHost(test.getVirtualHost());

        return factory;
    }

    @Bean
    public SimpleRabbitListenerContainerFactory testContainer(final ConnectionFactory testConnectionFactory) {
        final SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(testConnectionFactory);
        factory.setConnectionFactory(testConnectionFactory);
        factory.setErrorHandler(t -> log.error("ErrorHandler = {}", t.getMessage()));
        factory.setDefaultRequeueRejected(false); //true일 경우 리스너에서 예외가 발생시 다시 큐에 메시지가 쌓이게 된다.(예외상황 해결안될 시 무한루프)
        factory.setMessageConverter(jacksonConverter());
        //예외 발생시 recover할 수 있는 옵션, 위 에러 핸들러와 잘 구분해서 사용해야할듯
        //위 핸들러와 적용 순서등도 테스트가 필요(혹은 둘다 동시에 적용되나?)
//        factory.setAdviceChain(
//                RetryInterceptorBuilder
//                .stateless()
//                .maxAttempts(3) //최대 재시도 횟수
//                .recoverer() //recover handler
//                .backOffOptions(2000, 4, 10000) //2초 간격으로, 4번, 최대 10초 내에 재시도
//                .build()
//        );
        return factory;
    }

    @Bean
    public RabbitTemplate testTemplate(final ConnectionFactory testConnectionFactory, final MessageConverter jacksonConverter) {
        final RabbitMqProperty.RabbitMqDetailProperty property = rabbitMqProperty.getTest();
        final RabbitTemplate template = new RabbitTemplate();
        template.setConnectionFactory(testConnectionFactory);
        template.setMessageConverter(jacksonConverter);
        template.setExchange(property.getExchangeName());
        template.setRoutingKey(property.getRouteKey());
        return template;
    }

    @Bean
    public MessageConverter jacksonConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitAdmin testAdmin(final ConnectionFactory testConnectionFactory) {
        return new RabbitAdmin(testConnectionFactory);
    }
}

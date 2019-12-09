package com.test.amqp.adapter.configuration;

import com.test.amqp.adapter.model.RandomServiceMessage;
import com.test.amqp.adapter.service.AmqpAdapterService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.amqp.support.converter.ClassMapper;
import org.springframework.amqp.support.converter.DefaultClassMapper;
import org.springframework.amqp.support.converter.Jackson2JavaTypeMapper;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Configuration
public class AmqpConfiguration {
    @Bean
    public ConnectionFactory amqpConnectionFactory() {
        final CachingConnectionFactory connectionFactory = new CachingConnectionFactory("localhost");
        connectionFactory.setUsername("guest");
        connectionFactory.setPassword("guest");
        return connectionFactory;
    }

    @Bean
    public AmqpAdmin amqpAdmin(final ConnectionFactory amqpConnectionFactory) {
        return new RabbitAdmin(amqpConnectionFactory);
    }

    @Bean
    public Queue consumerQueue(final AmqpAdmin amqpAdmin) {
        final Queue queue = QueueBuilder
                .nonDurable(UUID.randomUUID().toString())
                .build();
        final Exchange exchange = ExchangeBuilder
                .topicExchange("amqp.topic.inbound")
                .durable(true)
                .build();
        final Binding binding = BindingBuilder
                .bind(queue)
                .to(exchange)
                .with("amqp.*.*.reply") // amqp.service_name.version.reply
                .noargs();
        amqpAdmin.declareQueue(queue);
        amqpAdmin.declareExchange(exchange);
        amqpAdmin.declareBinding(binding);
        return queue;
    }

    @Bean
    public Jackson2JsonMessageConverter jackson2JsonMessageConverter() {
        final Jackson2JsonMessageConverter messageConverter = new Jackson2JsonMessageConverter("com.test.amqp.adapter.model");
        messageConverter.setTypePrecedence(Jackson2JavaTypeMapper.TypePrecedence.TYPE_ID);
        messageConverter.setClassMapper(classMapper());
        return messageConverter;
    }

    private ClassMapper classMapper() {
        Map<String, Class<?>> idClassMapping = new HashMap<>();
        idClassMapping.put("amqp.random_service.v1.reply", RandomServiceMessage.class);
        DefaultClassMapper classMapper = new DefaultClassMapper();
        classMapper.setIdClassMapping(idClassMapping);
        return classMapper;
    }

    @Bean
    public SimpleMessageListenerContainer messageListenerContainer(final ConnectionFactory amqpConnectionFactory, final Queue consumerQueue, final Jackson2JsonMessageConverter jackson2JsonMessageConverter, final AmqpAdapterService amqpAdapterService) {
        SimpleMessageListenerContainer messageListenerContainer = new SimpleMessageListenerContainer(amqpConnectionFactory);
        messageListenerContainer.setQueues(consumerQueue);
        messageListenerContainer.setMessageListener(messageListener(amqpAdapterService, jackson2JsonMessageConverter));
        return messageListenerContainer;
    }

    private MessageListener messageListener(AmqpAdapterService amqpAdapterService, Jackson2JsonMessageConverter jackson2JsonMessageConverter) {
        final MessageListenerAdapter messageListenerAdapter = new MessageListenerAdapter(amqpAdapterService, "messageReceived");
        messageListenerAdapter.setMessageConverter(jackson2JsonMessageConverter);
        return messageListenerAdapter;
    }
}

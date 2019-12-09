package com.test.amqp.adapter.configuration;

import com.test.amqp.adapter.service.AmqpAdapterService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BootstrapConfiguration {
    @Bean
    public AmqpAdapterService amqpAdapterService() {
        return new AmqpAdapterService();
    }
}

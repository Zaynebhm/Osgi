package com.test.amqp.adapter;

import com.test.amqp.adapter.configuration.AmqpConfiguration;
import com.test.amqp.adapter.configuration.BootstrapConfiguration;
import com.test.amqp.adapter.configuration.JacksonConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.annotation.Import;

@EnableAutoConfiguration(exclude = {
        SecurityAutoConfiguration.class
})
@Import({
        JacksonConfiguration.class,
        BootstrapConfiguration.class,
        AmqpConfiguration.class
})
@SpringBootConfiguration
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}


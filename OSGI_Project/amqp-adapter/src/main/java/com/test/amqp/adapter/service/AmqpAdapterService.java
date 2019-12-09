package com.test.amqp.adapter.service;

import com.test.amqp.adapter.model.RandomServiceMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class AmqpAdapterService {
    public void messageReceived(Message message) {
        log.info("MESSAGE: {}", message);
    }

    public void messageReceived(RandomServiceMessage randomServiceMessage) {
        log.info("RANDOM_SERVICE_MESSAGE: {}", randomServiceMessage);
    }
}

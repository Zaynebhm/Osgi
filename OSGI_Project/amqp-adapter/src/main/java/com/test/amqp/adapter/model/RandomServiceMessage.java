package com.test.amqp.adapter.model;

import lombok.Data;

@Data
public class RandomServiceMessage {
    private String key;
    private Object value;
}

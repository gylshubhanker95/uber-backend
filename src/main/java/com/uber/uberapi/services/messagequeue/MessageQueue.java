package com.uber.uberapi.services.messagequeue;

import org.springframework.stereotype.Service;

@Service
public interface MessageQueue {
    void sendMessage(String topic,MQMessage message);
    MQMessage consumeMessage(String topic);
}

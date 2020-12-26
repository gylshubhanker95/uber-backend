package com.uber.uberapi.services.messagequeue;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

public class KafakeService implements MessageQueue{

    private final Map<String, Queue<MQMessage>> topics = new HashMap<>();

    @Override
    public void sendMessage(String topic, MQMessage message) {
        System.out.printf("Kafake: appended to %s %s",topic,message.toString());
        topics.putIfAbsent(topic,new LinkedList<>());
        topics.get(topic).add(message);
    }

    @Override
    public MQMessage consumeMessage(String topic) {
        MQMessage message = topics.getOrDefault(topic,new LinkedList<>()).poll();
        System.out.printf("Kafake consuming from %s: %s",topic,message.toString());
        return message;
    }
}

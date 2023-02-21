package org.gnori.mailsenderbot.service.impl;

import org.gnori.mailsenderbot.model.Message;
import org.gnori.mailsenderbot.service.QueueManager;
import org.springframework.stereotype.Component;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

@Component
public class QueueManagerImpl implements QueueManager {
    public final Queue<Message> sendQueue = new ConcurrentLinkedQueue<>();

    @Override
    public void addInQueue(Message message) {
        sendQueue.add(message);
    }

    @Override
    public Message pollFromQueue() {
        return sendQueue.poll();
    }
}

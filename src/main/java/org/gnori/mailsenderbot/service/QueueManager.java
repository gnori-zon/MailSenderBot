package org.gnori.mailsenderbot.service;

import org.gnori.mailsenderbot.model.Message;

public interface QueueManager {
    void addInQueue(Message message);
    Message pollFromQueue();
}

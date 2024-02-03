package org.gnori.client.telegram.service.message;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.gnori.data.model.Message;
import org.gnori.data.model.SendMode;
import org.springframework.stereotype.Service;

@Service
public class MessageStorageImpl implements MessageStorage {

    private final Map<Long, Message> messages = new HashMap<>();

    @Override
    public Message getMessage(Long accountId) {
        return messages.getOrDefault(accountId, emptyMessageOf(accountId));
    }

    @Override
    public void updateMessage(Long accountId, Message message) {
        messages.put(accountId, message);
    }

    @Override
    public boolean clearMessage(Long accountId) {
        return messages.remove(accountId) != null;
    }

    private Message emptyMessageOf(Long accountId) {

        return new Message(
                accountId,
                SendMode.ANONYMOUSLY,
                "",
                "",
                null,
                Collections.emptyList(),
                1,
                null
        );
    }
}

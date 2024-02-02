package org.gnori.client.telegram.service.message;

import org.gnori.data.model.Message;

public interface MessageStorage {

    Message getMessage(Long accountId);

    void updateMessage(Long accountId, Message message);

    boolean clearMessage(Long accountId);
}

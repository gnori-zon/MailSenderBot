package org.gnori.client.telegram.service.impl.message;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.gnori.data.model.Message;
import org.gnori.data.model.SendMode;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

@Service
public class MessageRepositoryService {

    private final Map<Long, Message> messages= new HashMap<>();

    public Message getMessage(Long accountId){

        return messages.getOrDefault(
                accountId,
                new Message(accountId, SendMode.ANONYMOUSLY, null, null, null, Collections.emptyList(), 1, null)
        );
    }

    public void putMessage(Long id,Message message){
        messages.put(id, message);
    }

    public boolean removeMessage(Long id){
        return messages.remove(id) != null;
    }
}

package org.gnori.store.dao;

import java.util.HashMap;
import java.util.Map;
import org.gnori.store.model.Message;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

/**
 * {@link Repository} for {@link Message} model.
 */
@Service
public class MessageRepositoryService {
    private final Map<Long, Message> messages= new HashMap<>();

    public Message getMessage(Long id){
        return messages.get(id);
    }
    public void putMessage(Long id,Message message){
        messages.put(id, message);
    }
    public boolean removeMessage(Long id){
        return messages.remove(id)!=null;
    }
}

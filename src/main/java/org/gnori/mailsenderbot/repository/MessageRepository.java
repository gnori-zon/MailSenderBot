package org.gnori.mailsenderbot.repository;

import org.gnori.mailsenderbot.model.Message;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

@Repository
public class MessageRepository{
    private Map<Long, Message> messages= new HashMap<>();

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

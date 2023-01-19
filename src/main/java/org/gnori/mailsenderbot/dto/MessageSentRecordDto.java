package org.gnori.mailsenderbot.dto;

import org.gnori.mailsenderbot.entity.MessageSentRecord;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

public class MessageSentRecordDto {
    private final Long countMessages;
    private final String sendDate;
    public MessageSentRecordDto(MessageSentRecord messageSentRecord) {
        this.countMessages = messageSentRecord.getCountMessages();
        this.sendDate = messageSentRecord.getSendDate().toString();

    }

    public Long getCountMessages() {
        return countMessages;
    }

    public String getSendDate() {
        return sendDate;
    }
}

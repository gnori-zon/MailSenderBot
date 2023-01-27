package org.gnori.mailsenderbot.dto;

import org.gnori.mailsenderbot.entity.MessageSentRecord;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.Objects;

public class MessageSentRecordDto {
    private final Integer countMessages;
    private final String sendDate;
    public MessageSentRecordDto(MessageSentRecord messageSentRecord) {
        this.countMessages = messageSentRecord.getCountMessages();
        this.sendDate = messageSentRecord.getSendDate().toString();

    }

    public Integer getCountMessages() {
        return countMessages;
    }

    public String getSendDate() {
        return sendDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MessageSentRecordDto that = (MessageSentRecordDto) o;
        return Objects.equals(getCountMessages(), that.getCountMessages()) && Objects.equals(getSendDate(), that.getSendDate());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getCountMessages(), getSendDate());
    }
}

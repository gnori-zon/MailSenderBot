package org.gnori.data.dto;

import java.util.Objects;
import org.gnori.store.entity.MessageSentRecord;

/**
 * dto for {@link MessageSentRecord} entity.
 */
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

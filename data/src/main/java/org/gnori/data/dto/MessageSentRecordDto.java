package org.gnori.data.dto;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.gnori.store.entity.MessageSentRecord;

import java.time.format.DateTimeFormatter;

@Getter
@EqualsAndHashCode
public class MessageSentRecordDto {

    private final String sendDate;
    private final Integer countMessages;

    public MessageSentRecordDto(MessageSentRecord messageSentRecord) {

        this.sendDate = messageSentRecord.getSendDate().format(DateTimeFormatter.RFC_1123_DATE_TIME);
        this.countMessages = messageSentRecord.getCountMessages();
    }
}

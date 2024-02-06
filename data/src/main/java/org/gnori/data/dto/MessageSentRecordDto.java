package org.gnori.data.dto;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.gnori.data.entity.MessageSentRecord;

import java.time.format.DateTimeFormatter;

@Getter
@EqualsAndHashCode
public class MessageSentRecordDto {

    private final String sentDate;
    private final Integer countMessages;

    public MessageSentRecordDto(MessageSentRecord messageSentRecord) {

        this.sentDate = messageSentRecord.getSentDate().format(DateTimeFormatter.RFC_1123_DATE_TIME);
        this.countMessages = messageSentRecord.getCountMessages();
    }
}

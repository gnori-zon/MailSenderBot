package org.gnori.data.dto;

import org.gnori.data.model.Message;

import java.time.format.DateTimeFormatter;
import java.util.List;

public record MessageDto(
        String title,
        String text,
        boolean hasAnnex,
        List<String> recipients,
        int countForRecipient,
        String sentDate
) {

    public MessageDto(Message message) {

        this(
                message.title(),
                message.text(),
                message.hasAnnex(),
                message.recipients(),
                message.countForRecipient(),
                message.hasSentDate() ? message.sentDate().format(DateTimeFormatter.RFC_1123_DATE_TIME) : "now"
        );
    }
}

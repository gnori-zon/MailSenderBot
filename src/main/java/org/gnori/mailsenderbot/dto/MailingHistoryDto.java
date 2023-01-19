package org.gnori.mailsenderbot.dto;

import org.gnori.mailsenderbot.entity.MailingHistory;
import org.gnori.mailsenderbot.entity.MessageSentRecord;

import java.util.ArrayList;
import java.util.List;

public class MailingHistoryDto {
    private final List<MessageSentRecordDto> mailingList;

    public MailingHistoryDto(MailingHistory mailingHistory) {
        List<MessageSentRecordDto> mailingList = new ArrayList<>();
        for (MessageSentRecord messageSentRecord : mailingHistory.getMailingList()) {
            mailingList.add(new MessageSentRecordDto(messageSentRecord));
        }
        this.mailingList = mailingList;
    }

    public List<MessageSentRecordDto> getMailingList() {
        return mailingList;
    }
}

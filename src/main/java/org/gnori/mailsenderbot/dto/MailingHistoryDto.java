package org.gnori.mailsenderbot.dto;

import org.gnori.mailsenderbot.entity.MailingHistory;
import org.gnori.mailsenderbot.entity.MessageSentRecord;
import org.gnori.mailsenderbot.entity.enums.StateMessage;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
/**
 * dto for {@link MailingHistory} entity.
 */
public class MailingHistoryDto {
    private final StateMessage stateLastMessage;

    private final List<MessageSentRecordDto> mailingList;

    public MailingHistoryDto(MailingHistory mailingHistory) {
        List<MessageSentRecordDto> mailingList = new ArrayList<>();
        for (MessageSentRecord messageSentRecord : mailingHistory.getMailingList()) {
            mailingList.add(new MessageSentRecordDto(messageSentRecord));
        }
        this.mailingList = mailingList;
        this.stateLastMessage = mailingHistory.getStateLastMessage();
    }

    public List<MessageSentRecordDto> getMailingList() {
        return mailingList;
    }
    public StateMessage getStateLastMessage(){
        return stateLastMessage;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MailingHistoryDto that = (MailingHistoryDto) o;
        return Objects.equals(getMailingList(), that.getMailingList());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getMailingList());
    }
}

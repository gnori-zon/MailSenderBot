package org.gnori.data.dto;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.gnori.data.entity.MailingHistory;
import org.gnori.data.entity.enums.StateMessage;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@EqualsAndHashCode(of = "mailingList")
public class MailingHistoryDto {

    private final StateMessage stateLastMessage;
    private final List<MessageSentRecordDto> mailingList;

    public MailingHistoryDto(MailingHistory mailingHistory) {

        this.stateLastMessage = mailingHistory.getStateLastMessage();
        this.mailingList = mailingHistory.getMailingList().stream()
                .map(MessageSentRecordDto::new)
                .collect(Collectors.toList());;
    }
}

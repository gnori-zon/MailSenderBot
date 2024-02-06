package org.gnori.data.service.mailing;

import org.gnori.data.entity.MailingHistory;
import org.gnori.data.entity.enums.StateMessage;

import java.util.Optional;

public interface MailingHistoryService {

    Optional<MailingHistory> getMailingHistoryById(Long id);
    void updateStateMessageByAccountId(Long id, StateMessage stateMessage);
}

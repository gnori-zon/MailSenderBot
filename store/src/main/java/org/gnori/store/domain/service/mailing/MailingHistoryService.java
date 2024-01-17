package org.gnori.store.domain.service.mailing;

import org.gnori.store.entity.MailingHistory;
import org.gnori.store.entity.enums.StateMessage;

import java.util.Optional;

public interface MailingHistoryService {

    Optional<MailingHistory> getMailingHistoryById(Long id);
    void updateStateMessageByAccountId(Long id, StateMessage stateMessage);
}

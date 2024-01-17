package org.gnori.store.domain.service.mailing.impl;

import lombok.RequiredArgsConstructor;
import org.gnori.store.domain.service.mailing.MailingHistoryService;
import org.gnori.store.entity.MailingHistory;
import org.gnori.store.entity.enums.StateMessage;
import org.gnori.store.repository.MailingHistoryRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MailingHistoryServiceImpl implements MailingHistoryService {

    private final MailingHistoryRepository mailingHistoryRepository;

    @Override
    public Optional<MailingHistory> getMailingHistoryById(Long id) {
        return mailingHistoryRepository.findByAccountId(id);
    }

    @Override
    public void updateStateMessageByAccountId(Long accountId, StateMessage stateMessage) {
        mailingHistoryRepository.updateStateLastMessageByAccountId(accountId, stateMessage.name());
    }
}

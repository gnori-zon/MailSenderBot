package org.gnori.data.service.mailing.impl;

import lombok.RequiredArgsConstructor;
import org.gnori.data.entity.MailingHistory;
import org.gnori.data.repository.MailingHistoryRepository;
import org.gnori.data.service.mailing.MailingHistoryService;
import org.gnori.data.entity.enums.StateMessage;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MailingHistoryServiceImpl implements MailingHistoryService {

    private final MailingHistoryRepository mailingHistoryRepository;

    @Override
    public Optional<MailingHistory> getMailingHistoryByAccountId(Long id) {
        return mailingHistoryRepository.findByAccountId(id);
    }

    @Override
    public void updateStateMessageByAccountId(Long accountId, StateMessage stateMessage) {
        mailingHistoryRepository.updateStateLastMessageByAccountId(accountId, stateMessage.name());
    }
}

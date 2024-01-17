package org.gnori.store.domain.service.messagesentrecord.impl;

import lombok.RequiredArgsConstructor;
import org.gnori.store.domain.service.messagesentrecord.MessageSentRecordService;
import org.gnori.store.entity.MailingHistory;
import org.gnori.store.entity.MessageSentRecord;
import org.gnori.store.repository.AccountRepository;
import org.gnori.store.repository.MailingHistoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MessageSentRecordServiceImpl implements MessageSentRecordService {

    private static final int MESSAGE_SENT_RECORD_OVERFLOW_SIZE = 30;

    private final MailingHistoryRepository mailingHistoryRepository;
    private final AccountRepository accountRepository;

    @Transactional
    @Override
    public void addMessageSentRecordByAccountId(Long accountId, MessageSentRecord messageSentRecord) {

        mailingHistoryRepository.findByAccountId(accountId)
                .ifPresentOrElse(
                        mailingHistory -> cleanOldMessageSentRecordsAndAdd(mailingHistory, messageSentRecord),
                        () -> createNewHistoryAndAdd(accountId, messageSentRecord)
                );
    }

    private void cleanOldMessageSentRecordsAndAdd(MailingHistory mailingHistory, MessageSentRecord messageSentRecord) {

        final List<MessageSentRecord> messageSentRecords = mailingHistory.getMailingList().stream()
                .sorted(Comparator.reverseOrder())
                .limit(MESSAGE_SENT_RECORD_OVERFLOW_SIZE)
                .collect(Collectors.toList());

        messageSentRecords.add(messageSentRecord);

        mailingHistory.setMailingList(messageSentRecords);
        mailingHistoryRepository.save(mailingHistory);
    }

    private void createNewHistoryAndAdd(Long accountId, MessageSentRecord messageSentRecord) {

        final MailingHistory mailingHistory = mailingHistoryRepository.saveAndFlush(mailingHistoryOf(List.of(messageSentRecord)));
        accountRepository.updateMailingHistoryId(accountId, mailingHistory.getId());
    }

    private MailingHistory mailingHistoryOf(List<MessageSentRecord> records) {

        return MailingHistory.builder()
                .mailingList(records)
                .build();
    }
}

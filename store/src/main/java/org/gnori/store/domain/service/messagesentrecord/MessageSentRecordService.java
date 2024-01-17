package org.gnori.store.domain.service.messagesentrecord;

import org.gnori.store.entity.MessageSentRecord;

public interface MessageSentRecordService {
    void addMessageSentRecordByAccountId(Long accountId, MessageSentRecord messageSentRecord);
}

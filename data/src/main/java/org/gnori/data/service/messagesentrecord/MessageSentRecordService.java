package org.gnori.data.service.messagesentrecord;

import org.gnori.data.entity.MessageSentRecord;

public interface MessageSentRecordService {
    void addMessageSentRecordByAccountId(Long accountId, MessageSentRecord messageSentRecord);
}

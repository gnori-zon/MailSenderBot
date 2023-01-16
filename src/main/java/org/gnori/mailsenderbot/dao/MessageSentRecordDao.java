package org.gnori.mailsenderbot.dao;

import org.gnori.mailsenderbot.entity.MessageSentRecord;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageSentRecordDao extends JpaRepository<MessageSentRecord,Long> {
}

package org.gnori.mailsenderbot.dao;

import org.gnori.mailsenderbot.entity.MailingHistory;
import org.gnori.mailsenderbot.entity.MessageSentRecord;
import org.springframework.data.jpa.repository.JpaRepository;
/**
 * dao for {@link MessageSentRecord} entity.
 */
public interface MessageSentRecordDao extends JpaRepository<MessageSentRecord,Long> {
}

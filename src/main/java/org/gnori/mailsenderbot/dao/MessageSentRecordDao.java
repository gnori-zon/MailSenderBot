package org.gnori.mailsenderbot.dao;

import org.gnori.mailsenderbot.entity.MessageSentRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;



public interface MessageSentRecordDao extends JpaRepository<MessageSentRecord,Long> {
}

package org.gnori.mailsenderbot.dao;

import org.gnori.mailsenderbot.entity.MailingHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MailingHistoryDao extends JpaRepository<MailingHistory,Long> {
}

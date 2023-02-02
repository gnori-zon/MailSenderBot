package org.gnori.mailsenderbot.dao;

import org.gnori.mailsenderbot.entity.MailingHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MailingHistoryDao extends JpaRepository<MailingHistory,Long> {

     Optional<MailingHistory> findByAccount_Id(Long id);

}

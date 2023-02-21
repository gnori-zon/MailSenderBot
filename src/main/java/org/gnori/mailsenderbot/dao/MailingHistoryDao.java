package org.gnori.mailsenderbot.dao;

import org.gnori.mailsenderbot.entity.MailingHistory;
import org.gnori.mailsenderbot.entity.enums.StateMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
/**
 * dao for {@link MailingHistory} entity.
 */
public interface MailingHistoryDao extends JpaRepository<MailingHistory,Long> {

     Optional<MailingHistory> findByAccount_Id(Long id);
     @Transactional
     @Modifying
     @Query("update MailingHistory a set a.stateLastMessage = :stateLastMessage WHERE a.id = :id")
     void updateStateLastMessage(@Param("id") Long id, @Param("stateLastMessage") StateMessage stateLastMessage);


}

package org.gnori.store.repository;

import java.util.Optional;
import org.gnori.store.entity.MailingHistory;
import org.gnori.store.entity.enums.StateMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
/**
 * dao for {@link MailingHistory} entity.
 */
public interface MailingHistoryRepository extends JpaRepository<MailingHistory,Long> {
     @Transactional
     Optional<MailingHistory> findByAccount_Id(Long id);
     @Transactional
     @Modifying
     @Query("update MailingHistory a set a.stateLastMessage = :stateLastMessage WHERE a.id = :id")
     void updateStateLastMessage(@Param("id") Long id, @Param("stateLastMessage") StateMessage stateLastMessage);


}

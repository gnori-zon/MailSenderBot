package org.gnori.data.repository;

import org.gnori.data.entity.MailingHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface MailingHistoryRepository extends JpaRepository<MailingHistory,Long> {

     @Transactional(readOnly = true)
     Optional<MailingHistory> findByAccountId(Long id);

     @Modifying
     @Transactional(propagation = Propagation.REQUIRED)
     @Query(value = """
             with account_mailing_history as (
                  select account.mailing_history_id as id
                  from account
                  where account.id = :accountId
             )
             
             update mailing_history history
             set history.state_last_message = :newStateLastMessage
             where history.id = account_mailing_history.id
             """, nativeQuery = true)
     void updateStateLastMessageByAccountId(Long accountId, String newStateLastMessage);
}

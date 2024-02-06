package org.gnori.data.repository;

import org.gnori.data.entity.Account;
import org.gnori.data.entity.enums.State;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account,Long> {

    Optional<Account> findByChatId(Long chatId);

    @Modifying
    @Transactional(propagation = Propagation.REQUIRED)
    @Query("""
            update Account a
                set a.state = :state
            where a.id = :id
            """)
    void updateStateById(Long id, State state);

    @Modifying
    @Transactional(propagation = Propagation.REQUIRED)
    @Query("""
            update Account a
                set a.keyForMail = :keyForMail
            where a.id = :id
             """)
    void updateKeyForMailById(Long id, String keyForMail);

    @Modifying
    @Transactional(propagation = Propagation.REQUIRED)
    @Query("""
            update Account a
                set a.email = :mail
            where a.id = :id
            """)
    void updateMailById(Long id, String mail);

    @Modifying
    @Transactional(propagation = Propagation.REQUIRED)
    @Query(value = """
             update account
               set mailing_history_id = :mailingHistoryId
             where account.id = :id
             """, nativeQuery = true)
    void updateMailingHistoryId(Long id, Long accountId);
}

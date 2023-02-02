package org.gnori.mailsenderbot.dao;

import org.gnori.mailsenderbot.entity.Account;
import org.gnori.mailsenderbot.entity.enums.State;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface AccountDao extends JpaRepository<Account,Long> {
    Account findFirstByEmailIgnoreCase(String email);
    @Transactional
    @Modifying
    @Query("update Account a set a.state = :state WHERE a.id = :id")
    void updateStateById(@Param("id") Long id, @Param("state") State state);
    @Transactional
    @Modifying
    @Query("update Account a set a.keyForMail = :keyForMail WHERE a.id = :id")
    void updateKeyForMailById(@Param("id") Long id, @Param("keyForMail") String keyForMail);
    @Transactional
    @Modifying
    @Query("update Account a set a.email = :mail WHERE a.id = :id")
    void updateMailById(@Param("id") Long id, @Param("mail") String mail);


}

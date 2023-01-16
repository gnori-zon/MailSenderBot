package org.gnori.mailsenderbot.dao;

import org.gnori.mailsenderbot.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountDao extends JpaRepository<Account,Long> {
    Account findFirstByEmailIgnoreCase(String email);
}

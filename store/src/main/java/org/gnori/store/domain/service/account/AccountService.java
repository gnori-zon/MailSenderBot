package org.gnori.store.domain.service.account;

import org.gnori.store.entity.Account;
import org.gnori.store.entity.enums.State;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.Optional;

public interface AccountService {

    Optional<Account> findAccountById(Long id);
    void saveAccount(Account account);
    void updateStateById(Long id, State keyForMailPending);
    void updateKeyForMailById(Long id, String newKey);
    void updateMailById(Long id, String mail) throws DataIntegrityViolationException;
}

package org.gnori.data.service.account;

import org.gnori.data.entity.Account;
import org.gnori.data.entity.enums.State;
import org.gnori.data.flow.Empty;
import org.gnori.data.flow.Result;

import java.util.Optional;

public interface AccountService {

    Optional<Account> findAccountById(Long id);

    Optional<Account> findByChatId(Long chatId);

    void updateStateById(Long id, State state);

    void updateKeyForMailById(Long id, String newKey);

    Result<Empty, AccountServiceFailure> updateMailById(Long id, String mail);

    Account save(Account account);
}

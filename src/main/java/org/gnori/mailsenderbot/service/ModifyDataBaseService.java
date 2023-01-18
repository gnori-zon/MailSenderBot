package org.gnori.mailsenderbot.service;

import org.gnori.mailsenderbot.dto.AccountDto;
import org.gnori.mailsenderbot.entity.Account;

public interface ModifyDataBaseService {
    AccountDto findAccountById(Long id);

    void saveAccount(Account account);
}

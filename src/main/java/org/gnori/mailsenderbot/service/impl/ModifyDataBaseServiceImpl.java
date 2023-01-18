package org.gnori.mailsenderbot.service.impl;

import lombok.extern.log4j.Log4j;
import org.gnori.mailsenderbot.dao.AccountDao;
import org.gnori.mailsenderbot.dto.AccountDto;
import org.gnori.mailsenderbot.entity.Account;
import org.gnori.mailsenderbot.service.ModifyDataBaseService;
import org.springframework.stereotype.Service;

@Service
public class ModifyDataBaseServiceImpl implements ModifyDataBaseService {
    static final String ERROR_TEXT = "Error occurred: ";
    private final AccountDao accountDao;

    public ModifyDataBaseServiceImpl(AccountDao accountDao) {
        this.accountDao = accountDao;
    }

    @Override
    public AccountDto findAccountById(Long id) {
        var optionalAccount = accountDao.findById(id);
        if(optionalAccount.isPresent()) {
            return new AccountDto(optionalAccount.get());
        }
        return null;
    }

    @Override
    public void saveAccount(Account account) {
        accountDao.save(account);
    }
}

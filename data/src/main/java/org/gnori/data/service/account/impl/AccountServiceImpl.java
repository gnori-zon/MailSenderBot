package org.gnori.data.service.account.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.gnori.data.flow.Empty;
import org.gnori.data.flow.Result;
import org.gnori.data.service.account.AccountService;
import org.gnori.data.service.account.AccountServiceFailure;
import org.gnori.data.entity.Account;
import org.gnori.data.entity.enums.State;
import org.gnori.data.repository.AccountRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Log4j2
@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;

    @Override
    public Optional<Account> findByChatId(Long chatId) {
        return accountRepository.findByChatId(chatId);
    }

    @Override
    public Optional<Account> findAccountById(Long id) {
        return accountRepository.findById(id);
    }

    @Override
    public void updateStateById(Long id, State state) {
        accountRepository.updateStateById(id, state);
    }

    @Override
    public void updateKeyForMailById(Long id, String newKey) {
        accountRepository.updateKeyForMailById(id, newKey);
    }

    @Override
    public Result<Empty, AccountServiceFailure> updateMailById(Long id, String mail) {

        try {

            accountRepository.updateMailById(id, mail);

            return Result.success(Empty.INSTANCE);
        } catch (DataIntegrityViolationException e) {

            log.error("mail: {}, already bind to account", mail);
            return Result.failure(AccountServiceFailure.UNIQUE_CONSTRAINT_FAILURE);
        }
    }

    @Override
    public Account save(Account account) {
        return accountRepository.saveAndFlush(account);
    }
}

package org.gnori.store.domain.service.account.impl;

import lombok.RequiredArgsConstructor;
import org.gnori.store.domain.service.account.AccountService;
import org.gnori.store.entity.Account;
import org.gnori.store.entity.enums.State;
import org.gnori.store.repository.AccountRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;

    @Override
    public Optional<Account> findAccountById(Long id) {
        return accountRepository.findById(id);
    }

    @Override
    public void saveAccount(Account account) {
        accountRepository.save(account);
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
    public void updateMailById(Long id, String mail) throws DataIntegrityViolationException {
        accountRepository.updateMailById(id, mail);
    }
}

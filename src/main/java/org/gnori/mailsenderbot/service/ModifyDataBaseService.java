package org.gnori.mailsenderbot.service;

import org.gnori.mailsenderbot.dto.AccountDto;
import org.gnori.mailsenderbot.dto.MailingHistoryDto;
import org.gnori.mailsenderbot.entity.Account;
import org.gnori.mailsenderbot.entity.MessageSentRecord;
import org.gnori.mailsenderbot.entity.enums.State;
import org.springframework.dao.DataIntegrityViolationException;

public interface ModifyDataBaseService {
    AccountDto findAccountById(Long id);

    void saveAccount(Account account);
    MailingHistoryDto getMailingHistoryById(Long id);

    void updateStateById(Long id, State keyForMailPending);

    void updateKeyForMailById(Long id, String newKey);

    void updateMailById(Long id, String mail) throws DataIntegrityViolationException;

    void addMessageSentRecord(Long id, MessageSentRecord message);
}

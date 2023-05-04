package org.gnori.store.dao;

import java.util.Optional;
import org.gnori.store.entity.Account;
import org.gnori.store.entity.MailingHistory;
import org.gnori.store.entity.MessageSentRecord;
import org.gnori.store.entity.enums.State;
import org.gnori.store.entity.enums.StateMessage;
import org.springframework.dao.DataIntegrityViolationException;

/**
 * Service for changing account data
 */
public interface ModifyDataBaseService {
    Optional<Account> findAccountById(Long id);
    void saveAccount(Account account);
    Optional<MailingHistory> getMailingHistoryById(Long id);
    void updateStateMessageById(Long id, StateMessage stateMessage);

    void updateStateById(Long id, State keyForMailPending);

    void updateKeyForMailById(Long id, String newKey);

    void updateMailById(Long id, String mail) throws DataIntegrityViolationException;

    void addMessageSentRecord(Long id, MessageSentRecord message);
}

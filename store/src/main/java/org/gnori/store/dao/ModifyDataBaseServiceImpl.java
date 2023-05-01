package org.gnori.store.dao;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.extern.log4j.Log4j2;
import org.gnori.store.entity.Account;
import org.gnori.store.entity.MailingHistory;
import org.gnori.store.entity.MessageSentRecord;
import org.gnori.store.entity.enums.State;
import org.gnori.store.entity.enums.StateMessage;
import org.gnori.store.repository.AccountRepository;
import org.gnori.store.repository.MailingHistoryRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

/**
 * Implementation service {@link ModifyDataBaseService}
 */
@Log4j2
@Service
public class ModifyDataBaseServiceImpl implements ModifyDataBaseService {
    private final AccountRepository accountRepository;
    private final MailingHistoryRepository mailingHistoryRepository;

    public ModifyDataBaseServiceImpl(AccountRepository accountRepository,
                                     MailingHistoryRepository mailingHistoryRepository) {
        this.accountRepository = accountRepository;
        this.mailingHistoryRepository = mailingHistoryRepository;

    }
    @Override
    public Optional<MailingHistory> getMailingHistoryById(Long id){
        var optionalMailingHistory = mailingHistoryRepository.findByAccount_Id(id);
        if(optionalMailingHistory.isPresent()) {
            var mailingList = optionalMailingHistory.get().getMailingList();
            if (mailingList != null) {
                return optionalMailingHistory;
            }
        }
        return Optional.empty();
    }

    @Override
    public void updateStateById(Long id, State state) {
        accountRepository.updateStateById(id, state);
    }
    @Override
    public void updateStateMessageById(Long id, StateMessage stateMessage) {
        var account = accountRepository.findById(id).get();
        mailingHistoryRepository.updateStateLastMessage(account.getMailingHistory().getId(), stateMessage);
    }

    @Override
    public void updateKeyForMailById(Long id, String newKey) {
        accountRepository.updateKeyForMailById(id, newKey);
    }

    @Override
    public void updateMailById(Long id, String mail) throws DataIntegrityViolationException {
        accountRepository.updateMailById(id, mail);
    }

    @Override
    public void addMessageSentRecord(Long id, MessageSentRecord message) {
        var optionalMailingHistory = mailingHistoryRepository.findByAccount_Id(id);
        MailingHistory mailingHistory;
        if(optionalMailingHistory.isPresent()) {
            mailingHistory = optionalMailingHistory.get();
            var records = mailingHistory.getMailingList().stream().sorted().collect(Collectors.toList());
            var overflow = records.size()-30;
            if(overflow > 0){
                records = records.stream().skip(overflow).collect(Collectors.toList());
            }
            records.add(message);
            mailingHistory.setMailingList(records);
            mailingHistoryRepository.save(mailingHistory);
        }else{
            var account = accountRepository.findById(id).get();
            account.setMailingHistory(
                    MailingHistory.builder()
                    .mailingList(List.of(message))
                    .build());
            accountRepository.save(account);
        }

    }
    @Override
    public Optional<Account> findAccountById(Long id){
        return accountRepository.findById(id);
    }

    @Override
    public void saveAccount(Account account) {
        accountRepository.save(account);
    }
}

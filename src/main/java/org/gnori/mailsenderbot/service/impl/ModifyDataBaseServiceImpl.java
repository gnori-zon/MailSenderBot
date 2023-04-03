package org.gnori.mailsenderbot.service.impl;

import lombok.extern.log4j.Log4j;
import org.gnori.mailsenderbot.dao.AccountDao;
import org.gnori.mailsenderbot.dao.MailingHistoryDao;
import org.gnori.mailsenderbot.dto.AccountDto;
import org.gnori.mailsenderbot.dto.MailingHistoryDto;
import org.gnori.mailsenderbot.entity.Account;
import org.gnori.mailsenderbot.entity.MailingHistory;
import org.gnori.mailsenderbot.entity.MessageSentRecord;
import org.gnori.mailsenderbot.entity.enums.State;
import org.gnori.mailsenderbot.entity.enums.StateMessage;
import org.gnori.mailsenderbot.service.ModifyDataBaseService;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
/**
 * Implementation service {@link ModifyDataBaseService}
 */
@Log4j
@Service
public class ModifyDataBaseServiceImpl implements ModifyDataBaseService {
    private final AccountDao accountDao;
    private final MailingHistoryDao mailingHistoryDao;

    public ModifyDataBaseServiceImpl(AccountDao accountDao,
                                     MailingHistoryDao mailingHistoryDao) {
        this.accountDao = accountDao;
        this.mailingHistoryDao = mailingHistoryDao;

    }

    public AccountDto findAccountDTOById(Long id) {
        var optionalAccount = accountDao.findById(id);
        if(optionalAccount.isPresent()) {
            return new AccountDto(optionalAccount.get());
        }
        return null;
    }
    @Override
    public MailingHistoryDto getMailingHistoryById(Long id){
        var optionalMailingHistory = mailingHistoryDao.findByAccount_Id(id);
        if(optionalMailingHistory.isPresent()) {
            var mailingList = optionalMailingHistory.get().getMailingList();
            if (mailingList != null) {
                return new MailingHistoryDto(optionalMailingHistory.get());
            }
        }
        return null;
    }

    @Override
    public void updateStateById(Long id, State state) {
        accountDao.updateStateById(id, state);
    }
    @Override
    public void updateStateMessageById(Long id, StateMessage stateMessage) {
        var account = accountDao.findById(id).get();
        mailingHistoryDao.updateStateLastMessage(account.getMailingHistory().getId(), stateMessage);
    }

    @Override
    public void updateKeyForMailById(Long id, String newKey) {
        accountDao.updateKeyForMailById(id, newKey);
    }

    @Override
    public void updateMailById(Long id, String mail) throws DataIntegrityViolationException {
        accountDao.updateMailById(id, mail);
    }

    @Override
    public void addMessageSentRecord(Long id, MessageSentRecord message) {
        var optionalMailingHistory = mailingHistoryDao.findByAccount_Id(id);
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
            mailingHistoryDao.save(mailingHistory);
        }else{
            var account = accountDao.findById(id).get();
            account.setMailingHistory(
                    MailingHistory.builder()
                    .mailingList(List.of(message))
                    .build());
            accountDao.save(account);
        }

    }
    @Override
    public Optional<Account> findAccountById(Long id){
        return accountDao.findById(id);
    }

    @Override
    public void saveAccount(Account account) {
        accountDao.save(account);
    }
}

package org.gnori.mailsenderbot.service.impl;

import org.gnori.mailsenderbot.command.commands.MailingHistoryCommand;
import org.gnori.mailsenderbot.dao.AccountDao;
import org.gnori.mailsenderbot.dao.MailingHistoryDao;
import org.gnori.mailsenderbot.dao.MessageSentRecordDao;
import org.gnori.mailsenderbot.dto.AccountDto;
import org.gnori.mailsenderbot.dto.MailingHistoryDto;
import org.gnori.mailsenderbot.entity.Account;
import org.gnori.mailsenderbot.entity.MailingHistory;
import org.gnori.mailsenderbot.entity.enums.State;
import org.gnori.mailsenderbot.service.ModifyDataBaseService;
import org.springframework.stereotype.Service;

@Service
public class ModifyDataBaseServiceImpl implements ModifyDataBaseService {
    static final String ERROR_TEXT = "Error occurred: ";
    private final AccountDao accountDao;
    private final MailingHistoryDao mailingHistoryDao;
    private final MessageSentRecordDao messageSentRecordDao;

    public ModifyDataBaseServiceImpl(AccountDao accountDao, MailingHistoryDao mailingHistoryDao, MessageSentRecordDao messageSentRecordDao) {
        this.accountDao = accountDao;
        this.mailingHistoryDao = mailingHistoryDao;
        this.messageSentRecordDao = messageSentRecordDao;

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
    public MailingHistoryDto getMailingHistoryById(Long id){
        var optionalMailingHistory = mailingHistoryDao.findById(id);
        if(optionalMailingHistory.isPresent()) {
            return new MailingHistoryDto(optionalMailingHistory.get());
        }
        return null;
    }

    @Override
    public void updateStateById(Long id, State state) {
        accountDao.updateStateById(id, state);
    }

    @Override
    public void updateKeyForMailById(Long id, String newKey) {
        accountDao.updateKeyForMailById(id, newKey);
    }

    @Override
    public void updateMailById(Long id, String mail) {
        accountDao.updateMailById(id, mail);
    }

    @Override
    public void saveAccount(Account account) {
        accountDao.save(account);
    }
}

package org.gnori.mailsenderbot.service;

import org.gnori.mailsenderbot.dao.AccountDao;
import org.gnori.mailsenderbot.dao.MailingHistoryDao;
import org.gnori.mailsenderbot.dao.MessageSentRecordDao;
import org.gnori.mailsenderbot.entity.Account;
import org.gnori.mailsenderbot.entity.MailingHistory;
import org.gnori.mailsenderbot.entity.enums.UserState;
import org.springframework.stereotype.Service;

@Service
public class TestService {
    private final AccountDao accountDao;
    private final MailingHistoryDao mailingHistoryDao;
    private final MessageSentRecordDao messageSentRecordDao;


    public TestService(AccountDao accountDao, MailingHistoryDao mailingHistoryDao, MessageSentRecordDao messageSentRecordDao) {
        this.accountDao = accountDao;
        this.mailingHistoryDao = mailingHistoryDao;
        this.messageSentRecordDao = messageSentRecordDao;
    }

    public void test() {

        MailingHistory mailHistory = new MailingHistory();
        mailHistory.setId(1L);

        Account account = Account.builder()
                .id(1L)
                .email("mail")
                .password("pass")
                .isActive(false)
                .mailForDistribution("mailFor")
                .keyForMail("keqy")
                .mailingHistory(mailHistory)
                .state(UserState.WAIT_FOR_EMAIL_STATE)
                .build();

        mailHistory.setAccount(account);




        System.out.println(account.toString());

        accountDao.save(account);

        System.out.println("-----------------saved-----------------");

    }

    public void test2(){
        System.out.println(" ");
        System.out.println("-----------------account-----------------");

        System.out.println(accountDao.findFirstByEmailIgnoreCase("mail"));
    }
}

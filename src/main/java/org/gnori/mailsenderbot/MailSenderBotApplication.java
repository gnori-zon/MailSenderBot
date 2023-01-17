package org.gnori.mailsenderbot;


import org.gnori.mailsenderbot.dao.AccountDao;
import org.gnori.mailsenderbot.dao.MailingHistoryDao;
import org.gnori.mailsenderbot.dao.MessageSentRecordDao;
import org.gnori.mailsenderbot.entity.Account;
import org.gnori.mailsenderbot.entity.MailingHistory;
import org.gnori.mailsenderbot.entity.MessageSentRecord;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@SpringBootApplication
public class MailSenderBotApplication implements ApplicationRunner {
    private final AccountDao accountDao;
    private final MessageSentRecordDao messageSentRecordDao;
    private final MailingHistoryDao mailingHistoryDao;

    public MailSenderBotApplication(AccountDao accountDao,
                                    MessageSentRecordDao messageSentRecordDao,
                                    MailingHistoryDao mailingHistoryDao) {
        this.accountDao = accountDao;
        this.messageSentRecordDao = messageSentRecordDao;
        this.mailingHistoryDao = mailingHistoryDao;
    }

    public static void main(String[] args) {
        SpringApplication.run(MailSenderBotApplication.class, args);
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {

        var record = new MessageSentRecord();
        record.setCountMessages(13L);
        var record3 = new MessageSentRecord();
        record3.setCountMessages(1111L);
        var history = new MailingHistory();
        history.setMailingList(List.of(record,record3));

        var record2 = new MessageSentRecord();
        record2.setCountMessages(167L);
        var history2 = new MailingHistory();
        history2.setMailingList(Collections.singletonList(record2));

        accountDao.save(Account.builder()
                        .id(2L)
                        .keyForMail("keyFor")
                        .email("mail")
                        .mailingHistory(history)
                .build());

        accountDao.save(Account.builder()
                .id(1L)
                .keyForMail("key2For")
                .email("mail2")
                .mailingHistory(history2)
                .build());

        var account = mailingHistoryDao.findByAccount_Id(2L);
        System.out.println(account.getMailingList().toString());
    }

}

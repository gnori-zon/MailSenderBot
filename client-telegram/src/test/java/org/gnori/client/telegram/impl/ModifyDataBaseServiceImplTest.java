package org.gnori.client.telegram.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.gnori.store.dao.ModifyDataBaseServiceImpl;
import org.gnori.store.entity.Account;
import org.gnori.store.entity.MailingHistory;
import org.gnori.store.entity.MessageSentRecord;
import org.gnori.store.entity.enums.State;
import org.gnori.store.entity.enums.StateMessage;
import org.gnori.store.repository.AccountRepository;
import org.gnori.store.repository.MailingHistoryRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

@DisplayName("Unit-level testing for ModifyDataBaseServiceImpl")
public class ModifyDataBaseServiceImplTest {
    private AccountRepository accountDao;
    private MailingHistoryRepository mailingHistoryDao;
    private ModifyDataBaseServiceImpl modifyDataBaseService;

    @BeforeEach
    public void init(){
        accountDao = Mockito.mock(AccountRepository.class);
        mailingHistoryDao = Mockito.mock(MailingHistoryRepository.class);
        modifyDataBaseService = new ModifyDataBaseServiceImpl(accountDao,mailingHistoryDao);
    }

    @Test
    public void findAccountByIdPositive(){
        var id = 12L;
        var email = "email@mail.ru";
        var keyForMail = "123ojjh23e12bas";
        var accountData = Account.builder()
                .id(id)
                .email(email)
                .keyForMail(keyForMail)
                .state(State.NOTHING_PENDING)
                .build();
        Mockito.when(accountDao.findById(id)).thenReturn(Optional.of(accountData));

        var account = modifyDataBaseService.findAccountById(id);

        Assertions.assertEquals(accountData, account.get());
    }

    @Test
    public void findAccountByIdNegative(){
        var id = 12L;

        Mockito.when(accountDao.findById(id)).thenReturn(Optional.empty());

        var account = modifyDataBaseService.findAccountById(id);

        Assertions.assertTrue(account.isEmpty());
    }

    @Test
    public void getMailingHistoryByIdPositive(){
        var id = 12L;
        var accountData = new Account();
        var mailingHistory = new MailingHistory(id,
                StateMessage.SUCCESS,
                accountData,
                List.of(MessageSentRecord.builder().countMessages(15).sendDate(LocalDateTime.MIN).build(),
                MessageSentRecord.builder().countMessages(4).sendDate(LocalDateTime.MIN).build()));

        Mockito.when(mailingHistoryDao.findByAccount_Id(id)).thenReturn(Optional.of(mailingHistory));

        var result = modifyDataBaseService.getMailingHistoryById(id);

        Assertions.assertEquals(mailingHistory.getMailingList(), result.get().getMailingList());
    }

    @Test
    public void getMailingHistoryByIdFirstNegative(){
        var id = 12L;
        var accountData = new Account();
        accountData.setMailingHistory(null);

        Mockito.when(accountDao.findById(id)).thenReturn(Optional.of(accountData));

        var result = modifyDataBaseService.getMailingHistoryById(id);

        Assertions.assertTrue(result.isEmpty());
    }

    @Test
    public void getMailingHistoryByIdSecondNegative(){
        var id = 12L;
        var accountData = new Account();
        var mailingHistory = new MailingHistory(id,StateMessage.QUEUE,accountData, null);
        accountData.setMailingHistory(mailingHistory);

        Mockito.when(accountDao.findById(id)).thenReturn(Optional.of(accountData));

        var result = modifyDataBaseService.getMailingHistoryById(id);

        Assertions.assertTrue(result.isEmpty());
    }

    @Test
    public void updateStateById(){
        var id = 12L;
        var state = State.NOTHING_PENDING;

        modifyDataBaseService.updateStateById(id,state);

        Mockito.verify(accountDao).updateStateById(id, state);
    }

    @Test
    public void updateKeyForMailById(){
        var id = 12L;
        var newKey = "129i3asdugc";

        modifyDataBaseService.updateKeyForMailById(id,newKey);

        Mockito.verify(accountDao).updateKeyForMailById(id,newKey);
    }

    @Test
    public void updateMailById(){
        var id = 12L;
        var mail = "mail@gmail.com";

        modifyDataBaseService.updateMailById(id, mail);

        Mockito.verify(accountDao).updateMailById(id, mail);
    }

    @Test
    public void saveAccount(){
        var id = 12L;
        var email = "email@mail.ru";
        var keyForMail = "123ojjh23e12bas";
        var account = Account.builder()
                .id(id)
                .email(email)
                .keyForMail(keyForMail)
                .state(State.NOTHING_PENDING)
                .build();

        modifyDataBaseService.saveAccount(account);

        Mockito.verify(accountDao).save(account);
    }

    @Test
    public void addMessageSentRecordPositive(){
        var id = 12L;
        var email = "email@mail.ru";
        var keyForMail = "123ojjh23e12bas";
        var accountData = Account.builder()
                .id(id)
                .email(email)
                .keyForMail(keyForMail)
                .state(State.NOTHING_PENDING)
                .build();
        var messageSentRecord = MessageSentRecord.builder().countMessages(12).sendDate(LocalDateTime.MIN).build();
        var mailingList = List.of(messageSentRecord);
        var mailingHistory = MailingHistory.builder()
                .mailingList(mailingList).account(accountData).build();
        Mockito.when(mailingHistoryDao.findByAccount_Id(id)).thenReturn(Optional.ofNullable(mailingHistory));

        modifyDataBaseService.addMessageSentRecord(id,messageSentRecord);

        mailingHistory.getMailingList().add(messageSentRecord);
        Mockito.verify(mailingHistoryDao).save(mailingHistory);
    }

    @Test
    public void addMessageSentRecordNegative(){
        var id = 12L;
        var email = "email@mail.ru";
        var keyForMail = "123ojjh23e12bas";
        var accountData = Account.builder()
                .id(id)
                .email(email)
                .keyForMail(keyForMail)
                .state(State.NOTHING_PENDING)
                .build();
        var messageSentRecord = MessageSentRecord.builder().countMessages(12).sendDate(LocalDateTime.MIN).build();
        Mockito.when(mailingHistoryDao.findByAccount_Id(id)).thenReturn(Optional.empty());
        Mockito.when(accountDao.findById(id)).thenReturn(Optional.ofNullable(accountData));

        modifyDataBaseService.addMessageSentRecord(id,messageSentRecord);

        accountData.setMailingHistory(MailingHistory.builder().mailingList(List.of(messageSentRecord)).build());
        Mockito.verify(accountDao).save(accountData);
    }

}

package org.gnori.send.mail.worker.service.mail.task.listener.impl;


import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.gnori.data.entity.MessageSentRecord;
import org.gnori.data.entity.enums.StateMessage;
import org.gnori.data.flow.Empty;
import org.gnori.data.flow.Result;
import org.gnori.data.model.Message;
import org.gnori.data.service.account.AccountService;
import org.gnori.data.service.mailing.MailingHistoryService;
import org.gnori.data.service.messagesentrecord.MessageSentRecordService;
import org.gnori.send.mail.worker.aop.LogExecutionTime;
import org.gnori.send.mail.worker.service.mail.sender.MailFailure;
import org.gnori.send.mail.worker.service.mail.sender.MailSender;
import org.gnori.send.mail.worker.service.mail.task.listener.MailTaskListener;
import org.gnori.send.mail.worker.service.storage.emaildata.DefaultEmailDataStorage;
import org.gnori.send.mail.worker.utils.LoginAuthenticator;
import org.gnori.send.mail.worker.utils.MailUtils;
import org.gnori.shared.crypto.CryptoTool;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import javax.mail.Session;
import java.util.Properties;

@Log4j2
@Service
@RequiredArgsConstructor
public class MailTaskListenerImpl implements MailTaskListener {

    private final CryptoTool cryptoTool;
    private final MailSender mailSender;
    private final AccountService accountService;
    private final MailingHistoryService mailingHistoryService;
    private final DefaultEmailDataStorage defaultEmailDataStorage;
    private final MessageSentRecordService messageSentRecordService;

    @RabbitListener(queues = "${spring.rabbitmq.queue-name}")
    public void receivedMailTask(Message message) {

        log.info("MailTaskListener: receivedMailTask {}", message);

        send(message)
                .doIfSuccess(empty -> addMessageSentRecord(message))
                .doIfSuccess(empty -> mailingHistoryService.updateStateMessageByAccountId(message.accountId(), StateMessage.SUCCESS))
                .doIfFailure(failure -> {
                    log.error("MailTaskListener: {}", failure.name());
                    mailingHistoryService.updateStateMessageByAccountId(message.accountId(), StateMessage.FAIL);
                });
    }

    private Result<Empty, MailFailure> send(Message message) {

        return switch (message.sendMode()) {
            case ANONYMOUSLY -> sendAnonymously(message);
            case CURRENT_MAIL -> sendWithUserMail(message);
        };
    }

    @LogExecutionTime
    private Result<Empty, MailFailure> sendAnonymously(Message message) {

        return defaultEmailDataStorage.find()
                .map(mail -> {

                    final Properties props = MailUtils.detectProperties(mail.login()).orElseThrow();
                    final Session session = Session.getDefaultInstance(props, new LoginAuthenticator(mail.login(), mail.password()));


                    return mailSender.send(mail.login(), session, message)
                            .doAnyway(() -> defaultEmailDataStorage.add(mail));
                })
                .orElseGet(() -> Result.failure(MailFailure.NO_FREE_MAILING_ADDRESSES));

    }

    @LogExecutionTime
    private Result<Empty, MailFailure> sendWithUserMail(Message message) {

        return accountService.findAccountById(message.accountId())
                .map(account ->
                        cryptoTool.decrypt(account.getKeyForMail())
                                .mapFailure(failure -> MailFailure.BAD_DECRYPT_KEY)
                                .flatMapSuccess(decryptedKey -> {

                                    final String username = account.getEmail();
                                    final Properties properties = MailUtils.detectProperties(username).orElse(null);
                                    final Session session = Session.getInstance(properties, new LoginAuthenticator(username, decryptedKey));

                                    return mailSender.send(username, session, message);
                                }))
                .orElseGet(() -> Result.failure(MailFailure.NOT_FOUND_EMAIL_ACCOUNT));
    }

    private void addMessageSentRecord(Message message) {

        final MessageSentRecord messageSentRecord = MessageSentRecord.builder()
                .countMessages(countWillSendMessages(message))
                .build();

        messageSentRecordService.addMessageSentRecordByAccountId(message.accountId(), messageSentRecord);
    }

    private int countWillSendMessages(Message message) {

        final long countRecipients = message.recipients().stream()
                .filter(MailUtils::validateMail).count();

        return (int) countRecipients * message.countForRecipient();
    }
}

package org.gnori.send.mail.worker.service.mail.task.listener.impl;


import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.gnori.data.model.Message;
import org.gnori.send.mail.worker.aop.LogExecutionTime;
import org.gnori.send.mail.worker.service.mail.sender.MailFailure;
import org.gnori.send.mail.worker.service.mail.sender.MailSender;
import org.gnori.send.mail.worker.service.mail.task.listener.MailTaskListener;
import org.gnori.send.mail.worker.utils.DefaultEmailData;
import org.gnori.send.mail.worker.utils.LoginAuthenticator;
import org.gnori.send.mail.worker.utils.MailUtils;
import org.gnori.shared.flow.Empty;
import org.gnori.shared.flow.Result;
import org.gnori.shared.crypto.CryptoTool;
import org.gnori.store.domain.service.ModifyDataBaseService;
import org.gnori.store.entity.MessageSentRecord;
import org.gnori.store.entity.enums.StateMessage;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import javax.mail.Session;
import java.util.Properties;

@Log4j2
@Service
@RequiredArgsConstructor
public class MailTaskListenerImpl implements MailTaskListener {

    private final MailSender mailSender;
    private final DefaultEmailData defaultEmailData;
    private final ModifyDataBaseService modifyDataBaseService;
    private final CryptoTool cryptoTool;

    @RabbitListener(queues = "${spring.rabbitmq.queue-name}")
    public void receivedMailTask(Message message) {

        log.info("MailTaskListener: receivedMailTask {}", message);

        send(message)
                .doIfSuccess(empty -> addMessageSentRecord(message))
                .doIfSuccess(empty -> modifyDataBaseService.updateStateMessageByAccountId(message.chatId(), StateMessage.SUCCESS))
                .doIfFailure(failure -> {
                    log.error("MailTaskListener: {}", failure.name());
                    modifyDataBaseService.updateStateMessageByAccountId(message.chatId(), StateMessage.FAIL);
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

        return defaultEmailData.find()
                .map(mail -> {

                    final Properties props = MailUtils.detectProperties(mail.login()).orElseThrow();
                    final Session session = Session.getDefaultInstance(props, new LoginAuthenticator(mail.login(), mail.password()));


                    return mailSender.send(mail.login(), session, message)
                            .doAnyway(() -> defaultEmailData.add(mail));
                })
                .orElseGet(() -> Result.failure(MailFailure.NO_FREE_MAILING_ADDRESSES));

    }

    @LogExecutionTime
    private Result<Empty, MailFailure> sendWithUserMail(Message message) {

        return modifyDataBaseService.findAccountById(message.chatId())
                .map(account -> {

                    final String username = account.getEmail();
                    final String keyForMail = cryptoTool.decrypt(account.getKeyForMail());
                    final Properties properties = MailUtils.detectProperties(username).orElse(null);
                    final Session session = Session.getInstance(properties, new LoginAuthenticator(username, keyForMail));

                    return mailSender.send(username, session, message);
                })
                .orElseGet(() -> Result.failure(MailFailure.NOT_FOUND_EMAIL_ACCOUNT));
    }

    private void addMessageSentRecord(Message message) {

        final MessageSentRecord messageSentRecord = MessageSentRecord.builder()
                .countMessages(countWillSendMessages(message))
                .build();

        modifyDataBaseService.addMessageSentRecord(message.chatId(), messageSentRecord);
    }

    private int countWillSendMessages(Message message) {
        return (int) message.recipients().stream().filter(MailUtils::validateMail).count() * message.countForRecipient();
    }
}

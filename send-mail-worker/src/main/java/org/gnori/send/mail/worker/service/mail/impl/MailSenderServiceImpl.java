package org.gnori.send.mail.worker.service.impl;


import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.gnori.data.model.Message;
import org.gnori.send.mail.worker.aop.LogExecutionTime;
import org.gnori.send.mail.worker.service.MailSenderService;
import org.gnori.send.mail.worker.service.enums.MailDomain;
import org.gnori.send.mail.worker.service.mail.sender.MailSender;
import org.gnori.send.mail.worker.utils.*;
import org.gnori.shared.utils.CryptoTool;
import org.gnori.store.dao.ModifyDataBaseService;
import org.gnori.store.entity.MessageSentRecord;
import org.gnori.store.entity.enums.StateMessage;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import javax.mail.AuthenticationFailedException;
import javax.mail.Session;
import javax.mail.internet.AddressException;
import java.util.Optional;
import java.util.Properties;

import static org.gnori.send.mail.worker.utils.UtilsMail.*;

@Log4j2
@Service
@RequiredArgsConstructor
public class MailSenderServiceImpl implements MailSenderService {

    private final MailSender mailSender;
    private final DefaultEmailData defaultEmailData;
    private final ModifyDataBaseService modifyDataBaseService;
    private final CryptoTool cryptoTool;

    @RabbitListener(queues = "${spring.rabbitmq.queue-name}")
    public void receivedMessage(Message message) {
        log.info("MailSenderService: receivedMessage {}", message);
        var sendMode = message.getSendMode();
        try {
            switch (sendMode) {
                case ANONYMOUSLY -> sendAnonymously(message);
                case CURRENT_MAIL -> sendWithUserMail(message);
            }
            createAndAddMessageSentRecord(message);
            modifyDataBaseService.updateStateMessageById(message.getChatId(), StateMessage.SUCCESS);

        } catch (AddressException | AuthenticationFailedException |
                 NoFreeMailingAddressesException e) {
            log.error("MailSenderService:", e);
            modifyDataBaseService.updateStateMessageById(message.getChatId(), StateMessage.FAIL);
        }
    }

    @LogExecutionTime
    private void sendAnonymously(Message message) throws AddressException, AuthenticationFailedException, NoFreeMailingAddressesException {

        var props = getBaseProperties();

        final Optional<EmailData> mailOptional = defaultEmailData.find();

        if (mailOptional.isPresent()) {

            final EmailData mail = mailOptional.get();
            final Session session = Session.getDefaultInstance(props, new LoginAuthenticator(mail.login(), mail.password()));

            try {
                mailSender.send(mail.login(), session, message);
            } catch (Exception e) {
                log.error(e);
            } finally {
                defaultEmailData.add(mail);
            }
        } else {
            throw new NoFreeMailingAddressesException("While all mailboxes are closed, you can later");
        }
    }

    @LogExecutionTime
    private void sendWithUserMail(Message message) throws AuthenticationFailedException, AddressException {

        var optionalAccount = modifyDataBaseService.findAccountById(message.getChatId());
        if (optionalAccount.isEmpty()) {
            return;
        }

        var account = optionalAccount.get();
        var username = account.getEmail();
        var keyForMail = cryptoTool.decrypt(account.getKeyForMail());

        var props = getPropertiesBasedOnDomain(username);

        var session = Session.getInstance(props, new LoginAuthenticator(username, keyForMail));
        try {
            mailSender.send(username, session, message);
        } catch (Exception e) {
            log.error(e);
        }
    }

    private Properties getPropertiesBasedOnDomain(String mail) {

        if (mail != null) {
            var indexSeparator = mail.indexOf("@");
            var domainPart = mail.substring(indexSeparator);
            for (var domain : MailDomain.GMAIL.getDomainList()) {
                if (domainPart.contains(domain)) {
                    return getGmailProperties();
                }
            }
            for (var domain : MailDomain.YANDEX.getDomainList()) {
                if (domainPart.contains(domain)) {
                    return getYandexProperties();
                }
            }
            for (var domain : MailDomain.MAIL.getDomainList()) {
                if (domainPart.contains(domain)) {
                    return getMailProperties();
                }
            }
        }
        return null;
    }

    private void createAndAddMessageSentRecord(Message message) {

        var countMessages = (int) message.getRecipients().stream().filter(UtilsMail::validateMail).count() * message.getCountForRecipient();
        var messageSentRecord = MessageSentRecord.builder().countMessages(countMessages).build();

        modifyDataBaseService.addMessageSentRecord(message.getChatId(), messageSentRecord);
    }
}

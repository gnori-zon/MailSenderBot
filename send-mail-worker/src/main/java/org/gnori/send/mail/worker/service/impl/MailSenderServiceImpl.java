package org.gnori.send.mail.worker.service.impl;


import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.gnori.data.model.Message;
import org.gnori.send.mail.worker.aop.LogExecutionTime;
import org.gnori.send.mail.worker.service.MailSenderService;
import org.gnori.send.mail.worker.service.enums.MailDomain;
import org.gnori.send.mail.worker.utils.*;
import org.gnori.shared.service.loader.file.FileData;
import org.gnori.shared.service.loader.file.FileLoader;
import org.gnori.shared.service.loader.file.FileType;
import org.gnori.shared.utils.CryptoTool;
import org.gnori.store.dao.ModifyDataBaseService;
import org.gnori.store.entity.MessageSentRecord;
import org.gnori.store.entity.enums.StateMessage;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Document;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;

import javax.mail.AuthenticationFailedException;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Optional;
import java.util.Properties;

import static org.gnori.send.mail.worker.utils.UtilsMail.*;

/**
 * Implementation service {@link MailSenderService}
 */
@Log4j2
@Service
@RequiredArgsConstructor
public class MailSenderServiceImpl implements MailSenderService {

    private static final int MAX_RECIPIENTS = 499;

    private final BasicEmails basicEmails;
    private final ModifyDataBaseService modifyDataBaseService;
    private final FileLoader fileLoader;
    private final CryptoTool cryptoTool;

    @RabbitListener( queues = "${spring.rabbitmq.queue-name}")
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
        var optionalMail = basicEmails.getBasicEmailAddresses().stream().filter(el -> el.getState().equals(StateEmail.FREE)).findFirst();
        if (optionalMail.isPresent()) {
            var mail = optionalMail.get();
            mail.setState(StateEmail.USED);
            var session = Session.getDefaultInstance(props, new LoginAuthenticator(mail.getLogin(), mail.getPassword()));
            try {
                sendMessage(mail.getLogin(), session, message);
            } catch (AddressException e) {
                throw e;
            } catch (Exception e) {
                log.error(e);
                if (e instanceof AuthenticationFailedException) {
                    throw new AuthenticationFailedException("Invalid authentication data");
                }
            }finally {
                mail.setState(StateEmail.FREE);
            }
        }else{
            throw new NoFreeMailingAddressesException("While all mailboxes are closed, you can later");
        }
    }

    @LogExecutionTime
    private void sendWithUserMail(Message message) throws AuthenticationFailedException, AddressException {
        var optionalAccount = modifyDataBaseService.findAccountById(message.getChatId());
        if(optionalAccount.isEmpty()){return;}

        var account = optionalAccount.get();
        var username = account.getEmail();
        var keyForMail = cryptoTool.decrypt(account.getKeyForMail());

        var props = getPropertiesBasedOnDomain(username);

        var session = Session.getInstance(props, new LoginAuthenticator(username,keyForMail));
        try {
            sendMessage(username, session, message);
        } catch (AddressException e) {
            throw e;
        }catch (AuthenticationFailedException e) {
            throw new AuthenticationFailedException("Invalid authentication data");
        }catch (Exception e){
            log.error(e);
        }
    }

    private Properties getPropertiesBasedOnDomain(String mail) {
        if(mail!=null){
            var indexSeparator = mail.indexOf("@");
            var domainPart = mail.substring(indexSeparator);
            for(var domain : MailDomain.GMAIL.getDomainList()) {
                if (domainPart.contains(domain)) {
                    return getGmailProperties();
                }
            }
            for(var domain : MailDomain.YANDEX.getDomainList()) {
                if (domainPart.contains(domain)) {
                    return getYandexProperties();
                }
            }
            for(var domain : MailDomain.MAIL.getDomainList()) {
                if (domainPart.contains(domain)) {
                    return getMailProperties();
                }
            }
        }
        return null;
    }

    private InternetAddress[] prepareRecipients(Message message) throws AddressException{

        message.setRecipients(
            message.getRecipients()
                .stream()
                .limit(MAX_RECIPIENTS)
                .toList()
        );

        var recipientsStr = message.getRecipients()
            .stream()
            .filter(UtilsMail::validateMail)
            .toList()
            .toString();

        if (recipientsStr.length()<3){
            throw new AddressException("No valid recipient addresses");
        }
        var addresses = InternetAddress.parse(
            recipientsStr.substring(1, recipientsStr.length()-1)
        );

        return addresses;
    }

    private void sendMessage(String mail, Session session, Message message) throws MessagingException {
        InternetAddress[] recipients;
        recipients = prepareRecipients(message);
        var mailMessage = new MimeMessage(session);
        var helper = new MimeMessageHelper(mailMessage,true);
        helper.setFrom(mail);
        helper.setTo(recipients);
        helper.setSubject(message.getTitle());
        helper.setText(message.getText());

        if(message.hasSentDate()){
            helper.setSentDate(message.getSentDate());
        }
        FileSystemResource file = null;
        int processFileStatus = 0;
        if(message.hasAnnex()) {

            final FileData fileData = message.getDocAnnex() != null
                    ? fileDataOf(message.getDocAnnex())
                    : fileDataOf(message.getPhotoAnnex());

            fileLoader.loadFile(fileData)
                    .doIfSuccess(fileSystemResource -> attach(helper, fileSystemResource));
        }

        var countMessage = message.getCountForRecipient();
        while (countMessage > 0) {
            Transport.send(mailMessage);
            countMessage--;
        }
        if(processFileStatus==1) {
            var isDeleted = file.getFile().delete();
            if(!isDeleted){
                log.error("File:"+file.getFilename()+" not removed");
            }
        }
    }

    private void attach(MimeMessageHelper helper, FileSystemResource fileSystemResource) {

        try {

            final String filename = Optional.ofNullable(fileSystemResource.getFilename())
                    .orElse("file");

            helper.addAttachment(filename, fileSystemResource);
        } catch (MessagingException e) {
            log.error("bad attaching file: {}", e.getLocalizedMessage());
        }
    }

    private FileData fileDataOf(Document documentAnnex) {

        final String fileId = documentAnnex.getFileId();
        final String fileName = documentAnnex.getFileName();

        return new FileData(fileId, fileName, FileType.DOCUMENT);
    }

    private FileData fileDataOf(PhotoSize photoAnnex) {

        final String fileId = photoAnnex.getFileId();
        final String fileName = "IMG";

        return new FileData(fileId, fileName, FileType.PHOTO);
    }

    private void createAndAddMessageSentRecord(Message message){
        var countMessages = (int) message.getRecipients().stream().filter(UtilsMail::validateMail).count() * message.getCountForRecipient();
        var messageSentRecord = MessageSentRecord.builder().countMessages(countMessages).build();
        modifyDataBaseService.addMessageSentRecord(message.getChatId(), messageSentRecord);
    }

}

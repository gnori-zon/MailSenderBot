package org.gnori.mailsenderbot.service.impl;

import lombok.extern.log4j.Log4j;
import org.gnori.mailsenderbot.aop.LogExecutionTime;
import org.gnori.mailsenderbot.entity.MessageSentRecord;
import org.gnori.mailsenderbot.entity.enums.StateMessage;
import org.gnori.mailsenderbot.model.Message;
import org.gnori.mailsenderbot.service.FileService;
import org.gnori.mailsenderbot.service.MailSenderService;
import org.gnori.mailsenderbot.service.ModifyDataBaseService;
import org.gnori.mailsenderbot.service.QueueManager;
import org.gnori.mailsenderbot.service.impl.enums.MailDomain;
import org.gnori.mailsenderbot.utils.CryptoTool;
import org.gnori.mailsenderbot.utils.LoginAuthenticator;
import org.gnori.mailsenderbot.utils.UtilsCommand;
import org.gnori.mailsenderbot.utils.forMail.BasicEmails;
import org.gnori.mailsenderbot.utils.forMail.NoFreeMailingAddressesException;
import org.gnori.mailsenderbot.utils.forMail.StateEmail;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.MimeMessageHelper;

import javax.mail.AuthenticationFailedException;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;
import java.util.stream.Collectors;

import static org.gnori.mailsenderbot.utils.forMail.UtilsMail.*;
/**
 * Implementation service {@link MailSenderService}
 */
@Log4j
public class MailSenderServiceImpl implements MailSenderService, Runnable {
    private final int WAIT_FOR_NEW_MESSAGE_DELAY = 1000;

    private final BasicEmails basicEmails;
    private final ModifyDataBaseService modifyDataBaseService;
    private final FileService fileService;
    private final CryptoTool cryptoTool;
    private final QueueManager queueManager;

    public MailSenderServiceImpl(BasicEmails basicEmails, ModifyDataBaseService modifyDataBaseService, FileService fileService,
                                 CryptoTool cryptoTool,QueueManager queueManager) {
        this.basicEmails = basicEmails;
        this.modifyDataBaseService = modifyDataBaseService;
        this.fileService = fileService;
        this.cryptoTool = cryptoTool;
        this.queueManager = queueManager;

    }
    @Override
    public void run() {
        log.info("[STARTED] MailSenderService: " + this);
        while (true) {
            for (Message message = queueManager.pollFromQueue(); message != null; message = queueManager.pollFromQueue()) {
                var sendMode = message.getSendMode();
                try {
                    switch (sendMode) {
                        case ANONYMOUSLY: {
                            sendAnonymously(message);
                            break;
                        }
                        case CURRENT_MAIL: {
                            sendWithUserMail(message);
                            break;
                        }
                    }
                    createAndAddMessageSentRecord(message);
                    modifyDataBaseService.updateStateMessageById(message.getChatId(), StateMessage.SUCCESS);
                    }catch (AddressException | AuthenticationFailedException | NoFreeMailingAddressesException e) {
                        log.error("MailSenderService:", e);
                        modifyDataBaseService.updateStateMessageById(message.getChatId(), StateMessage.FAIL);
                    return;
                    }
                }
            try {
                Thread.sleep(WAIT_FOR_NEW_MESSAGE_DELAY);
            } catch (InterruptedException e) {
                log.error("Catch interrupt. Exit", e);
                return;
            }
        }

    }
    @LogExecutionTime
    private void sendAnonymously(Message message) throws AddressException, NoFreeMailingAddressesException {
        var props = getBaseProperties();
        var optionalMail = basicEmails.getBasicEmailAddresses().stream().filter(el -> el.getState().equals(StateEmail.FREE)).findFirst();
        if (optionalMail.isPresent()) {
            var mail = optionalMail.get();
            mail.setState(StateEmail.USED);
            var session = Session.getDefaultInstance(props, new LoginAuthenticator(mail.getLogin(), mail.getPassword()));
            try {
                sendMessage(message.getChatId(), mail.getLogin(), session, message);
            } catch (AddressException e) {
                throw e;
            } catch (Exception e) {
                log.error(e);
            }finally {
                mail.setState(StateEmail.FREE);
            }
        }else{
            throw new NoFreeMailingAddressesException("Пока все почтовые ящики заняты, попробуйте позже");
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
            sendMessage(message.getChatId(), username, session, message);
        } catch (AddressException e) {
            throw e;
        }catch (AuthenticationFailedException e) {
            throw new AuthenticationFailedException("Неверный ключ доступа");
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
        var recipientsStr = message.getRecipients().stream().filter(UtilsCommand::validateMail).collect(Collectors.toList()).toString();
        if (recipientsStr.length()<3){
            throw new AddressException("Отсутсвуют валидные адреса получаетелей");
        }
        return InternetAddress.parse(recipientsStr.substring(1, recipientsStr.length()-1));
    }
    private void sendMessage(Long id, String mail, Session session, Message message) throws MessagingException {
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
            if(message.getDocAnnex()!=null){
                processFileStatus = fileService.processDoc(id);
            }else {
                processFileStatus = fileService.processPhoto(id);
            }
            if(processFileStatus==1) {
                file = fileService.getFileSystemResource(id);
                helper.addAttachment(file.getFilename(), file);
            }
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
    private void createAndAddMessageSentRecord(Message message){
        var countMessages = (int) message.getRecipients().stream().filter(UtilsCommand::validateMail).count() * message.getCountForRecipient();
        var messageSentRecord = MessageSentRecord.builder().countMessages(countMessages).build();
        modifyDataBaseService.addMessageSentRecord(message.getChatId(), messageSentRecord);
    }
}

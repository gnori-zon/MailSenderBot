package org.gnori.mailsenderbot.service.impl;

import lombok.extern.log4j.Log4j;
import org.gnori.mailsenderbot.aop.LogExecutionTime;
import org.gnori.mailsenderbot.dao.AccountDao;
import org.gnori.mailsenderbot.model.Message;
import org.gnori.mailsenderbot.service.FileService;
import org.gnori.mailsenderbot.service.MailSenderService;
import org.gnori.mailsenderbot.service.impl.enums.MailDomain;
import org.gnori.mailsenderbot.utils.CryptoTool;
import org.gnori.mailsenderbot.utils.LoginAuthenticator;
import org.gnori.mailsenderbot.utils.UtilsCommand;
import org.gnori.mailsenderbot.utils.forMail.BasicEmails;
import org.gnori.mailsenderbot.utils.forMail.NoFreeMailingAddressesException;
import org.gnori.mailsenderbot.utils.forMail.StateEmail;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

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
@Service
public class MailSenderServiceImpl implements MailSenderService {
    private final BasicEmails basicEmails;
    private final AccountDao accountDao;
    private final FileService fileService;
    private final CryptoTool cryptoTool;

    public MailSenderServiceImpl(BasicEmails basicEmails, AccountDao accountDao, FileService fileService,
                                 CryptoTool cryptoTool) {
        this.basicEmails = basicEmails;

        this.accountDao = accountDao;
        this.fileService = fileService;
        this.cryptoTool = cryptoTool;

    }

    @LogExecutionTime
    @Override
    public int sendAnonymously(Long id, Message message) throws AddressException, NoFreeMailingAddressesException {
        var props = getBaseProperties();
        var optionalMail = basicEmails.getBasicEmailAddresses().stream().filter(el -> el.getState().equals(StateEmail.FREE)).findFirst();
        if (optionalMail.isPresent()) {
            var mail = optionalMail.get();
            mail.setState(StateEmail.USED);
            var session = Session.getDefaultInstance(props, new LoginAuthenticator(mail.getLogin(), mail.getPassword()));
            try {
                sendMessage(id, mail.getLogin(), session, message);
                return 1;
            } catch (AddressException e) {
                throw e;
            } catch (Exception e) {
                log.error(e);
                return 0;
            }finally {
                mail.setState(StateEmail.FREE);
            }
        }else{
            throw new NoFreeMailingAddressesException("Пока все почтовые ящики заняты, попробуйте позже");
        }
    }

    @LogExecutionTime
    @Override
    public int sendWithUserMail(Long id, Message message) throws AuthenticationFailedException, AddressException {
        var optionalAccount = accountDao.findById(id);
        if(optionalAccount.isEmpty()){return 0;}

        var account = optionalAccount.get();
        var username = account.getEmail();
        var keyForMail = cryptoTool.decrypt(account.getKeyForMail());

        var props = getPropertiesBasedOnDomain(username);

        var session = Session.getInstance(props, new LoginAuthenticator(username,keyForMail));
        try {
            sendMessage(id, username, session, message);
            return 1;
        } catch (AddressException e) {
            throw e;
        }catch (AuthenticationFailedException e) {
            throw new AuthenticationFailedException("Неверный ключ доступа");
        }catch (Exception e){
            log.error(e);
            return 0;
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
        int processStatus = 0;
        if(message.hasAnnex()) {
            if(message.getDocAnnex()!=null){
                processStatus = fileService.processDoc(id);
            }else {
                processStatus = fileService.processPhoto(id);
            }
            if(processStatus==1) {
                file = fileService.getFileSystemResource(id);
                helper.addAttachment(file.getFilename(), file);
            }
        }
        var countMessage = message.getCountForRecipient();
        while (countMessage > 0) {
            Transport.send(mailMessage);
            countMessage--;
        }
        if(processStatus==1) {
            var isDeleted = file.getFile().delete();
            if(!isDeleted){
                log.error("File:"+file.getFilename()+" not removed");
            }
        }
    }

}

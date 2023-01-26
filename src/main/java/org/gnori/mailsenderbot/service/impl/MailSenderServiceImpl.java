package org.gnori.mailsenderbot.service.impl;

import lombok.extern.log4j.Log4j;
import org.gnori.mailsenderbot.dao.AccountDao;
import org.gnori.mailsenderbot.model.Message;
import org.gnori.mailsenderbot.service.FileService;
import org.gnori.mailsenderbot.service.MailSenderService;
import org.gnori.mailsenderbot.service.impl.enums.MailDomain;
import org.gnori.mailsenderbot.utils.CryptoTool;
import org.gnori.mailsenderbot.utils.LoginAuthenticator;
import org.springframework.beans.factory.annotation.Value;
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

import static org.gnori.mailsenderbot.utils.UtilsMail.*;

@Log4j
@Service
public class MailSenderServiceImpl implements MailSenderService {
    @Value("${base.mail}")
    private String BASE_MAIL;
    @Value("${base.key}")
    private String BASE_KEY;
    private final AccountDao accountDao;
    private final FileService fileService;
    private final CryptoTool cryptoTool;

    public MailSenderServiceImpl(AccountDao accountDao, FileService fileService, CryptoTool cryptoTool) {

        this.accountDao = accountDao;
        this.fileService = fileService;
        this.cryptoTool = cryptoTool;

    }

    @Override
    public int sendAnonymously(Long id, Message message) {
        var props = getBaseProperties();
        var session = Session.getDefaultInstance(props, new LoginAuthenticator(BASE_MAIL, BASE_KEY));
        try {
            sendMessage(id, BASE_MAIL,session,message);
            return 1;
        } catch (Exception e) {
            log.error(e);
            return 0;
        }
    }

    @Override
    public int sendWithUserMail(Long id, Message message) throws AuthenticationFailedException {
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
        }catch (AuthenticationFailedException e) {
            throw new AuthenticationFailedException();
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
        var recipientsStr = message.getRecipients().toString();
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
        if(message.hasAnnex()) {
            int processStatus;
            if(message.getDocAnnex()!=null){
                processStatus = fileService.processDoc(id);
            }else {
                processStatus = fileService.processPhoto(id);
            }
            if(processStatus==1) {
                var file = fileService.getFileSystemResource(id);
                helper.addAttachment(file.getFilename(), file);
            }
        }
        var countMessage = message.getCountForRecipient();
        while (countMessage > 0) {
            Transport.send(mailMessage);
            countMessage--;
        }
    }

}

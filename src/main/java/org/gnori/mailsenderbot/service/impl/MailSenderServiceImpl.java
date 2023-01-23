package org.gnori.mailsenderbot.service.impl;

import lombok.extern.log4j.Log4j;
import org.gnori.mailsenderbot.dao.AccountDao;
import org.gnori.mailsenderbot.model.Message;
import org.gnori.mailsenderbot.service.MailSenderService;
import org.gnori.mailsenderbot.service.utils.LoginAuthenticator;
import org.springframework.stereotype.Service;

import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

@Log4j
@Service
public class MailSenderServiceImpl implements MailSenderService {
    private final AccountDao accountDao;

    public MailSenderServiceImpl(AccountDao accountDao) {
        this.accountDao = accountDao;
    }

    @Override
    public int sendAnonymously(Long id, Message message) {
        //TODO impl this
        return 0;
    }

    @Override
    public int sendWithUserMail(Long id, Message message){
        var optionalAccount = accountDao.findById(id);
        if(optionalAccount.isEmpty()){return 0;}

        var account = optionalAccount.get();
        //TODO move the definition of the port and host to a separate Utils class
        var username = account.getEmail();
        var keyForMail = account.getKeyForMail();
        var recipientsStr = message.getRecipients().toString();
        InternetAddress[] recipients = new InternetAddress[0];
        try {
            recipients = InternetAddress.parse(recipientsStr.substring(1, recipientsStr.length()-1));
        } catch (AddressException e) {
            log.error(e);
            return 0;
        }

        var props = new Properties();
        props.put("mail.smtp.host","smtp.gmail.com");
        props.put("mail.smtp.port","587");
        props.put("mail.protocol","smtp");
        props.put("mail.smtp.auth","true");
        props.put("mail.smtp.starttls.enable","true");
        props.put("mail.debug","true");

        var session = Session.getDefaultInstance(props, new LoginAuthenticator(username,keyForMail));
        try{
            javax.mail.Message mailMessage = new MimeMessage(session);
            mailMessage.addFrom(InternetAddress.parse(username));
            mailMessage.addRecipients(javax.mail.Message.RecipientType.TO,recipients);
            mailMessage.setSubject(message.getTitle());
            mailMessage.setText(message.getText());
            var countMessage = message.getCountForRecipient();
            while (countMessage > 0) {
                Transport.send(mailMessage);
                countMessage--;
            }
                return 1;
        }catch (Exception e){
            return 0;
        }
    }
}

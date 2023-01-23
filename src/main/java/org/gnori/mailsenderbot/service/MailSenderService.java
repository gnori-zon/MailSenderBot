package org.gnori.mailsenderbot.service;

import org.gnori.mailsenderbot.model.Message;

import javax.mail.internet.AddressException;

public interface MailSenderService {
    int sendAnonymously(Long id, Message message);
    int sendWithUserMail(Long id, Message message);
}

package org.gnori.mailsenderbot.service;

import org.gnori.mailsenderbot.model.Message;
import org.gnori.mailsenderbot.utils.forMail.NoFreeMailingAddressesException;

import javax.mail.AuthenticationFailedException;
import javax.mail.internet.AddressException;
/**
 * Service for sending mailing
 */
public interface MailSenderService {
    int sendAnonymously(Long id, Message message) throws AddressException, NoFreeMailingAddressesException;
    int sendWithUserMail(Long id, Message message) throws AuthenticationFailedException, AddressException;
}

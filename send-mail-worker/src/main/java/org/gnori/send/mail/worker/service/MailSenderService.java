package org.gnori.send.mail.worker.service;

import org.gnori.data.model.Message;

/**
 * Service for sending mailing
 */
public interface MailSenderService {
    void receivedMessage(Message message);
}
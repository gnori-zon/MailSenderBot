package org.gnori.send.mail.worker.service.mail.sender;

import org.gnori.data.model.Message;

import javax.mail.Session;

public interface MailSender {

    void send(String senderMail, Session session, Message message);
}

package org.gnori.send.mail.worker.service.mail.sender;

import org.gnori.data.model.Message;
import org.gnori.data.flow.Empty;
import org.gnori.data.flow.Result;

import javax.mail.Session;

public interface MailSender {

    Result<Empty, MailFailure> send(String senderMail, Session session, Message message);
}

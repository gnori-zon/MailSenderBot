package org.gnori.send.mail.worker.service.mail.filler;

import org.gnori.send.mail.worker.service.mail.sender.MailFailure;
import org.gnori.data.flow.Empty;
import org.gnori.data.flow.Result;

import javax.mail.internet.MimeMessage;

public interface MailMessageFiller {
    Result<MimeMessage, MailFailure> fill(MimeMessage mailMessage, MailMessageData data);
}

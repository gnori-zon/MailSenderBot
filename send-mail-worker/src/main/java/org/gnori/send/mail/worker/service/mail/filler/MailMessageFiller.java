package org.gnori.send.mail.worker.service.mail.filler;

import org.gnori.send.mail.worker.service.mail.sender.MailFailure;
import org.gnori.shared.flow.Empty;
import org.gnori.shared.flow.Result;

import javax.mail.internet.MimeMessage;

public interface MailMessageFiller {
    Result<Empty, MailFailure> fill(MimeMessage mailMessage, MailMessageData data);
}

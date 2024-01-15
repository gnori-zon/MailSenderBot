package org.gnori.send.mail.worker.service.mail.filler;

import javax.mail.internet.MimeMessage;

public interface MailMessageFiller {
    void fill(MimeMessage mailMessage, MailMessageData data);
}

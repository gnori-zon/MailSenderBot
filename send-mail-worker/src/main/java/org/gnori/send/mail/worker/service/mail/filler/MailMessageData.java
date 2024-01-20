package org.gnori.send.mail.worker.service.mail.filler;

import org.springframework.core.io.FileSystemResource;

import javax.mail.internet.InternetAddress;
import java.util.Date;

public record MailMessageData(
        String title,
        String senderMail,
        InternetAddress[] recipients,
        String text,
        FileSystemResource annex,
        Date sentDate
) {}

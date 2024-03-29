package org.gnori.send.mail.worker.service.mail.filler.impl;

import lombok.extern.log4j.Log4j2;
import org.gnori.data.flow.Result;
import org.gnori.send.mail.worker.service.mail.filler.MailMessageData;
import org.gnori.send.mail.worker.service.mail.filler.MailMessageFiller;
import org.gnori.send.mail.worker.service.mail.sender.MailFailure;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.Optional;

@Log4j2
@Component
public class MailMessageFillerImpl implements MailMessageFiller {

    private static final String DEFAULT_ANNEX_NAME = "annex";

    @Override
    public Result<MimeMessage, MailFailure> fill(MimeMessage mailMessage, MailMessageData data) {

        try {

            final MimeMessageHelper helper = new MimeMessageHelper(mailMessage, true);

            helper.setFrom(data.senderMail());
            helper.setTo(data.recipients());
            helper.setSubject(data.title());
            helper.setText(data.text());

            if (data.sentDate() != null) {
                helper.setSentDate(asDate(data.sentDate()));
            }

            if (data.annex() != null) {

                final String filename = extractAnnexFilename(data.annex());
                helper.addAttachment(filename, data.annex());
            }

            return Result.success(mailMessage);

        } catch (Exception e) {
            log.error("bad fill mail message: {}", e.getLocalizedMessage());

            if (e instanceof MessagingException) {
                return Result.failure(MailFailure.MESSAGING_EXCEPTION);
            }

            return Result.failure(MailFailure.BAD_FILL_MESSAGE);
        }
    }

    private String extractAnnexFilename(FileSystemResource annex) {

        return Optional.ofNullable(annex.getFilename())
                .orElse(DEFAULT_ANNEX_NAME);
    }

    private Date asDate(LocalDate localDate) {

        final Instant localDateInstant = localDate.atStartOfDay()
                .atZone(ZoneId.systemDefault())
                .toInstant();

        return Date.from(localDateInstant);
    }
}

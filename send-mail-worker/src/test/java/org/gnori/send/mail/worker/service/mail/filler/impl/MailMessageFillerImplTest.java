package org.gnori.send.mail.worker.service.mail.filler.impl;

import org.gnori.data.flow.Result;
import org.gnori.send.mail.worker.service.mail.filler.MailMessageData;
import org.gnori.send.mail.worker.service.mail.sender.MailFailure;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.FileSystemResource;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;

@DisplayName("Unit-level testing for MailMessageFillerImpl")
class MailMessageFillerImplTest {

    final MailMessageFillerImpl filler = new MailMessageFillerImpl();

    @Test
    void successEmptyFill() {

        final MimeMessage mimeMessage = new MimeMessage(getDefaultSession());
        final MailMessageData mailMessageData = new MailMessageData(
                "",
                "sender@mail.com",
                new InternetAddress[0],
                "",
                null,
                null

        );

        final AtomicBoolean isSuccessResult = new AtomicBoolean();
        final Result<MimeMessage, MailFailure> result = filler.fill(mimeMessage, mailMessageData);

        result.doIfSuccess(success -> {
            try {

                Assertions.assertEquals(mailMessageData.title(), success.getSubject());
                Assertions.assertEquals(mailMessageData.senderMail(), success.getFrom()[0].toString());
                Assertions.assertEquals("text/plain", success.getContentType());
                Assertions.assertNull(success.getRecipients(Message.RecipientType.TO));
                Assertions.assertEquals(mailMessageData.text(), extractOnlyText(success));
                Assertions.assertNull(success.getSentDate());

                isSuccessResult.set(true);
            } catch (MessagingException ignored) {}
        });

        Assertions.assertTrue(isSuccessResult.get());
    }

    private String extractOnlyText(MimeMessage success) {
        try {

            final MimeMultipart mainPart = (MimeMultipart) success.getContent();
            final MimeMultipart textContainerPart = ((MimeMultipart) mainPart.getBodyPart(0).getContent());

            return (String) textContainerPart.getBodyPart(0).getContent();
        } catch (MessagingException | IOException e){
            throw new RuntimeException("bad extract text in test: %s".formatted(e.getMessage()));
        }
    }


    private Session getDefaultSession() {
        return Session.getDefaultInstance(new Properties());
    }
}
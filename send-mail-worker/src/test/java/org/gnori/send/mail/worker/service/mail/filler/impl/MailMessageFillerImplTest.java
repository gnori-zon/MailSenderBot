package org.gnori.send.mail.worker.service.mail.filler.impl;

import org.gnori.data.flow.Result;
import org.gnori.send.mail.worker.service.mail.filler.MailMessageData;
import org.gnori.send.mail.worker.service.mail.sender.MailFailure;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.FileSystemResource;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

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
            } catch (MessagingException ignored) {
            }
        });

        Assertions.assertTrue(isSuccessResult.get());
    }

    private String extractOnlyText(MimeMessage message) {
        try {

            final MimeMultipart mainPart = (MimeMultipart) message.getContent();
            final MimeMultipart textContainerPart = ((MimeMultipart) mainPart.getBodyPart(0).getContent());

            return (String) textContainerPart.getBodyPart(0).getContent();
        } catch (MessagingException | IOException e) {
            throw new RuntimeException("bad extract text in test: %s".formatted(e.getMessage()));
        }
    }

    @Test
    void successFoolFill() throws AddressException {

        final MimeMessage mimeMessage = new MimeMessage(getDefaultSession());
        final MailMessageData mailMessageData = new MailMessageData(
                "expected-title",
                "sender@mail.com",
                InternetAddress.parse("recipient@mail.com"),
                "expected-text",
                getMockFileSystemResource(),
                LocalDate.now().plusDays(1)

        );

        final AtomicBoolean isSuccessResult = new AtomicBoolean();
        final Result<MimeMessage, MailFailure> result = filler.fill(mimeMessage, mailMessageData);

        result.doIfSuccess(success -> {
            try {

                Assertions.assertEquals(mailMessageData.title(), success.getSubject());
                Assertions.assertEquals(mailMessageData.senderMail(), success.getFrom()[0].toString());
                Assertions.assertEquals("text/plain", success.getContentType());

                final Address[] actualRecipients = success.getRecipients(Message.RecipientType.TO);
                final InternetAddress[] expectedRecipients = mailMessageData.recipients();

                Assertions.assertEquals(expectedRecipients.length, actualRecipients.length);
                IntStream.range(0, actualRecipients.length)
                        .forEach(index -> Assertions.assertEquals(expectedRecipients[index].getAddress(), actualRecipients[index].toString()));

                Assertions.assertEquals(mailMessageData.text(), extractOnlyText(success));
                Assertions.assertEquals(mailMessageData.annex() != null, existAttachment(success));
                Assertions.assertEquals(mailMessageData.sentDate(), success.getSentDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());

                isSuccessResult.set(true);
            } catch (MessagingException ignored) {
            }
        });

        Assertions.assertTrue(isSuccessResult.get());
    }

    @Test
    void failureFill() {

        final List<MailMessageData> invalidMailMessageDataList = List.of(
                new MailMessageData(null, "seneder@mail.com", new InternetAddress[0], "text", null, null),
                new MailMessageData("title", null, new InternetAddress[0], "text", null, null),
                new MailMessageData("title", "seneder@mail.com", null, "text", null, null),
                new MailMessageData("title", "seneder@mail.com", new InternetAddress[0], null, null, null)
        );

        final AtomicInteger countInvalidResult = new AtomicInteger();
        invalidMailMessageDataList.forEach(mailMessageData -> {

            final MimeMessage mimeMessage = new MimeMessage(getDefaultSession());
            filler.fill(mimeMessage, mailMessageData)
                    .doIfFailure(failure -> countInvalidResult.incrementAndGet());
        });

        Assertions.assertEquals(invalidMailMessageDataList.size(), countInvalidResult.get());
    }

    private boolean existAttachment(MimeMessage message) {
        try {

            final MimeMultipart mainPart = (MimeMultipart) message.getContent();
            final MimeBodyPart fileBodyPart = (MimeBodyPart) mainPart.getBodyPart(1);

            return fileBodyPart != null;
        } catch (MessagingException | IOException e) {
            throw new RuntimeException("bad check on exist attachment: %s".formatted(e.getMessage()));
        }
    }

    private FileSystemResource getMockFileSystemResource() {

        try {
            final Path tempFilePath = Files.createTempFile("for_test", ".txt");
            tempFilePath.toFile().deleteOnExit();

            return new FileSystemResource(tempFilePath);
        } catch (IOException e) {
            throw new RuntimeException("bad create temp file for test");
        }
    }

    private Session getDefaultSession() {
        return Session.getDefaultInstance(new Properties());
    }
}
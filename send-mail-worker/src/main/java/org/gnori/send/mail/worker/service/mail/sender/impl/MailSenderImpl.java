package org.gnori.send.mail.worker.service.mail.sender.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.gnori.data.model.Message;
import org.gnori.send.mail.worker.service.mail.filler.MailMessageData;
import org.gnori.send.mail.worker.service.mail.filler.MailMessageFiller;
import org.gnori.send.mail.worker.service.mail.recipinets.parser.MailRecipientsParser;
import org.gnori.send.mail.worker.service.mail.sender.MailSender;
import org.gnori.data.model.FileData;
import org.gnori.shared.service.loader.file.FileLoader;
import org.gnori.data.model.FileType;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Document;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.File;
import java.util.Optional;

@Log4j2
@Component
@RequiredArgsConstructor
public class MailSenderImpl implements MailSender {

    private final MailRecipientsParser mailRecipientsParser;
    private final MailMessageFiller mailMessageFiller;
    private final FileLoader fileLoader;

    @Override
    public void send(String senderMail, Session session, Message message) {

        final MimeMessage mailMessage = new MimeMessage(session);
        final MailMessageData mailMessageData = createMailMessageData(senderMail, message);

        mailMessageFiller.fill(mailMessage, mailMessageData);
        sendMessages(mailMessage, message.countForRecipient());
        deleteFile(mailMessageData.annex());
    }

    private MailMessageData createMailMessageData(String senderMail, Message message) {

        final InternetAddress[] recipients = mailRecipientsParser.parse(message.recipients());
        final Optional<FileSystemResource> fileSystemResourceOptional = message.hasAnnex()
                ? loadFrom(message.fileData())
                :Optional.empty();

        return new MailMessageData(
                message.title(),
                senderMail,
                recipients,
                message.text(),
                fileSystemResourceOptional.get(),
                message.sentDate()
        );
    }

    private Optional<FileSystemResource> loadFrom(FileData fileData) {

        final FileSystemResource nullableFileSystemResource = fileLoader.loadFile(fileData)
                .fold(fileSystemResource -> fileSystemResource, fileFailure -> null);

        return Optional.ofNullable(nullableFileSystemResource);
    }

    private void sendMessages(MimeMessage mailMessage, Integer countMessage) {

        while (countMessage > 0) {
            try {
                Transport.send(mailMessage);
            } catch (MessagingException e) {
                log.error("bad send mail message: {}", e.getLocalizedMessage());
            }
            countMessage--;
        }
    }

    private void deleteFile(FileSystemResource fileSystemResource) {

        Optional.ofNullable(fileSystemResource)
                .map(FileSystemResource::getFile)
                .map(File::delete)
                .ifPresent(isDeleted -> {
                    if (!isDeleted) {
                        log.error("File:" + fileSystemResource.getFilename() + " not removed");
                    }
                });
    }
}

package org.gnori.send.mail.worker.service.mail.sender.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.gnori.data.flow.Empty;
import org.gnori.data.flow.Result;
import org.gnori.data.model.FileData;
import org.gnori.data.model.Message;
import org.gnori.send.mail.worker.service.mail.filler.MailMessageData;
import org.gnori.send.mail.worker.service.mail.filler.MailMessageFiller;
import org.gnori.send.mail.worker.service.mail.parser.recipinets.MailRecipientsParser;
import org.gnori.send.mail.worker.service.mail.sender.MailFailure;
import org.gnori.send.mail.worker.service.mail.sender.MailSender;
import org.gnori.shared.service.loader.file.FileLoader;
import org.springframework.core.io.FileSystemResource;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.MimeMessage;
import java.io.File;
import java.util.Optional;

@Log4j2
@Component
@RequiredArgsConstructor
public class MailSenderImpl implements MailSender {

    private final FileLoader fileLoader;
    private final MailMessageFiller mailMessageFiller;
    private final MailRecipientsParser mailRecipientsParser;

    @Override
    public Result<Empty, MailFailure> send(String senderMail, Session session, Message message) {

        return Result.<MailMessageData, MailFailure>success(createMailMessageData(senderMail, message))
                .flatMapSuccess(mailMessageData ->
                        mailMessageFiller.fill(new MimeMessage(session), mailMessageData)
                                .mapSuccess(mimeMessage -> Pair.of(mailMessageData, mimeMessage))
                )
                .doIfSuccess(pair -> {
                    final MimeMessage filledMimeMessage = pair.getSecond();
                    sendMessages(filledMimeMessage, message.countForRecipient());
                })
                .mapSuccess(Pair::getFirst)
                .mapSuccess(MailMessageData::annex)
                .flatMapSuccess(this::deleteFile);
    }

    private MailMessageData createMailMessageData(String senderMail, Message message) {

        final FileSystemResource fileSystemResource = Optional.ofNullable(message.fileData())
                .flatMap(this::loadFrom)
                .orElse(null);

        return new MailMessageData(
                message.title(),
                senderMail,
                mailRecipientsParser.parse(message.recipients()),
                message.text(),
                fileSystemResource,
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

    private Result<Empty, MailFailure> deleteFile(FileSystemResource fileSystemResource) {

        return Optional.ofNullable(fileSystemResource)
                .map(FileSystemResource::getFile)
                .map(File::delete)
                .map(isDeleted -> {
                    return isDeleted
                            ? Result.<Empty, MailFailure>success(Empty.INSTANCE)
                            : Result.<Empty, MailFailure>failure(MailFailure.NOT_DELETE_FILE);
                })
                .orElse(Result.success(Empty.INSTANCE));
    }
}

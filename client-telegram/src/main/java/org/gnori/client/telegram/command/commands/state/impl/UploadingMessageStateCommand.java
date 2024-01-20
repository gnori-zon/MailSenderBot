package org.gnori.client.telegram.command.commands.state.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.gnori.client.telegram.command.commands.state.StateCommand;
import org.gnori.client.telegram.command.commands.state.StateCommandType;
import org.gnori.client.telegram.service.SendBotMessageService;
import org.gnori.client.telegram.service.impl.MessageRepositoryService;
import org.gnori.client.telegram.utils.command.UtilsCommand;
import org.gnori.data.model.FileData;
import org.gnori.data.model.FileType;
import org.gnori.shared.flow.Result;
import org.gnori.shared.service.loader.file.FileFailure;
import org.gnori.shared.service.loader.file.FileLoader;
import org.gnori.store.domain.service.account.AccountService;
import org.gnori.store.entity.Account;
import org.gnori.store.entity.enums.State;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Document;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.gnori.client.telegram.utils.FileDataParser.*;
import static org.gnori.client.telegram.utils.preparers.CallbackDataPreparer.prepareCallbackDataForCreateMailingMessage;
import static org.gnori.client.telegram.utils.preparers.TextPreparer.*;

@Log4j2
@Component
@RequiredArgsConstructor
public class UploadingMessageStateCommand implements StateCommand {


    private final SendBotMessageService sendBotMessageService;
    private final AccountService modifyDataBaseService;
    private final MessageRepositoryService messageRepositoryService;
    private final FileLoader fileLoader;

    @Override
    public void execute(Account account, Update update) {
        var chatId = update.getMessage().getChatId();
        var lastMessageId = update.getMessage().getMessageId() - 1;
        var newCallbackData = prepareCallbackDataForCreateMailingMessage();
        var message = messageRepositoryService.getMessage(chatId);
        var textForOld = prepareTextForAfterBadDownloadMessage();

        if (update.getMessage().hasDocument()) {
            final Document newDocument = update.getMessage().getDocument();
            final String contentText = getContentText(chatId, newDocument);
            final String titleForMessage = getTitleFromContent(contentText);
            final String textForMessage = getTextFromContent(contentText);
            final List<String> recipients = getRecipientsFromContent(contentText);
            final Integer countForRecipient = getCountForRecipientFromContent(contentText);
            final String sentDateRaw = getSentDateFromContent(contentText);
            // todo: refactor on copy
//            message.setCountForRecipient(countForRecipient);
//            message.setRecipients(recipients);
//            message.setTitle(titleForMessage);
//            message.setText(textForMessage);
            UtilsCommand.parseLocalDate(sentDateRaw)
                    .doIfSuccess(sentDate -> {
                        if (sentDate.isAfter(LocalDate.now())) {
//                            message.setSentDate(newSentDate); // todo: refactor on copy
                        }
                    });

            messageRepositoryService.putMessage(chatId, message);
            textForOld = prepareTextForAfterSuccessDownloadMessage();
        }

        final String text = prepareTextForPreviewMessage(message);

        modifyDataBaseService.updateStateById(chatId, State.DEFAULT);
        sendBotMessageService.editMessage(chatId, lastMessageId, textForOld, Collections.emptyList(), false);
        sendBotMessageService.createChangeableMessage(chatId, text, newCallbackData, true);
    }

    @Override
    public StateCommandType getSupportedType() {
        return StateCommandType.UPLOADING_MESSAGE;
    }

    private String getContentText(Long id, Document doc) {

        return fileLoader.loadFile(new FileData(String.valueOf(id), doc.getFileName(), FileType.DOCUMENT))
                .mapSuccess(FileSystemResource::getFile)
                .flatMapSuccess(file -> {

                    try {

                        return Result.success(Files.readString(file.toPath(), StandardCharsets.UTF_8));
                    } catch (IOException e) {

                        return Result.failure(FileFailure.IO_FAILURE);
                    } finally {

                        boolean isDeleted = file.delete();
                        if (!isDeleted) {
                            log.error("File: {} not removed", file.toURI());
                        }
                    }
                })
                .fold(
                        successText -> successText,
                        failure -> ""
                );
    }
}

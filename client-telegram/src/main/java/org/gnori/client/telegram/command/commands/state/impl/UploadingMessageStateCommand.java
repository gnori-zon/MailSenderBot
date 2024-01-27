package org.gnori.client.telegram.command.commands.state.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.gnori.client.telegram.command.commands.state.StateCommand;
import org.gnori.client.telegram.command.commands.state.StateCommandType;
import org.gnori.client.telegram.service.SendBotMessageService;
import org.gnori.client.telegram.service.impl.message.MessageRepositoryService;
import org.gnori.client.telegram.service.impl.message.MessageUpdateFailure;
import org.gnori.client.telegram.utils.command.UtilsCommand;
import org.gnori.client.telegram.utils.command.UtilsCommandFailure;
import org.gnori.data.model.FileData;
import org.gnori.data.model.FileType;
import org.gnori.data.model.Message;
import org.gnori.shared.flow.Empty;
import org.gnori.shared.flow.Result;
import org.gnori.shared.service.loader.file.FileLoader;
import org.gnori.store.domain.service.account.AccountService;
import org.gnori.store.entity.Account;
import org.gnori.store.entity.enums.State;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Document;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.gnori.client.telegram.utils.FileDataParser.*;
import static org.gnori.client.telegram.utils.preparers.CallbackDataPreparer.prepareCallbackDataForCreateMailingMessage;
import static org.gnori.client.telegram.utils.preparers.TextPreparer.*;
import static org.gnori.client.telegram.utils.preparers.TextPreparer.prepareTextForAfterBadDownloadMessage;

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

        final long chatId = account.getChatId();
        var textForOld = updateMessage(chatId, update)
                .fold(
                        success -> prepareTextForAfterSuccessDownloadMessage(),
                        failure -> prepareTextForAfterBadDownloadMessage()
                );

        final int lastMessageId = update.getMessage().getMessageId() - 1;
        modifyDataBaseService.updateStateById(chatId, State.DEFAULT);
        sendBotMessageService.editMessage(chatId, lastMessageId, textForOld, Collections.emptyList(), false);

        final Message message = messageRepositoryService.getMessage(chatId);
        final List<List<String>> newCallbackData = prepareCallbackDataForCreateMailingMessage();
        final String text = prepareTextForPreviewMessage(message);
        sendBotMessageService.createChangeableMessage(chatId, text, newCallbackData, true);
    }

    @Override
    public StateCommandType getSupportedType() {
        return StateCommandType.UPLOADING_MESSAGE;
    }

    private Result<Empty, MessageUpdateFailure> updateMessage(Long chatId, Update update) {

        return extractMessageContent(update)
                .doIfSuccess(messageContent -> {
                    var message = messageRepositoryService.getMessage(chatId);

                    final String titleForMessage = getTitleFromContent(messageContent);
                    final String textForMessage = getTextFromContent(messageContent);
                    final List<String> recipients = getRecipientsFromContent(messageContent);
                    final Integer countForRecipient = getCountForRecipientFromContent(messageContent);
                    final LocalDate sentDate = UtilsCommand.parseLocalDate(getSentDateFromContent(messageContent))
                            .flatMapSuccess(parsedSentDate -> {
                                if (parsedSentDate.isAfter(LocalDate.now())) {
                                    return Result.success(parsedSentDate);
                                }
                                return Result.failure(UtilsCommandFailure.DATE_TIME_PARSE_EXCEPTION);
                            })
                            .fold(success -> success, failure -> null);

                    messageRepositoryService.putMessage(chatId, message.with(titleForMessage, textForMessage, recipients, countForRecipient, sentDate));
                })
                .mapSuccess(messageContent -> Empty.INSTANCE);
    }


    private Result<String, MessageUpdateFailure> extractMessageContent(Update update) {

        return extractDocumentFileData(update)
                .flatMapSuccess(fileData -> fileLoader.loadFile(fileData).mapFailure(failure -> MessageUpdateFailure.INCORRECT_INPUT_TYPE))
                .mapSuccess(FileSystemResource::getFile)
                .flatMapSuccess(this::readFile)
                .mapFailure(failure -> MessageUpdateFailure.INCORRECT_INPUT_TYPE);
    }

    private Result<String, MessageUpdateFailure> readFile(File file) {

        try {

            return Result.success(Files.readString(file.toPath(), StandardCharsets.UTF_8));
        } catch (IOException e) {

            return Result.failure(MessageUpdateFailure.INTERNAL_ERROR);
        } finally {

            boolean isDeleted = file.delete();
            if (!isDeleted) {
                log.error("File: {} not removed", file.toURI());
            }
        }
    }

    private Result<FileData, MessageUpdateFailure> extractDocumentFileData(Update update) {

        if (update.getMessage().hasDocument()) {

            final Document newDocument = update.getMessage().getDocument();
            final FileData fileData = new FileData(newDocument.getFileId(), newDocument.getFileName(), FileType.DOCUMENT);

            return Result.success(fileData);
        }

        return Result.failure(MessageUpdateFailure.INCORRECT_INPUT_TYPE);
    }
}

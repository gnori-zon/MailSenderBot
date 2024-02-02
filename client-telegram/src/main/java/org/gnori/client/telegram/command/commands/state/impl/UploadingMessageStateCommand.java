package org.gnori.client.telegram.command.commands.state.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.gnori.client.telegram.command.commands.callback.impl.back.menustep.MenuStepCommandType;
import org.gnori.client.telegram.command.commands.state.StateCommand;
import org.gnori.client.telegram.command.commands.state.StateCommandType;
import org.gnori.client.telegram.service.bot.SendBotMessageService;
import org.gnori.client.telegram.service.bot.model.CallbackButtonData;
import org.gnori.client.telegram.service.message.MessageStorageImpl;
import org.gnori.client.telegram.service.message.MessageUpdateFailure;
import org.gnori.client.telegram.utils.MessageItemTextExtractor;
import org.gnori.client.telegram.utils.command.UtilsCommand;
import org.gnori.client.telegram.utils.command.UtilsCommandFailure;
import org.gnori.client.telegram.utils.preparers.button.data.ButtonDataPreparer;
import org.gnori.client.telegram.utils.preparers.button.data.callback.CallbackButtonDataPreparerParam;
import org.gnori.client.telegram.utils.preparers.button.data.callback.CallbackButtonDataPresetType;
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
import java.util.Optional;

import static org.gnori.client.telegram.utils.preparers.TextPreparer.*;

@Log4j2
@Component
@RequiredArgsConstructor
public class UploadingMessageStateCommand implements StateCommand {


    private final ButtonDataPreparer<CallbackButtonData, CallbackButtonDataPreparerParam> buttonDataPreparer;
    private final SendBotMessageService sendBotMessageService;
    private final AccountService modifyDataBaseService;
    private final MessageStorageImpl messageStorageImpl;
    private final MessageItemTextExtractor messageItemTextExtractor;
    private final FileLoader fileLoader;

    @Override
    public void execute(Account account, Update update) {

        final long chatId = account.getChatId();
        var textForOld = updateMessage(account.getId() , update)
                .fold(
                        success -> prepareTextForAfterSuccessDownloadMessage(),
                        failure -> prepareTextForAfterBadDownloadMessage()
                );

        final int lastMessageId = update.getMessage().getMessageId() - 1;
        modifyDataBaseService.updateStateById(chatId, State.DEFAULT);
        sendBotMessageService.editMessage(chatId, lastMessageId, textForOld, Collections.emptyList(), false);

        final Message message = messageStorageImpl.getMessage(account.getId());
        final List<CallbackButtonData> newCallbackButtonDataList = buttonDataPreparer.prepare(callbackButtonDataPreparerParamOf());
        final String text = prepareTextForPreviewMessage(message);
        sendBotMessageService.createChangeableMessage(chatId, text, newCallbackData, true);
    }

    @Override
    public StateCommandType getSupportedType() {
        return StateCommandType.UPLOADING_MESSAGE;
    }

    private CallbackButtonDataPreparerParam callbackButtonDataPreparerParamOf() {

        return new CallbackButtonDataPreparerParam(
                CallbackButtonDataPresetType.SELECT_ACTION_MAILING_ITEM,
                MenuStepCommandType.CHANGE_MESSAGE_ITEM,
                true,
                false
        );
    }

    private Result<Empty, MessageUpdateFailure> updateMessage(Long accountId, Update update) {

        return extractMessageContent(update)
                .doIfSuccess(messageContent -> {

                    final Message message = messageStorageImpl.getMessage(accountId);

                    final String titleForMessage = orElseEmpty(messageItemTextExtractor.extractTitle(messageContent));
                    final String textForMessage = orElseEmpty(messageItemTextExtractor.extractText(messageContent));
                    final List<String> recipients = messageItemTextExtractor.extractRecipients(messageContent);
                    final Integer countForRecipient = orElseOne(messageItemTextExtractor.extractCountForRecipient(messageContent));
                    final LocalDate sentDate = messageItemTextExtractor.extractSentDate(messageContent)
                            .map(this::validateDate)
                            .orElse(null);

                    messageStorageImpl.updateMessage(accountId, message.with(titleForMessage, textForMessage, recipients, countForRecipient, sentDate));
                })
                .mapSuccess(messageContent -> Empty.INSTANCE);
    }

    private LocalDate validateDate(String sentDateRaw) {

        return UtilsCommand.parseLocalDate(sentDateRaw)
                .flatMapSuccess(parsedSentDate -> {

                    if (parsedSentDate.isAfter(LocalDate.now())) {
                        return Result.success(parsedSentDate);
                    }

                    return Result.failure(UtilsCommandFailure.DATE_TIME_PARSE_EXCEPTION);
                })
                .fold(success -> success, failure -> null);
    }

    private Integer orElseOne(Optional<Integer> optionalNumber) {

        return optionalNumber
                .orElse(1);
    }

    private String orElseEmpty(Optional<String> optionalString) {

        return optionalString
                .orElse("");
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

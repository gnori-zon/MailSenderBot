package org.gnori.client.telegram.service.command.commands.state.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.gnori.client.telegram.service.bot.BotMessageEditor;
import org.gnori.client.telegram.service.bot.BotMessageSender;
import org.gnori.client.telegram.service.bot.model.button.ButtonData;
import org.gnori.client.telegram.service.bot.model.message.EditBotMessageParam;
import org.gnori.client.telegram.service.bot.model.message.SendBotMessageParam;
import org.gnori.client.telegram.service.command.commands.callback.impl.back.menustep.MenuStepCommandType;
import org.gnori.client.telegram.service.command.commands.state.StateCommand;
import org.gnori.client.telegram.service.command.commands.state.StateCommandType;
import org.gnori.client.telegram.service.command.utils.command.UtilsCommand;
import org.gnori.client.telegram.service.command.utils.command.UtilsCommandFailure;
import org.gnori.client.telegram.service.command.utils.preparers.button.data.ButtonDataPreparer;
import org.gnori.client.telegram.service.command.utils.preparers.button.data.callback.CallbackButtonDataPreparerParam;
import org.gnori.client.telegram.service.command.utils.preparers.button.data.callback.CallbackButtonDataPresetType;
import org.gnori.client.telegram.service.command.utils.text.extractor.MessageItemTextExtractor;
import org.gnori.client.telegram.service.message.MessageStorage;
import org.gnori.client.telegram.service.message.MessageUpdateFailure;
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
import java.util.List;
import java.util.Optional;

@Log4j2
@Component
@RequiredArgsConstructor
public class UploadingMessageStateCommand implements StateCommand {


    private final FileLoader fileLoader;
    private final AccountService accountService;
    private final MessageStorage messageStorage;
    private final BotMessageEditor botMessageEditor;
    private final BotMessageSender botMessageSender;
    private final MessageItemTextExtractor messageItemTextExtractor;
    private final ButtonDataPreparer<ButtonData, CallbackButtonDataPreparerParam> buttonDataPreparer;

    @Override
    public void execute(Account account, Update update) {

        accountService.updateStateById(account.getId(), State.DEFAULT);

        final long chatId = account.getChatId();
        final int lastMessageId = update.getMessage().getMessageId() - 1;
        final String textForOld = updateMessage(account.getId() , update)
                .fold(
                        success -> prepareTextForAfterSuccessDownloadMessage(),
                        failure -> prepareTextForAfterBadDownloadMessage()
                );

        botMessageEditor.edit(new EditBotMessageParam(chatId, lastMessageId, textForOld));

        final Message message = messageStorage.getMessage(account.getId());
        final String text = prepareTextForPreviewMessage(message);
        final List<ButtonData> newCallbackButtonDataList = buttonDataPreparer.prepare(callbackButtonDataPreparerParamOf());

        botMessageSender.send(new SendBotMessageParam(chatId, text, newCallbackButtonDataList));
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

                    final Message message = messageStorage.getMessage(accountId);

                    final String titleForMessage = orElseEmpty(messageItemTextExtractor.extractTitle(messageContent));
                    final String textForMessage = orElseEmpty(messageItemTextExtractor.extractText(messageContent));
                    final List<String> recipients = messageItemTextExtractor.extractRecipients(messageContent);
                    final Integer countForRecipient = orElseOne(messageItemTextExtractor.extractCountForRecipient(messageContent));
                    final LocalDate sentDate = messageItemTextExtractor.extractSentDate(messageContent)
                            .map(this::validateDate)
                            .orElse(null);

                    messageStorage.updateMessage(accountId, message.with(titleForMessage, textForMessage, recipients, countForRecipient, sentDate));
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

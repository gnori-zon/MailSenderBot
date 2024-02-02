package org.gnori.client.telegram.command.commands.state.impl;

import lombok.RequiredArgsConstructor;
import org.gnori.client.telegram.command.commands.callback.impl.back.menustep.MenuStepCommandType;
import org.gnori.client.telegram.command.commands.state.StateCommand;
import org.gnori.client.telegram.command.commands.state.StateCommandType;
import org.gnori.client.telegram.service.bot.SendBotMessageService;
import org.gnori.client.telegram.service.bot.model.CallbackButtonData;
import org.gnori.client.telegram.service.message.MessageStorageImpl;
import org.gnori.client.telegram.service.message.MessageUpdateFailure;
import org.gnori.client.telegram.utils.preparers.button.data.ButtonDataPreparer;
import org.gnori.client.telegram.utils.preparers.button.data.callback.CallbackButtonDataPreparerParam;
import org.gnori.client.telegram.utils.preparers.button.data.callback.CallbackButtonDataPresetType;
import org.gnori.data.model.FileData;
import org.gnori.data.model.FileType;
import org.gnori.data.model.Message;
import org.gnori.shared.flow.Empty;
import org.gnori.shared.flow.Result;
import org.gnori.store.domain.service.account.AccountService;
import org.gnori.store.entity.Account;
import org.gnori.store.entity.enums.State;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.gnori.client.telegram.utils.preparers.TextPreparer.*;

@Component
@RequiredArgsConstructor
public class ChangingMessageItemAnnexStateCommand implements StateCommand {

    private static final String IMAGE_NAME_PATTERN = "IMG_%s";

    private final ButtonDataPreparer<CallbackButtonData, CallbackButtonDataPreparerParam> buttonDataPreparer;
    private final SendBotMessageService sendBotMessageService;
    private final MessageStorageImpl messageStorageImpl;
    private final AccountService accountService;

    @Override
    public void execute(Account account, Update update) {

        final long chatId = account.getChatId();
        final String textForOld = updateMessage(account.getId(), update)
                .fold(
                        success -> prepareSuccessTextForChangingLastMessage(),
                        failure -> prepareFailureTextIncorrectTypeForChangingLastMessage()
                );

        accountService.updateStateById(chatId, State.DEFAULT);

        final int lastMessageId = update.getMessage().getMessageId() - 1;
        sendBotMessageService.editMessage(chatId, lastMessageId, textForOld, Collections.emptyList(), false);

        final Message message = messageStorageImpl.getMessage(account.getId());
        final String text = prepareTextForPreviewMessage(message);
        final List<CallbackButtonData> newCallbackButtonDataList = buttonDataPreparer.prepare(callbackButtonDataPreparerParamOf());

        sendBotMessageService.createChangeableMessage(chatId, text, newCallbackData, true);
    }

    @Override
    public StateCommandType getSupportedType() {
        return StateCommandType.CHANGING_MESSAGE_ITEM_ANNEX;
    }


    private CallbackButtonDataPreparerParam callbackButtonDataPreparerParamOf() {

        return new CallbackButtonDataPreparerParam(
                CallbackButtonDataPresetType.SELECT_CHANGE_MESSAGE_ITEM,
                MenuStepCommandType.CHANGE_MESSAGE_ITEM,
                true,
                false
        );
    }

    private Result<Empty, MessageUpdateFailure> updateMessage(long accountId, Update update) {

        final Message message = messageStorageImpl.getMessage(accountId);

        return extractFileData(update)
                .mapSuccess(message::withFileData)
                .doIfSuccess(newMessage -> messageStorageImpl.updateMessage(accountId, newMessage))
                .mapSuccess(newMessage -> Empty.INSTANCE);
    }

    private Result<FileData, MessageUpdateFailure> extractFileData(Update update) {

        if (update.getMessage().hasDocument()) {

            return extractDocumentFileData(update);
        } else if (update.getMessage().hasPhoto()) {

            return extractPhotoFileData(update);
        }

        return Result.failure(MessageUpdateFailure.INCORRECT_INPUT_TYPE);
    }

    private Result<FileData, MessageUpdateFailure> extractDocumentFileData(Update update) {

        return Optional.ofNullable(update.getMessage().getDocument())
                .map(newDocument ->
                        new FileData(
                                newDocument.getFileId(),
                                newDocument.getFileName(),
                                FileType.DOCUMENT
                        )
                )
                .map(Result::<FileData, MessageUpdateFailure>success)
                .orElseGet(() -> Result.failure(MessageUpdateFailure.INCORRECT_INPUT_TYPE));
    }

    private Result<FileData, MessageUpdateFailure> extractPhotoFileData(Update update) {


        return Optional.ofNullable(update.getMessage().getPhoto())
                .map(List::size)
                .map(countPhoto ->
                        (countPhoto > 1)
                                ? countPhoto - 1
                                : 0
                )
                .map(photoIndex -> update.getMessage().getPhoto().get(photoIndex))
                .map(newPhoto ->
                        new FileData(
                                newPhoto.getFileId(),
                                IMAGE_NAME_PATTERN.formatted(update.getMessage().getChatId()),
                                FileType.PHOTO
                        )
                )
                .map(Result::<FileData, MessageUpdateFailure>success)
                .orElseGet(() -> Result.failure(MessageUpdateFailure.INCORRECT_INPUT_TYPE));
    }
}
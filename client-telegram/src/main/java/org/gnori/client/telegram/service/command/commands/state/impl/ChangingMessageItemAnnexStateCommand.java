package org.gnori.client.telegram.service.command.commands.state.impl;

import lombok.RequiredArgsConstructor;
import org.gnori.client.telegram.service.account.updater.AccountUpdater;
import org.gnori.client.telegram.service.bot.message.BotMessageEditor;
import org.gnori.client.telegram.service.bot.message.BotMessageSender;
import org.gnori.client.telegram.service.bot.message.model.button.ButtonData;
import org.gnori.client.telegram.service.bot.message.model.message.EditBotMessageParam;
import org.gnori.client.telegram.service.bot.message.model.message.SendBotMessageParam;
import org.gnori.client.telegram.service.command.commands.callback.impl.back.menustep.MenuStepCommandType;
import org.gnori.client.telegram.service.command.commands.state.StateCommand;
import org.gnori.client.telegram.service.command.commands.state.StateCommandType;
import org.gnori.client.telegram.service.command.utils.preparers.button.data.ButtonDataPreparer;
import org.gnori.client.telegram.service.command.utils.preparers.button.data.callback.CallbackButtonDataPreparerParam;
import org.gnori.client.telegram.service.command.utils.preparers.button.data.callback.CallbackButtonDataPresetType;
import org.gnori.client.telegram.service.command.utils.preparers.text.TextPreparer;
import org.gnori.client.telegram.service.command.utils.preparers.text.param.PatternTextPreparerParam;
import org.gnori.client.telegram.service.command.utils.preparers.text.param.SimpleTextPreparerParam;
import org.gnori.client.telegram.service.message.MessageStorage;
import org.gnori.client.telegram.service.message.MessageUpdateFailure;
import org.gnori.data.dto.MessageDto;
import org.gnori.data.model.FileData;
import org.gnori.data.model.FileType;
import org.gnori.data.model.Message;
import org.gnori.data.flow.Empty;
import org.gnori.data.flow.Result;
import org.gnori.data.entity.Account;
import org.gnori.data.entity.enums.State;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ChangingMessageItemAnnexStateCommand implements StateCommand {

    private static final String IMAGE_NAME_PATTERN = "IMG_%s";

    private final TextPreparer textPreparer;
    private final AccountUpdater accountUpdater;
    private final MessageStorage messageStorage;
    private final BotMessageEditor botMessageEditor;
    private final BotMessageSender botMessageSender;
    private final ButtonDataPreparer<ButtonData, CallbackButtonDataPreparerParam> buttonDataPreparer;

    @Override
    public void execute(Account account, Update update) {

        accountUpdater.updateState(account.getId(), State.DEFAULT);

        final long chatId = account.getChatId();
        final int lastMessageId = update.getMessage().getMessageId() - 1;
        final String textForOld = updateMessage(account.getId(), update)
                .fold(
                        success -> textPreparer.prepare(SimpleTextPreparerParam.DEFAULT_SUCCESS),
                        failure -> textPreparer.prepare(SimpleTextPreparerParam.AFTER_INCORRECT_TYPE)
                );

        botMessageEditor.edit(new EditBotMessageParam(chatId, lastMessageId, textForOld));

        final Message message = messageStorage.getMessage(account.getId());

        final MessageDto messageDto = new MessageDto(message);
        final String text = textPreparer.prepare(PatternTextPreparerParam.previewMessage(messageDto));
        final List<ButtonData> newCallbackButtonDataList = buttonDataPreparer.prepare(callbackButtonDataPreparerParamOf());

        botMessageSender.send(new SendBotMessageParam(chatId, text, newCallbackButtonDataList));
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

        final Message message = messageStorage.getMessage(accountId);

        return extractFileData(update)
                .mapSuccess(message::withFileData)
                .doIfSuccess(newMessage -> messageStorage.updateMessage(accountId, newMessage))
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
package org.gnori.client.telegram.service.command.commands.state.impl;

import lombok.RequiredArgsConstructor;
import org.gnori.client.telegram.service.account.updater.AccountUpdater;
import org.gnori.client.telegram.service.bot.BotMessageEditor;
import org.gnori.client.telegram.service.bot.BotMessageSender;
import org.gnori.client.telegram.service.bot.model.button.ButtonData;
import org.gnori.client.telegram.service.bot.model.message.EditBotMessageParam;
import org.gnori.client.telegram.service.bot.model.message.SendBotMessageParam;
import org.gnori.client.telegram.service.command.commands.callback.impl.back.menustep.MenuStepCommandType;
import org.gnori.client.telegram.service.command.commands.state.StateCommand;
import org.gnori.client.telegram.service.command.commands.state.StateCommandType;
import org.gnori.client.telegram.service.command.utils.command.UtilsCommand;
import org.gnori.client.telegram.service.command.utils.preparers.button.data.ButtonDataPreparer;
import org.gnori.client.telegram.service.command.utils.preparers.button.data.callback.CallbackButtonDataPreparerParam;
import org.gnori.client.telegram.service.command.utils.preparers.button.data.callback.CallbackButtonDataPresetType;
import org.gnori.client.telegram.service.message.MessageStorage;
import org.gnori.client.telegram.service.message.MessageUpdateFailure;
import org.gnori.data.model.Message;
import org.gnori.shared.flow.Empty;
import org.gnori.shared.flow.Result;
import org.gnori.store.entity.Account;
import org.gnori.store.entity.enums.State;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.gnori.client.telegram.service.command.utils.preparers.TextPreparer.prepareSuccessTextForChangingLastMessage;

@Component
@RequiredArgsConstructor
public class ChangingMessageItemSentDateStateCommand implements StateCommand {

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
                        success -> prepareSuccessTextForChangingLastMessage(),
                        failure -> prepareTextInvalidDateForAfterChangeSentDateMessage()
                );

        botMessageEditor.edit(new EditBotMessageParam(chatId, lastMessageId, textForOld));

        final Message message = messageStorage.getMessage(chatId);
        final String text = prepareTextForPreviewMessage(message);
        final List<ButtonData> newCallbackButtonDataList = buttonDataPreparer.prepare(callbackButtonDataPreparerParamOf());

        botMessageSender.send(new SendBotMessageParam(chatId, text, newCallbackButtonDataList));
    }

    @Override
    public StateCommandType getSupportedType() {
        return StateCommandType.CHANGING_MESSAGE_ITEM_SENT_DATE;
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

        return extractSentDate(update)
                .flatMapSuccess(this::validateSentDate)
                .doIfSuccess(newSentDate -> {

                    final Message message = messageStorage.getMessage(accountId);
                    messageStorage.updateMessage(accountId, message.withSentDate(newSentDate));
                })
                .mapSuccess(newSentDate -> Empty.INSTANCE);
    }

    private Result<LocalDate, MessageUpdateFailure> validateSentDate(LocalDate sendDate) {

        if (LocalDate.now().isAfter(sendDate)) {
            return Result.success(sendDate);
        }

        return Result.failure(MessageUpdateFailure.INCORRECT_INPUT_TYPE);
    }

    private Result<LocalDate, MessageUpdateFailure> extractSentDate(Update update) {

        return Optional.ofNullable(update.getMessage().getText())
                .map(UtilsCommand::parseLocalDate)
                .map(resultParsed -> resultParsed.mapFailure(failure -> MessageUpdateFailure.INCORRECT_INPUT_TYPE))
                .orElseGet(() -> Result.failure(MessageUpdateFailure.INCORRECT_INPUT_TYPE));
    }
}

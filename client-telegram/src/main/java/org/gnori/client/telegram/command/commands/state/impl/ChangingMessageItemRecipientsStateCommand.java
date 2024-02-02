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
import org.gnori.data.model.Message;
import org.gnori.shared.flow.Empty;
import org.gnori.shared.flow.Result;
import org.gnori.store.domain.service.account.AccountService;
import org.gnori.store.entity.Account;
import org.gnori.store.entity.enums.State;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.gnori.client.telegram.utils.preparers.TextPreparer.*;

@Component
@RequiredArgsConstructor
public class ChangingMessageItemRecipientsStateCommand implements StateCommand {

    private final AccountService accountService;
    private final SendBotMessageService sendBotMessageService;
    private final MessageStorageImpl messageStorageImpl;
    private final ButtonDataPreparer<CallbackButtonData, CallbackButtonDataPreparerParam> buttonDataPreparer;

    @Override
    public void execute(Account account, Update update) {

        final long chatId = account.getChatId();
        final String textForOld = updateMessage(account.getId(), update)
                .fold(
                        success -> prepareSuccessTextForChangingLastMessage(),
                        failure -> prepareFailureTextIncorrectTypeForChangingLastMessage()
                );

        accountService.updateStateById(account.getId(), State.DEFAULT);

        final int lastMessageId = update.getMessage().getMessageId() - 1;
        sendBotMessageService.editMessage(chatId, lastMessageId, textForOld, Collections.emptyList(), false);

        final Message message = messageStorageImpl.getMessage(account.getId());
        final String text = prepareTextForPreviewMessage(message);
        final List<CallbackButtonData> newCallbackButtonDataList = buttonDataPreparer.prepare(callbackButtonDataPreparerParamOf());

        sendBotMessageService.createChangeableMessage(chatId, text, newCallbackData, true);
    }

    @Override
    public StateCommandType getSupportedType() {
        return StateCommandType.CHANGING_MESSAGE_ITEM_RECIPIENTS;
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

        return extractRecipients(update)
                .doIfSuccess(newRecipients -> {

                    final Message message = messageStorageImpl.getMessage(accountId);
                    messageStorageImpl.updateMessage(accountId, message.withRecipients(newRecipients));
                })
                .mapSuccess(newRecipients -> Empty.INSTANCE);
    }

    private Result<List<String>, MessageUpdateFailure> extractRecipients(Update update) {

        return Optional.ofNullable(update.getMessage().getText())
                .map(text -> text.split(","))
                .map(newRecipientsRaw ->
                        Arrays.stream(newRecipientsRaw)
                                .map(String::trim)
                                .distinct()
                                .toList()
                )
                .map(Result::<List<String>, MessageUpdateFailure>success)
                .orElseGet(() -> Result.failure(MessageUpdateFailure.INCORRECT_INPUT_TYPE));
    }
}

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
import org.gnori.data.model.Message;
import org.gnori.shared.flow.Empty;
import org.gnori.shared.flow.Result;
import org.gnori.store.entity.Account;
import org.gnori.store.entity.enums.State;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ChangingMessageItemRecipientsStateCommand implements StateCommand {

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

        final MessageDto messageDto = new MessageDto(messageStorage.getMessage(account.getId()));
        final String text = textPreparer.prepare(PatternTextPreparerParam.previewMessage(messageDto));
        final List<ButtonData> newCallbackButtonDataList = buttonDataPreparer.prepare(callbackButtonDataPreparerParamOf());

        botMessageSender.send(new SendBotMessageParam(chatId, text, newCallbackButtonDataList));
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

                    final Message message = messageStorage.getMessage(accountId);
                    messageStorage.updateMessage(accountId, message.withRecipients(newRecipients));
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

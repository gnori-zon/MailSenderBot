package org.gnori.client.telegram.command.commands.text.impl;

import lombok.RequiredArgsConstructor;
import org.gnori.client.telegram.command.commands.text.TextCommand;
import org.gnori.client.telegram.command.commands.text.TextCommandType;
import org.gnori.client.telegram.service.SendBotMessageService;
import org.gnori.client.telegram.service.impl.bot.model.CallbackButtonData;
import org.gnori.client.telegram.utils.preparers.button.data.ButtonDataPreparer;
import org.gnori.client.telegram.utils.preparers.button.data.callback.CallbackButtonDataPreparerParam;
import org.gnori.client.telegram.utils.preparers.button.data.callback.CallbackButtonDataPresetType;
import org.gnori.store.entity.Account;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;

import static org.gnori.client.telegram.utils.preparers.TextPreparer.prepareTextForStartMessage;

@Component
@RequiredArgsConstructor
public class StartCommand implements TextCommand {

    private final SendBotMessageService sendBotMessageService;
    private final ButtonDataPreparer<CallbackButtonData, CallbackButtonDataPreparerParam> buttonDataPreparer;

    @Override
    public void execute(Account account, Update update) {

        final long chatId = account.getChatId();
        final int messageId = update.getMessage().getMessageId();

        final String text = prepareTextForStartMessage();
        final List<CallbackButtonData> newCallbackButtonDataList = buttonDataPreparer.prepare(callbackButtonDataPreparerParamOf());

        sendBotMessageService.editMessage(chatId, messageId, text, newCallbackData, false);
    }

    @Override
    public TextCommandType getSupportedType() {
        return TextCommandType.START;
    }

    private CallbackButtonDataPreparerParam callbackButtonDataPreparerParamOf() {

        return new CallbackButtonDataPreparerParam(
                CallbackButtonDataPresetType.SELECT_START_MENU_ITEM,
                false
        );
    }
}

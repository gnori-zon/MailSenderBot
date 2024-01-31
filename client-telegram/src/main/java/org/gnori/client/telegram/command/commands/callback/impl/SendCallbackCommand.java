package org.gnori.client.telegram.command.commands.callback.impl;

import lombok.RequiredArgsConstructor;
import org.gnori.client.telegram.command.commands.callback.CallbackCommand;
import org.gnori.client.telegram.command.commands.callback.CallbackCommandType;
import org.gnori.client.telegram.command.commands.callback.impl.back.menustep.MenuStepCommandType;
import org.gnori.client.telegram.service.SendBotMessageService;
import org.gnori.client.telegram.service.impl.bot.model.CallbackButtonData;
import org.gnori.client.telegram.utils.preparers.button.data.ButtonDataPreparer;
import org.gnori.client.telegram.utils.preparers.button.data.callback.CallbackButtonDataPreparerParam;
import org.gnori.client.telegram.utils.preparers.button.data.callback.CallbackButtonDataPresetType;
import org.gnori.data.dto.AccountDto;
import org.gnori.store.entity.Account;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;

import static org.gnori.client.telegram.utils.preparers.TextPreparer.prepareTextForSendMessage;

@Component
@RequiredArgsConstructor
public class SendCallbackCommand implements CallbackCommand {

    private final SendBotMessageService sendBotMessageService;
    private final ButtonDataPreparer<CallbackButtonData, CallbackButtonDataPreparerParam> buttonDataPreparer;

    @Override
    public void execute(Account account, Update update) {

        final long chatId = account.getChatId();
        final int messageId = update.getCallbackQuery().getMessage().getMessageId();
        final String text = prepareTextForSendMessage(new AccountDto(account));
        final List<CallbackButtonData> callbackButtonDataList = buttonDataPreparer.prepare(callbackButtonDataPreparerParamOf(account.getEmail() == null));

        sendBotMessageService.editMessage(chatId, messageId, text, newCallbackData, true);
    }

    @Override
    public CallbackCommandType getSupportedType() {
        return CallbackCommandType.SEND;
    }

    private CallbackButtonDataPreparerParam callbackButtonDataPreparerParamOf(boolean hasCurrentMail) {

        return new CallbackButtonDataPreparerParam(
                CallbackButtonDataPresetType.SELECT_SEND_MESSAGE_MODE,
                MenuStepCommandType.SETUP_ACTION_MAILING_ITEM,
                true,
                hasCurrentMail
        );
    }
}

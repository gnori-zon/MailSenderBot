package org.gnori.client.telegram.service.command.commands.callback.impl;

import lombok.RequiredArgsConstructor;
import org.gnori.client.telegram.service.bot.message.BotMessageEditor;
import org.gnori.client.telegram.service.bot.message.model.button.ButtonData;
import org.gnori.client.telegram.service.bot.message.model.message.EditBotMessageParam;
import org.gnori.client.telegram.service.command.commands.callback.CallbackCommand;
import org.gnori.client.telegram.service.command.commands.callback.CallbackCommandType;
import org.gnori.client.telegram.service.command.commands.callback.impl.back.menustep.MenuStepCommandType;
import org.gnori.client.telegram.service.command.utils.preparers.button.data.ButtonDataPreparer;
import org.gnori.client.telegram.service.command.utils.preparers.button.data.callback.CallbackButtonDataPreparerParam;
import org.gnori.client.telegram.service.command.utils.preparers.button.data.callback.CallbackButtonDataPresetType;
import org.gnori.client.telegram.service.command.utils.preparers.text.TextPreparer;
import org.gnori.client.telegram.service.command.utils.preparers.text.param.WithOptionalPartTextPreparerParam;
import org.gnori.data.dto.AccountDto;
import org.gnori.data.entity.Account;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;

@Component
@RequiredArgsConstructor
public class SendCallbackCommand implements CallbackCommand {

    private final TextPreparer textPreparer;
    private final BotMessageEditor botMessageEditor;
    private final ButtonDataPreparer<ButtonData, CallbackButtonDataPreparerParam> buttonDataPreparer;

    @Override
    public void execute(Account account, Update update) {

        final long chatId = account.getChatId();
        final int messageId = update.getCallbackQuery().getMessage().getMessageId();
        final AccountDto accountDto = new AccountDto(account);
        final String text = textPreparer.prepare(WithOptionalPartTextPreparerParam.selectSendMessageMode(accountDto.getEmail() != null && accountDto.isPresentKey()));
        final List<ButtonData> callbackButtonDataList = buttonDataPreparer.prepare(callbackButtonDataPreparerParamOf(account.getEmail() == null));

        botMessageEditor.edit(new EditBotMessageParam(chatId, messageId, text, callbackButtonDataList));
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

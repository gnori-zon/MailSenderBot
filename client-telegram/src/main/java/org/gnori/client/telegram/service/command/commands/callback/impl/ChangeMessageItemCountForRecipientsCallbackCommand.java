package org.gnori.client.telegram.service.command.commands.callback.impl;

import lombok.RequiredArgsConstructor;
import org.gnori.client.telegram.service.bot.BotMessageEditor;
import org.gnori.client.telegram.service.bot.model.button.ButtonData;
import org.gnori.client.telegram.service.bot.model.message.EditBotMessageParam;
import org.gnori.client.telegram.service.command.commands.callback.CallbackCommand;
import org.gnori.client.telegram.service.command.commands.callback.CallbackCommandType;
import org.gnori.client.telegram.service.command.commands.callback.impl.back.menustep.MenuStepCommandType;
import org.gnori.client.telegram.service.command.utils.preparers.button.data.ButtonDataPreparer;
import org.gnori.client.telegram.service.command.utils.preparers.button.data.callback.CallbackButtonDataPreparerParam;
import org.gnori.store.domain.service.account.AccountService;
import org.gnori.store.entity.Account;
import org.gnori.store.entity.enums.State;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;

import static org.gnori.client.telegram.service.command.utils.preparers.TextPreparer.prepareTextForBeforeChangeCountForRecipientsMessage;

@Component
@RequiredArgsConstructor
public class ChangeMessageItemCountForRecipientsCallbackCommand implements CallbackCommand {

    private final AccountService accountService;
    private final BotMessageEditor botMessageEditor;
    private final ButtonDataPreparer<ButtonData, CallbackButtonDataPreparerParam> buttonDataPreparer;

    @Override
    public void execute(Account account, Update update) {

        accountService.updateStateById(account.getId(), State.MESSAGE_COUNT_FOR_RECIPIENT_PENDING);

        final long chatId = account.getChatId();
        final int messageId = update.getCallbackQuery().getMessage().getMessageId();
        final String text = prepareTextForBeforeChangeCountForRecipientsMessage();
        final List<ButtonData> callbackButtonDataList = buttonDataPreparer.prepare(CallbackButtonDataPreparerParam.onlyBack(MenuStepCommandType.SETUP_CREATE_MAILING_ITEM));

        botMessageEditor.edit(new EditBotMessageParam(chatId, messageId, text, callbackButtonDataList));
    }

    @Override
    public CallbackCommandType getSupportedType() {
        return CallbackCommandType.CHANGE_MESSAGE_ITEM_COUNT_FOR_RECIPIENTS;
    }
}

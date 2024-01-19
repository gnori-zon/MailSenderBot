package org.gnori.client.telegram.command.commands.callback.impl;

import lombok.RequiredArgsConstructor;
import org.gnori.client.telegram.command.commands.callback.CallbackCommand;
import org.gnori.client.telegram.command.commands.callback.CallbackCommandType;
import org.gnori.client.telegram.service.SendBotMessageService;
import org.gnori.store.entity.Account;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;

import static org.gnori.client.telegram.utils.preparers.CallbackDataPreparer.prepareCallbackDataForChangeItemMessage;
import static org.gnori.client.telegram.utils.preparers.TextPreparer.prepareTextForChangeItemMessage;

@Component
@RequiredArgsConstructor
public class ChangeMessageItemCallbackCommand implements CallbackCommand {

    private final SendBotMessageService sendBotMessageService;

    @Override
    public void execute(Account account, Update update) {

        final long chatId = account.getChatId();
        final int messageId = update.getCallbackQuery().getMessage().getMessageId();
        final String text = prepareTextForChangeItemMessage();
        final List<List<String>> newCallbackData = prepareCallbackDataForChangeItemMessage();

        sendBotMessageService.editMessage(chatId, messageId, text, newCallbackData, true);
    }

    @Override
    public CallbackCommandType getSupportedType() {
        return CallbackCommandType.CHANGE_MESSAGE_ITEM;
    }
}

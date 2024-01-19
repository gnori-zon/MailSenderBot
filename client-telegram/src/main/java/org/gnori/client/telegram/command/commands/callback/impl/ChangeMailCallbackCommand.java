package org.gnori.client.telegram.command.commands.callback.impl;

import lombok.RequiredArgsConstructor;
import org.gnori.client.telegram.command.commands.callback.CallbackCommand;
import org.gnori.client.telegram.command.commands.callback.CallbackCommandType;
import org.gnori.client.telegram.service.SendBotMessageService;
import org.gnori.store.domain.service.account.AccountService;
import org.gnori.store.entity.Account;
import org.gnori.store.entity.enums.State;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Collections;

import static org.gnori.client.telegram.utils.preparers.TextPreparer.prepareTextForBeforeChangeMailMessage;

@Component
@RequiredArgsConstructor
public class ChangeMailCallbackCommand implements CallbackCommand {

    private final SendBotMessageService sendBotMessageService;
    private final AccountService accountService;

    @Override
    public void execute(Account account, Update update) {

        final long chatId = account.getChatId();
        final int messageId = update.getCallbackQuery().getMessage().getMessageId();
        final String text = prepareTextForBeforeChangeMailMessage();

        accountService.updateStateById(chatId, State.MAIL_PENDING);
        sendBotMessageService.editMessage(chatId, messageId, text, Collections.emptyList(), true);
    }

    @Override
    public CallbackCommandType getSupportedType() {
        return CallbackCommandType.CHANGE_MAIL;
    }
}

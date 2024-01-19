package org.gnori.client.telegram.command.commands.callback.impl;

import lombok.RequiredArgsConstructor;
import org.gnori.client.telegram.command.commands.callback.CallbackCommand;
import org.gnori.client.telegram.command.commands.callback.CallbackCommandType;
import org.gnori.client.telegram.service.SendBotMessageService;
import org.gnori.data.dto.AccountDto;
import org.gnori.store.domain.service.account.AccountService;
import org.gnori.store.entity.Account;
import org.gnori.store.entity.enums.State;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;

import static org.gnori.client.telegram.utils.preparers.CallbackDataPreparer.prepareCallbackDataForProfileMessage;
import static org.gnori.client.telegram.utils.preparers.TextPreparer.prepareTextForProfileMessage;

@Component
@RequiredArgsConstructor
public class ProfileInfoCallbackCommand implements CallbackCommand {

    private final SendBotMessageService sendBotMessageService;
    private final AccountService accountService;

    @Override
    public void execute(Account account, Update update) {

        final long chatId = account.getChatId();
        final int messageId = update.getCallbackQuery().getMessage().getMessageId();
        final String text = prepareTextForProfileMessage(new AccountDto(account));
        final List<List<String>> newCallbackData = prepareCallbackDataForProfileMessage();

        accountService.updateStateById(chatId, State.DEFAULT);
        sendBotMessageService.editMessage(chatId,messageId,text,newCallbackData,true);
    }

    @Override
    public CallbackCommandType getSupportedType() {
        return CallbackCommandType.PROFILE_INFO;
    }
}

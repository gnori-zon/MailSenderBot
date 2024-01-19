package org.gnori.client.telegram.command.commands.text.impl;

import lombok.RequiredArgsConstructor;
import org.gnori.client.telegram.command.commands.text.TextCommand;
import org.gnori.client.telegram.command.commands.text.TextCommandType;
import org.gnori.client.telegram.service.SendBotMessageService;
import org.gnori.store.entity.Account;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;

import static org.gnori.client.telegram.utils.preparers.CallbackDataPreparer.prepareCallbackDataForStartMessage;
import static org.gnori.client.telegram.utils.preparers.TextPreparer.prepareTextForStartMessage;

@Component
@RequiredArgsConstructor
public class StartCommand implements TextCommand {

    private final SendBotMessageService sendBotMessageService;

    @Override
    public void execute(Account account, Update update) {

        final long chatId = account.getChatId();
        final int messageId = update.getMessage().getMessageId();

        final String text = prepareTextForStartMessage();
        final List<List<String>> newCallbackData = prepareCallbackDataForStartMessage();

        sendBotMessageService.editMessage(chatId, messageId, text, newCallbackData, false);
    }

    @Override
    public TextCommandType getSupportedType() {
        return TextCommandType.START;
    }
}

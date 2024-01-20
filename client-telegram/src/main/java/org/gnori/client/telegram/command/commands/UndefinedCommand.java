package org.gnori.client.telegram.command.commands;

import lombok.AllArgsConstructor;
import org.gnori.client.telegram.service.SendBotMessageService;
import org.gnori.store.entity.Account;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Collections;
import java.util.List;

import static org.gnori.client.telegram.utils.preparers.CallbackDataPreparer.prepareCallbackDataForStartMessage;
import static org.gnori.client.telegram.utils.preparers.TextPreparer.prepareTextForLastForUnknownMessage;
import static org.gnori.client.telegram.utils.preparers.TextPreparer.prepareTextForStartMessage;

@Component
@AllArgsConstructor
public class UndefinedCommand {

    private final SendBotMessageService sendBotMessageService;

    public void execute(Account account, Update update) {

        final long chatId = account.getChatId();
        final int lastMessageId = update.getMessage().getMessageId() - 1;
        final String textForOld = prepareTextForLastForUnknownMessage();

        sendBotMessageService.editMessage(chatId, lastMessageId, textForOld, Collections.emptyList(), false);

        final String text = prepareTextForStartMessage();
        final List<List<String>> newCallbackData = prepareCallbackDataForStartMessage();

        sendBotMessageService.createChangeableMessage(chatId, text, newCallbackData, false);
    }
}

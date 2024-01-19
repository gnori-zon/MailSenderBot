package org.gnori.client.telegram.command.commands.callback.impl;

import lombok.RequiredArgsConstructor;
import org.gnori.client.telegram.command.commands.callback.CallbackCommand;
import org.gnori.client.telegram.command.commands.callback.CallbackCommandType;
import org.gnori.client.telegram.service.SendBotMessageService;
import org.gnori.client.telegram.service.impl.MessageRepositoryService;
import org.gnori.store.entity.Account;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Collections;
import java.util.List;

import static org.gnori.client.telegram.utils.preparers.CallbackDataPreparer.prepareCallbackDataForCreateMailingMessage;
import static org.gnori.client.telegram.utils.preparers.TextPreparer.prepareSuccessTextForChangingLastMessage;
import static org.gnori.client.telegram.utils.preparers.TextPreparer.prepareTextForPreviewMessage;

@Component
@RequiredArgsConstructor
public class ClearMessageCallbackCommand implements CallbackCommand {

    private final SendBotMessageService sendBotMessageService;
    private final MessageRepositoryService messageRepositoryService;

    @Override
    public void execute(Account account, Update update) {

        final long chatId = account.getChatId();
        final int messageId = update.getCallbackQuery().getMessage().getMessageId();
        final String text = prepareTextForPreviewMessage();
        final String textForLatMessage = prepareSuccessTextForChangingLastMessage();
        final List<List<String>> newCallbackData = prepareCallbackDataForCreateMailingMessage();

        messageRepositoryService.removeMessage(chatId);
        sendBotMessageService.editMessage(chatId, messageId, textForLatMessage, Collections.emptyList(), false);
        sendBotMessageService.createChangeableMessage(chatId, text, newCallbackData, true);
    }

    @Override
    public CallbackCommandType getSupportedType() {
        return CallbackCommandType.CLEAR_MESSAGE;
    }
}

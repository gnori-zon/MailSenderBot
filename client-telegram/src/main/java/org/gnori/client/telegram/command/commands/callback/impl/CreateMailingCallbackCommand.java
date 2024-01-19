package org.gnori.client.telegram.command.commands.callback.impl;

import lombok.RequiredArgsConstructor;
import org.gnori.client.telegram.command.commands.callback.CallbackCommand;
import org.gnori.client.telegram.command.commands.callback.CallbackCommandType;
import org.gnori.client.telegram.service.SendBotMessageService;
import org.gnori.client.telegram.service.impl.MessageRepositoryService;
import org.gnori.store.entity.Account;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;

import static org.gnori.client.telegram.utils.preparers.CallbackDataPreparer.prepareCallbackDataForCreateMailingMessage;
import static org.gnori.client.telegram.utils.preparers.TextPreparer.prepareTextForPreviewMessage;

@Component
@RequiredArgsConstructor
public class CreateMailingCallbackCommand implements CallbackCommand {

    private final SendBotMessageService sendBotMessageService;
    private final MessageRepositoryService messageRepository;

    @Override
    public void execute(Account account, Update update) {

        final long chatId = account.getChatId();
        final int messageId = update.getCallbackQuery().getMessage().getMessageId();
        final String text = prepareTextForPreviewMessage();
        final List<List<String>> newCallbackData = prepareCallbackDataForCreateMailingMessage();

        messageRepository.removeMessage(chatId);
        sendBotMessageService.editMessage(chatId, messageId, text, newCallbackData, true);
    }

    @Override
    public CallbackCommandType getSupportedType() {
        return CallbackCommandType.CREATE_MAILING;
    }
}

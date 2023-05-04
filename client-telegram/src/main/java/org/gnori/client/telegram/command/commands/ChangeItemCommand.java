package org.gnori.client.telegram.command.commands;

import static org.gnori.client.telegram.utils.preparers.CallbackDataPreparer.prepareCallbackDataForChangeItemMessage;
import static org.gnori.client.telegram.utils.preparers.TextPreparer.prepareTextForChangeItemMessage;

import org.gnori.client.telegram.command.Command;
import org.gnori.client.telegram.service.SendBotMessageService;
import org.telegram.telegrambots.meta.api.objects.Update;
/**
 * Provides a selection of item to change {@link Command}.
 */
public class ChangeItemCommand implements Command {
    private final SendBotMessageService sendBotMessageService;

    public ChangeItemCommand(SendBotMessageService sendBotMessageService) {
        this.sendBotMessageService = sendBotMessageService;
    }

    @Override
    public void execute(Update update) {
        var chatId = update.getCallbackQuery().getMessage().getChatId();
        var text = prepareTextForChangeItemMessage();
        var messageId = update.getCallbackQuery().getMessage().getMessageId();
        var newCallbackData = prepareCallbackDataForChangeItemMessage();

        sendBotMessageService.executeEditMessage(chatId,messageId,text,newCallbackData,true);
    }
}

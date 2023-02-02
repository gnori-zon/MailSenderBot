package org.gnori.mailsenderbot.command.commands;

import org.gnori.mailsenderbot.command.Command;
import org.gnori.mailsenderbot.service.SendBotMessageService;
import org.telegram.telegrambots.meta.api.objects.Update;

import static org.gnori.mailsenderbot.utils.CallbackDataPreparer.prepareCallbackDataForChangeItemMessage;
import static org.gnori.mailsenderbot.utils.TextPreparer.prepareTextForChangeItemMessage;

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

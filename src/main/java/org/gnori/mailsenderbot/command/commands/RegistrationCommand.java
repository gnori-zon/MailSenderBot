package org.gnori.mailsenderbot.command.commands;

import org.gnori.mailsenderbot.command.Command;
import org.gnori.mailsenderbot.service.SendBotMessageService;
import org.telegram.telegrambots.meta.api.objects.Update;

import static org.gnori.mailsenderbot.utils.CallbackDataPreparer.prepareCallbackDataForRegistrationMessage;
import static org.gnori.mailsenderbot.utils.TextPreparer.prepareTextForRegistrationMessage;

public class RegistrationCommand implements Command {
    private final SendBotMessageService sendBotMessageService;

    public RegistrationCommand(SendBotMessageService sendBotMessageService) {
        this.sendBotMessageService = sendBotMessageService;
    }

    @Override
    public void execute(Update update) {
        var message = update.getMessage();
        var chatId = message.getChatId();
        var textForMessage = prepareTextForRegistrationMessage();
        var newCallbackData = prepareCallbackDataForRegistrationMessage();

        sendBotMessageService.createChangeableMessage(chatId,textForMessage,newCallbackData,false);

    }
}

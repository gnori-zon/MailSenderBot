package org.gnori.client.telegram.command.commands;

import static org.gnori.client.telegram.utils.preparers.CallbackDataPreparer.prepareCallbackDataForRegistrationMessage;
import static org.gnori.client.telegram.utils.preparers.TextPreparer.prepareTextForRegistrationMessage;

import org.gnori.client.telegram.command.Command;
import org.gnori.client.telegram.service.SendBotMessageService;
import org.telegram.telegrambots.meta.api.objects.Update;
/**
 * Registers a user in the database {@link Command}.
 */
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

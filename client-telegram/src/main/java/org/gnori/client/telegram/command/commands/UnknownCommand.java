package org.gnori.client.telegram.command.commands;

import static org.gnori.client.telegram.utils.preparers.CallbackDataPreparer.prepareCallbackDataForBeginningMessage;
import static org.gnori.client.telegram.utils.preparers.TextPreparer.prepareTextForBeginningMessage;
import static org.gnori.client.telegram.utils.preparers.TextPreparer.prepareTextForLastForUnknownMessage;

import java.util.Collections;
import org.gnori.client.telegram.command.Command;
import org.gnori.client.telegram.service.SendBotMessageService;
import org.telegram.telegrambots.meta.api.objects.Update;
/**
 * Notifies that the command is unknown {@link Command}.
 */
public class UnknownCommand implements Command {
    private final SendBotMessageService sendBotMessageService;

    public UnknownCommand(SendBotMessageService sendBotMessageService) {
        this.sendBotMessageService = sendBotMessageService;
    }

    @Override
    public void execute(Update update) {
        var chatId = update.getMessage().getChatId();
        var lastMessageId = update.getMessage().getMessageId() - 1;
        var textForOld = prepareTextForLastForUnknownMessage();
        var text = prepareTextForBeginningMessage();
        var newCallbackData = prepareCallbackDataForBeginningMessage();

        sendBotMessageService.executeEditMessage(chatId,lastMessageId,textForOld, Collections.emptyList(),false);
        sendBotMessageService.createChangeableMessage(chatId,text,newCallbackData,false);

    }
}

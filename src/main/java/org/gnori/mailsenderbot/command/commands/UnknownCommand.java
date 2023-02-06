package org.gnori.mailsenderbot.command.commands;

import org.gnori.mailsenderbot.command.Command;
import org.gnori.mailsenderbot.service.SendBotMessageService;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Collections;

import static org.gnori.mailsenderbot.utils.CallbackDataPreparer.prepareCallbackDataForBeginningMessage;
import static org.gnori.mailsenderbot.utils.TextPreparer.prepareTextForBeginningMessage;
import static org.gnori.mailsenderbot.utils.TextPreparer.prepareTextForLastForUnknownMessage;
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

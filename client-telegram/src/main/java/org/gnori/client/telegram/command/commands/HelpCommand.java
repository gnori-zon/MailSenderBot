package org.gnori.client.telegram.command.commands;

import static org.gnori.client.telegram.utils.preparers.TextPreparer.prepareTextForHelpMessage;
import static org.gnori.client.telegram.utils.preparers.UrlDatePreparer.prepareUrlsForHelpMessage;

import java.util.Collections;
import org.gnori.client.telegram.command.Command;
import org.gnori.client.telegram.service.SendBotMessageService;
import org.telegram.telegrambots.meta.api.objects.Update;
/**
 * Provides help information for using the user's mail {@link Command}.
 */
public class HelpCommand implements Command {
    private final SendBotMessageService sendBotMessageService;

    public HelpCommand(SendBotMessageService sendBotMessageService) {
        this.sendBotMessageService = sendBotMessageService;
    }

    @Override
    public void execute(Update update) {
        var chatId = update.getCallbackQuery().getMessage().getChatId();
        var messageId = update.getCallbackQuery().getMessage().getMessageId();
        var text = prepareTextForHelpMessage();
        var urlsData = prepareUrlsForHelpMessage();

        sendBotMessageService.executeEditMessage(chatId,messageId,text, Collections.emptyList(),urlsData,true);
    }
}

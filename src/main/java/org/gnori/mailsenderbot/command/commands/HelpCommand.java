package org.gnori.mailsenderbot.command.commands;

import org.gnori.mailsenderbot.command.Command;
import org.gnori.mailsenderbot.service.SendBotMessageService;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Collections;

import static org.gnori.mailsenderbot.utils.TextPreparer.prepareTextForHelpMessage;
import static org.gnori.mailsenderbot.utils.UrlDatePreparer.prepareUrlsForHelpMessage;
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

package org.gnori.mailsenderbot.command.commands;

import org.gnori.mailsenderbot.command.Command;
import org.gnori.mailsenderbot.service.ModifyDataBaseService;
import org.gnori.mailsenderbot.service.SendBotMessageService;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Collections;

import static org.gnori.mailsenderbot.utils.preparers.TextPreparer.prepareTextForMessage;
/**
 * Displays the mailing history of the user {@link Command}.
 */
public class MailingHistoryCommand implements Command {
    private final ModifyDataBaseService modifyDataBaseService;
    private final SendBotMessageService sendBotMessageService;

    public MailingHistoryCommand(ModifyDataBaseService modifyDataBaseService, SendBotMessageService sendBotMessageService) {
        this.modifyDataBaseService = modifyDataBaseService;
        this.sendBotMessageService = sendBotMessageService;
    }

    @Override
    public void execute(Update update) {
        var chatId = update.getCallbackQuery().getMessage().getChatId();
        var mailingHistory = modifyDataBaseService.getMailingHistoryById(chatId);
        var text = prepareTextForMessage(mailingHistory);
        var messageId = update.getCallbackQuery().getMessage().getMessageId();

        sendBotMessageService.executeEditMessage(chatId,messageId,text, Collections.emptyList(),true);
    }


}

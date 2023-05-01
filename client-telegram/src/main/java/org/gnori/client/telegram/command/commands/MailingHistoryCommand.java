package org.gnori.client.telegram.command.commands;


import static org.gnori.client.telegram.utils.preparers.TextPreparer.prepareTextForMessage;

import java.util.Collections;
import org.gnori.client.telegram.command.Command;
import org.gnori.client.telegram.service.SendBotMessageService;
import org.gnori.data.dto.MailingHistoryDto;
import org.gnori.store.dao.ModifyDataBaseService;
import org.gnori.store.entity.MailingHistory;
import org.telegram.telegrambots.meta.api.objects.Update;
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
        var text = prepareTextForMessage(new MailingHistoryDto(mailingHistory.orElse(new MailingHistory())));
        var messageId = update.getCallbackQuery().getMessage().getMessageId();

        sendBotMessageService.executeEditMessage(chatId,messageId,text, Collections.emptyList(),true);
    }


}

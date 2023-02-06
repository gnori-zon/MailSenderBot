package org.gnori.mailsenderbot.command.commands;

import org.gnori.mailsenderbot.command.Command;
import org.gnori.mailsenderbot.dto.AccountDto;
import org.gnori.mailsenderbot.service.ModifyDataBaseService;
import org.gnori.mailsenderbot.service.SendBotMessageService;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;

import static org.gnori.mailsenderbot.utils.CallbackDataPreparer.prepareCallbackDataForSendMessage;
import static org.gnori.mailsenderbot.utils.TextPreparer.prepareTextForSendMessage;
/**
 * Provides a choice of sending method {@link Command}.
 */
public class SendCommand implements Command {
    private final SendBotMessageService sendBotMessageService;
    private final ModifyDataBaseService modifyDataBaseService;

    public SendCommand(SendBotMessageService sendBotMessageService,ModifyDataBaseService modifyDataBaseService) {
        this.sendBotMessageService = sendBotMessageService;
        this.modifyDataBaseService = modifyDataBaseService;
    }

    @Override
    public void execute(Update update) {
        var chatId = update.getCallbackQuery().getMessage().getChatId();
        var messageId = update.getCallbackQuery().getMessage().getMessageId();
        var account = modifyDataBaseService.findAccountById(chatId);
        var text = prepareTextForSendMessage(account);
        var newCallbackData = prepareCallbackDataForSendMessage(account);

        sendBotMessageService.executeEditMessage(chatId,messageId,text,newCallbackData,true);
    }
}
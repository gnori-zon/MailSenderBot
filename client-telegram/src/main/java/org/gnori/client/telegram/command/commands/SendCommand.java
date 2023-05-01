package org.gnori.client.telegram.command.commands;

import static org.gnori.client.telegram.utils.preparers.CallbackDataPreparer.prepareCallbackDataForSendMessage;
import static org.gnori.client.telegram.utils.preparers.TextPreparer.prepareTextForSendMessage;

import org.gnori.client.telegram.command.Command;
import org.gnori.client.telegram.service.SendBotMessageService;
import org.gnori.data.dto.AccountDto;
import org.gnori.store.dao.ModifyDataBaseService;
import org.gnori.store.entity.Account;
import org.telegram.telegrambots.meta.api.objects.Update;
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
        var text = prepareTextForSendMessage(new AccountDto(account.orElse(new Account())));
        var newCallbackData = prepareCallbackDataForSendMessage(new AccountDto(account.orElse(new Account())));

        sendBotMessageService.executeEditMessage(chatId,messageId,text,newCallbackData,true);
    }
}
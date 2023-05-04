package org.gnori.client.telegram.command.commands;

import static org.gnori.client.telegram.utils.preparers.CallbackDataPreparer.prepareCallbackDataForProfileMessage;
import static org.gnori.client.telegram.utils.preparers.TextPreparer.prepareTextForProfileMessage;

import org.gnori.client.telegram.command.Command;
import org.gnori.client.telegram.service.SendBotMessageService;
import org.gnori.data.dto.AccountDto;
import org.gnori.store.dao.ModifyDataBaseService;
import org.gnori.store.entity.Account;
import org.gnori.store.entity.enums.State;
import org.telegram.telegrambots.meta.api.objects.Update;
/**
 * Displays user data {@link Command}.
 */
public class ProfileCommand implements Command {
    private final ModifyDataBaseService modifyDataBaseService;
    private final SendBotMessageService sendBotMessageService;

    public ProfileCommand(ModifyDataBaseService modifyDataBaseService, SendBotMessageService sendBotMessageService) {
        this.modifyDataBaseService = modifyDataBaseService;
        this.sendBotMessageService = sendBotMessageService;
    }

    @Override
    public void execute(Update update) {
        var chatId = update.getCallbackQuery().getMessage().getChatId();
        var account = modifyDataBaseService.findAccountById(chatId);
        var text = prepareTextForProfileMessage(new AccountDto(account.orElse(new Account())));
        var messageId = update.getCallbackQuery().getMessage().getMessageId();
        var newCallbackData = prepareCallbackDataForProfileMessage();

        modifyDataBaseService.updateStateById(chatId, State.NOTHING_PENDING);

        sendBotMessageService.executeEditMessage(chatId,messageId,text,newCallbackData,true);
    }
}

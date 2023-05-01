package org.gnori.client.telegram.command.commands;

import static org.gnori.client.telegram.utils.UtilsCommand.checkedRegistrationCommand;
import static org.gnori.client.telegram.utils.preparers.CallbackDataPreparer.prepareCallbackDataForBeginningMessage;
import static org.gnori.client.telegram.utils.preparers.TextPreparer.prepareTextForBeginningMessage;

import org.gnori.client.telegram.command.Command;
import org.gnori.client.telegram.service.SendBotMessageService;
import org.gnori.store.dao.ModifyDataBaseService;
import org.gnori.store.entity.Account;
import org.gnori.store.entity.MailingHistory;
import org.gnori.store.entity.enums.State;
import org.telegram.telegrambots.meta.api.objects.Update;
/**
 * Provides command selection at the start (initial command) {@link Command}.
 */
public class BeginningCommand implements Command {
    private final SendBotMessageService sendBotMessageService;
    private final ModifyDataBaseService modifyDataBaseService;

    public BeginningCommand(SendBotMessageService sendBotMessageService, ModifyDataBaseService modifyDataBaseService) {
        this.sendBotMessageService = sendBotMessageService;
        this.modifyDataBaseService = modifyDataBaseService;
    }

    @Override
    public void execute(Update update) {
        var chatId =  update.getCallbackQuery().getMessage().getChatId();
        var text = prepareTextForBeginningMessage();
        var messageId = update.getCallbackQuery().getMessage().getMessageId();
        var newCallbackData = prepareCallbackDataForBeginningMessage();
        var isRegistrationCommand = checkedRegistrationCommand(update.getCallbackQuery().getMessage());
        if(isRegistrationCommand) {
            if (modifyDataBaseService.findAccountById(chatId).isEmpty()) {
                var account = createAccount(chatId);
                modifyDataBaseService.saveAccount(account);
            }
        }else{
            modifyDataBaseService.updateStateById(chatId,State.NOTHING_PENDING);
        }
        sendBotMessageService.executeEditMessage(chatId,messageId,text,newCallbackData,false);
    }


    private Account createAccount(Long chatId) {
        return Account.builder()
                .id(chatId)
                .state(State.NOTHING_PENDING)
                .mailingHistory(new MailingHistory())
                .build();
    }
}

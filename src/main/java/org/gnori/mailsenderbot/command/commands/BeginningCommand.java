package org.gnori.mailsenderbot.command.commands;

import org.gnori.mailsenderbot.command.Command;
import org.gnori.mailsenderbot.entity.Account;
import org.gnori.mailsenderbot.entity.enums.State;
import org.gnori.mailsenderbot.service.ModifyDataBaseService;
import org.gnori.mailsenderbot.service.SendBotMessageService;
import org.telegram.telegrambots.meta.api.objects.Update;

import static org.gnori.mailsenderbot.utils.UtilsCommand.checkedRegistrationCommand;
import static org.gnori.mailsenderbot.utils.UtilsCommand.prepareCallbackDataForBeginningMessage;

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
        var text = "Выберите необходимый пункт👇🏿";
        var messageId = update.getCallbackQuery().getMessage().getMessageId();
        var newCallbackData = prepareCallbackDataForBeginningMessage();
        var isRegistrationCommand = checkedRegistrationCommand(update.getCallbackQuery().getMessage());
        if(isRegistrationCommand) {
            if (modifyDataBaseService.findAccountById(chatId) == null) {
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
                .build();
    }
}

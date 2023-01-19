package org.gnori.mailsenderbot.command.commands;

import org.gnori.mailsenderbot.command.Command;
import org.gnori.mailsenderbot.entity.enums.State;
import org.gnori.mailsenderbot.service.MessageTypesDistributorService;
import org.gnori.mailsenderbot.service.ModifyDataBaseService;
import org.gnori.mailsenderbot.service.SendBotMessageService;
import org.telegram.telegrambots.meta.api.objects.Update;

import static org.gnori.mailsenderbot.command.CommandName.*;

public class BackCommand implements Command {
    private final ModifyDataBaseService modifyDataBaseService;
    private final SendBotMessageService sendBotMessageService;
    private final MessageTypesDistributorService messageTypesDistributorService;

    public BackCommand(ModifyDataBaseService modifyDataBaseService,
                       SendBotMessageService sendBotMessageService,
                       MessageTypesDistributorService messageTypesDistributorService) {
        this.modifyDataBaseService = modifyDataBaseService;
        this.sendBotMessageService = sendBotMessageService;
        this.messageTypesDistributorService = messageTypesDistributorService;
    }

    @Override
    public void execute(Update update) {
        var originalMessage = update.getCallbackQuery().getMessage();
        var firstCallBackDataFromOriginal = originalMessage.getReplyMarkup().getKeyboard().get(0).get(0).getCallbackData();

        if (CHANGE_MAIL.getCommandName().equals(firstCallBackDataFromOriginal)){
            update.getCallbackQuery().setData(BEGINNING.getCommandName());
            messageTypesDistributorService.distributeMessageByType(update);
        }else if (BACK.getCommandName().equals(firstCallBackDataFromOriginal)){
            var account = modifyDataBaseService.findAccountById(originalMessage.getChatId());
                if(account!=null && State.NOTHING_PENDING.equals(account.getState())){
                    update.getCallbackQuery().setData(BEGINNING.getCommandName());
                    messageTypesDistributorService.distributeMessageByType(update);
            }
                if(account!=null && (State.MAIL_PENDING.equals(account.getState()) ||
                                 State.KEY_FOR_MAIL_PENDING.equals(account.getState()))){
                    update.getCallbackQuery().setData(PROFILE.getCommandName());
                    messageTypesDistributorService.distributeMessageByType(update);
            }
        }
    }
}

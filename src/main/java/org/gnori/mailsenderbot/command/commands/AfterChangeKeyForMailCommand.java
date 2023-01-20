package org.gnori.mailsenderbot.command.commands;

import org.gnori.mailsenderbot.command.Command;
import org.gnori.mailsenderbot.dto.AccountDto;
import org.gnori.mailsenderbot.entity.enums.State;
import org.gnori.mailsenderbot.service.MessageTypesDistributorService;
import org.gnori.mailsenderbot.service.ModifyDataBaseService;
import org.gnori.mailsenderbot.service.SendBotMessageService;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Collections;
import java.util.List;

import static org.gnori.mailsenderbot.command.CommandName.PROFILE;
import static org.gnori.mailsenderbot.command.commands.Utils.prepareTextForProfileMessage;

public class AfterChangeKeyForMailCommand implements Command {
    private final ModifyDataBaseService modifyDataBaseService;
    private final SendBotMessageService sendBotMessageService;


    public AfterChangeKeyForMailCommand(ModifyDataBaseService modifyDataBaseService, SendBotMessageService sendBotMessageService) {
        this.modifyDataBaseService = modifyDataBaseService;
        this.sendBotMessageService = sendBotMessageService;
    }

    @Override
    public void execute(Update update) {

        var newKey = update.getMessage().getText();
        var chatId = update.getMessage().getChatId();

        modifyDataBaseService.updateKeyForMailById(chatId,newKey);
        modifyDataBaseService.updateStateById(chatId, State.NOTHING_PENDING);

        var lastMessageId = update.getMessage().getMessageId() - 1;
        var account = modifyDataBaseService.findAccountById(chatId);
        var text = prepareTextForProfileMessage(account);
        List<String> callbackData = List.of("CHANGE_MAIL","CHANGE_KEY");
        List<String> callbackDataText = List.of("Изменить почту","Изменить ключ");
        List<List<String>> newCallbackData = List.of(callbackData, callbackDataText);

        sendBotMessageService.executeEditMessage(chatId,lastMessageId,"✔Успешно", Collections.emptyList(),false);
        sendBotMessageService.createChangeableMessage(chatId,text,newCallbackData,true);

    }

    private CallbackQuery newCallbackQuery(Update update) {
        var callbackQuery = new CallbackQuery();
        callbackQuery.setData(PROFILE.getCommandName());
        callbackQuery.setMessage(update.getMessage());
        return callbackQuery;
    }

}

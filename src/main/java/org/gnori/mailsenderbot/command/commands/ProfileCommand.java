package org.gnori.mailsenderbot.command.commands;

import org.gnori.mailsenderbot.command.Command;
import org.gnori.mailsenderbot.dto.AccountDto;
import org.gnori.mailsenderbot.entity.enums.State;
import org.gnori.mailsenderbot.service.ModifyDataBaseService;
import org.gnori.mailsenderbot.service.SendBotMessageService;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;

import static org.gnori.mailsenderbot.command.commands.Utils.prepareTextForProfileMessage;

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
        var text = prepareTextForProfileMessage(account);
        var messageId = update.getCallbackQuery().getMessage().getMessageId();
        List<String> callbackData = List.of("CHANGE_MAIL","CHANGE_KEY");
        List<String> callbackDataText = List.of("Изменить почту","Изменить ключ");
        List<List<String>> newCallbackData = List.of(callbackData, callbackDataText);

        modifyDataBaseService.updateStateById(chatId, State.NOTHING_PENDING);

        sendBotMessageService.executeEditMessage(chatId,messageId,text,newCallbackData,true);
    }
}

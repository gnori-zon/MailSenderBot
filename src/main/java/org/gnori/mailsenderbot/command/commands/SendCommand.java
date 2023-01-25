package org.gnori.mailsenderbot.command.commands;

import org.gnori.mailsenderbot.command.Command;
import org.gnori.mailsenderbot.service.ModifyDataBaseService;
import org.gnori.mailsenderbot.service.SendBotMessageService;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;

public class SendCommand implements Command {
    private final SendBotMessageService sendBotMessageService;
    private final ModifyDataBaseService modifyDataBaseService;

    public SendCommand(SendBotMessageService sendBotMessageService,ModifyDataBaseService modifyDataBaseService) {
        this.sendBotMessageService = sendBotMessageService;
        this.modifyDataBaseService = modifyDataBaseService;
    }

    @Override
    public void execute(Update update) {
        //TODO added checking email.isPresent()
        var chatId = update.getCallbackQuery().getMessage().getChatId();
        var text = "*Выберите способ отправки:*";
        var messageId = update.getCallbackQuery().getMessage().getMessageId();
        var account = modifyDataBaseService.findAccountById(chatId);
        List<String> callbackData = List.of("SEND_ANONYMOUSLY", "SEND_CURRENT_MAIL");
        List<String> callbackDataText = List.of("Отправить анонимно", "Отпрвить почтой аккаунта");
        if(!(account.getEmail()!=null && account.hasKey())){
            callbackData = List.of("SEND_ANONYMOUSLY");
            callbackDataText = List.of("Отправить анонимно");
            text += "\nВы можете добавить свою почту для отправки";
        }
        List<List<String>> newCallbackData = List.of(callbackData, callbackDataText);
        sendBotMessageService.executeEditMessage(chatId,messageId,text,newCallbackData,true);
    }
}
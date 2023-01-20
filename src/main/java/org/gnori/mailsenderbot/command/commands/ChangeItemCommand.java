package org.gnori.mailsenderbot.command.commands;

import org.gnori.mailsenderbot.command.Command;
import org.gnori.mailsenderbot.service.SendBotMessageService;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;

public class ChangeItemCommand implements Command {
    private final SendBotMessageService sendBotMessageService;

    public ChangeItemCommand(SendBotMessageService sendBotMessageService) {
        this.sendBotMessageService = sendBotMessageService;
    }

    @Override
    public void execute(Update update) {
        var chatId = update.getCallbackQuery().getMessage().getChatId();
        var text = "*Выберите пункт, для изменения:*";
        var messageId = update.getCallbackQuery().getMessage().getMessageId();
        List<String> callbackData = List.of("CHANGE_ITEM_TITLE",
                                            "CHANGE_ITEM_TEXT",
                                            "CHANGE_ITEM_ANNEX",
                                            "CHANGE_ITEM_RECIPIENTS",
                                            "CHANGE_ITEM_COUNT_FOR_RECIPIENTS");
        List<String> callbackDataText = List.of("Заголовок",
                                                "Текст",
                                                "Приложение",
                                                "Получатели",
                                                "Количество шт. каждому");
        List<List<String>> newCallbackData = List.of(callbackData, callbackDataText);
        sendBotMessageService.executeEditMessage(chatId,messageId,text,newCallbackData,true);
    }
}

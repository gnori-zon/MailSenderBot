package org.gnori.mailsenderbot.command.commands;

import org.gnori.mailsenderbot.command.Command;
import org.gnori.mailsenderbot.service.SendBotMessageService;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Collections;
import java.util.List;

import static org.gnori.mailsenderbot.command.commands.Utils.prepareCallbackDataForBeginningMessage;
import static org.gnori.mailsenderbot.command.commands.Utils.prepareCallbackDataForCreateMailingMessage;

public class UnknownCommand implements Command {
    private final SendBotMessageService sendBotMessageService;

    public UnknownCommand(SendBotMessageService sendBotMessageService) {
        this.sendBotMessageService = sendBotMessageService;
    }

    @Override
    public void execute(Update update) {
        var chatId = update.getMessage().getChatId();
        var lastMessageId = update.getMessage().getMessageId() - 1;
        var textForOld = "Данная команда не реализована👀\n" +
                "Пожалуйста, используйте кнопки👌";
        var text = "Выберите необходимый пункт👇🏿";
        List<String> callbackData = List.of("MAILING_HISTORY","CREATE_MAILING", "PROFILE");
        List<String> callbackDataText = List.of("📃История рассылок","📧Создать рассылку", "⚙Профиль");
        var newCallbackData = prepareCallbackDataForBeginningMessage();

        sendBotMessageService.executeEditMessage(chatId,lastMessageId,textForOld, Collections.emptyList(),false);
        sendBotMessageService.createChangeableMessage(chatId,text,newCallbackData,true);

    }
}

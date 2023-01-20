package org.gnori.mailsenderbot.command.commands;

import org.gnori.mailsenderbot.command.Command;
import org.gnori.mailsenderbot.entity.enums.State;
import org.gnori.mailsenderbot.service.SendBotMessageService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.ArrayList;
import java.util.List;

public class RegistrationCommand implements Command {
    private final SendBotMessageService sendBotMessageService;

    public RegistrationCommand(SendBotMessageService sendBotMessageService) {
        this.sendBotMessageService = sendBotMessageService;
    }

    @Override
    public void execute(Update update) {
        var message = update.getMessage();
        var chatId = message.getChatId();
        var stateForChange = State.NOTHING_PENDING;
        var textForMessage = "Кликните по кнопке, для начала работы";
        List<String> callbackData = List.of("BEGINNING");
        List<String> callbackDataText = List.of("✔Just click button");
        List<List<String>> newCallbackData = List.of(callbackData,callbackDataText);

        //TODO changeStateForAccount

        sendBotMessageService.createChangeableMessage(chatId,textForMessage,newCallbackData,false);

    }
}

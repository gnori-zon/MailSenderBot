package org.gnori.mailsenderbot.command;

import org.gnori.mailsenderbot.service.SendBotMessageService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component //TODO заменить на контейнер с командами
public class RegistrationCommand implements Command {
    private final SendBotMessageService sendBotMessageService;

    public RegistrationCommand(SendBotMessageService sendBotMessageService) {
        this.sendBotMessageService = sendBotMessageService;
    }

    @Override
    public void execute(Update update) {
        sendBotMessageService.sendMessages(update.getMessage().getChatId(),
                "Для использования необходимо зарегестрироваться! \n ✔Just click button");
    }
}

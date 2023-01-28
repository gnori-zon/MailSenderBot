package org.gnori.mailsenderbot.command;

import org.gnori.mailsenderbot.command.commands.RegistrationCommand;
import org.gnori.mailsenderbot.service.SendBotMessageService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import static org.gnori.mailsenderbot.utils.UtilsCommand.prepareCallbackDataForRegistrationMessage;

public class RegistrationCommandTest {
    SendBotMessageService sendBotMessageService = Mockito.mock(SendBotMessageService.class);

    @Test
    public void shouldProperlyExecuteCommand() {
        var chatId = 12L;
        var text = "Кликните по кнопке, для начала работы";
        var newCallbackData = prepareCallbackDataForRegistrationMessage();
        var withButton = false;
        Update update = new Update();
        Message message = Mockito.mock(Message.class);
        Mockito.when(message.getChatId()).thenReturn(chatId);
        update.setMessage(message);
        var command = new RegistrationCommand(sendBotMessageService);

        command.execute(update);

        Mockito.verify(sendBotMessageService).createChangeableMessage(chatId,text,newCallbackData,withButton);
    }
}

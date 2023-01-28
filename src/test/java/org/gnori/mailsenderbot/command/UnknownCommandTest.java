package org.gnori.mailsenderbot.command;

import org.gnori.mailsenderbot.command.commands.RegistrationCommand;
import org.gnori.mailsenderbot.command.commands.UnknownCommand;
import org.gnori.mailsenderbot.service.SendBotMessageService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Collections;

import static org.gnori.mailsenderbot.utils.UtilsCommand.prepareCallbackDataForBeginningMessage;

public class UnknownCommandTest {
    SendBotMessageService sendBotMessageService = Mockito.mock(SendBotMessageService.class);
    @Test
    public void shouldProperlyExecuteCommand() {
        var chatId = 12L;
        var messageId = 12;
        var textForOld = "Данная команда не реализована👀\n" +
                "Пожалуйста, используйте кнопки👌";
        var text = "Выберите необходимый пункт👇🏿";
        var newCallbackData = prepareCallbackDataForBeginningMessage();
        Update update = new Update();
        Message message = Mockito.mock(Message.class);
        Mockito.when(message.getChatId()).thenReturn(chatId);
        Mockito.when(message.getMessageId()).thenReturn(messageId+1);// because lastBotMessage has this messageId-1
        update.setMessage(message);
        var command = new UnknownCommand(sendBotMessageService);

        command.execute(update);

        Mockito.verify(sendBotMessageService).executeEditMessage(chatId,messageId,textForOld, Collections.emptyList(),false);
        Mockito.verify(sendBotMessageService).createChangeableMessage(chatId,text,newCallbackData,false);

    }
}

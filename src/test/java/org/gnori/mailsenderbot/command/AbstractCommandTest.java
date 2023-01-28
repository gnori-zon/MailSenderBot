package org.gnori.mailsenderbot.command;

import org.gnori.mailsenderbot.controller.TelegramBot;
import org.gnori.mailsenderbot.service.SendBotMessageService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;

public abstract class AbstractCommandTest {
    SendBotMessageService sendBotMessageService = Mockito.mock(SendBotMessageService.class);

    public SendBotMessageService getSendBotMessageService() {
        return sendBotMessageService;
    }

    abstract String getCommandMessage();
    abstract List<List<String>> getCallbackData();
    abstract boolean withButton();
    abstract Command getCommand();

    @Test
    public void shouldProperlyExecuteCommand() {
        //given
        var chatId = 12L;
        var messageId = 12;
        var text = getCommandMessage();
        var newCallbackData = getCallbackData();
        var withButton = withButton();
        Update update = prepareUpdate(chatId, messageId);

        //when
        getCommand().execute(update);

        //then
        Mockito.verify(sendBotMessageService).executeEditMessage(chatId,messageId,text,newCallbackData,withButton);
    }


    private static Update prepareUpdate(Long chatId, int messageId){
        Update update = new Update();
        CallbackQuery callbackQuery = new CallbackQuery();
        Message message = Mockito.mock(Message.class);
        Mockito.when(message.getChatId()).thenReturn(chatId);
        Mockito.when(message.getMessageId()).thenReturn(messageId);
        callbackQuery.setMessage(message);
        update.setCallbackQuery(callbackQuery);
        return update;
    }
}

package org.gnori.mailsenderbot.command.commands;

import org.gnori.mailsenderbot.command.Command;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Collections;
import java.util.List;

import static org.gnori.mailsenderbot.utils.preparers.CallbackDataPreparer.prepareCallbackDataForBeginningMessage;
import static org.gnori.mailsenderbot.utils.preparers.TextPreparer.prepareTextForBeginningMessage;
import static org.gnori.mailsenderbot.utils.preparers.TextPreparer.prepareTextForLastForUnknownMessage;

public class UnknownCommandTest extends AbstractCommandTest{
    @Override
    public String getCommandMessage() {
        return prepareTextForBeginningMessage();
    }

    @Override
    public List<List<String>> getCallbackData() {
        return prepareCallbackDataForBeginningMessage();
    }

    @Override
    public boolean withButton() {
        return false;
    }

    @Override
    public Command getCommand() {
        return new UnknownCommand(getSendBotMessageService());
    }

    @Test
    @Override
    public void shouldProperlyExecuteCommand() {
        var chatId = 12L;
        var messageId = 12;
        var textForOld = prepareTextForLastForUnknownMessage();
        var text = getCommandMessage();
        var newCallbackData = getCallbackData();
        var withButton = withButton();
        Update update = new Update();
        Message message = Mockito.mock(Message.class);
        Mockito.when(message.getChatId()).thenReturn(chatId);
        Mockito.when(message.getMessageId()).thenReturn(messageId+1);// because lastBotMessage has this messageId-1
        update.setMessage(message);
        var command = getCommand();

        command.execute(update);

        Mockito.verify(sendBotMessageService).executeEditMessage(chatId,messageId,textForOld, Collections.emptyList(),false);
        Mockito.verify(sendBotMessageService).createChangeableMessage(chatId,text,newCallbackData,withButton);

    }
}

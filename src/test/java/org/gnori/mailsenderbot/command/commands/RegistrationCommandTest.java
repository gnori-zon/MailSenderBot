package org.gnori.mailsenderbot.command.commands;

import org.gnori.mailsenderbot.command.Command;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;

import static org.gnori.mailsenderbot.utils.preparers.CallbackDataPreparer.prepareCallbackDataForRegistrationMessage;
import static org.gnori.mailsenderbot.utils.preparers.TextPreparer.prepareTextForRegistrationMessage;

public class RegistrationCommandTest extends AbstractCommandTest {
    @Override
    public String getCommandMessage() {
        return prepareTextForRegistrationMessage();
    }

    @Override
    public List<List<String>> getCallbackData() {
        return prepareCallbackDataForRegistrationMessage();
    }

    @Override
    public boolean withButton() {
        return false;
    }

    @Override
    public Command getCommand() {
        return new RegistrationCommand(getSendBotMessageService());
    }

    @Test
    @Override
    public void shouldProperlyExecuteCommand() {
        var chatId = 12L;
        var text = getCommandMessage();
        var newCallbackData = getCallbackData();
        var withButton = withButton();
        Update update = new Update();
        Message message = Mockito.mock(Message.class);
        Mockito.when(message.getChatId()).thenReturn(chatId);
        update.setMessage(message);
        var command = getCommand();

        command.execute(update);

        Mockito.verify(sendBotMessageService).createChangeableMessage(chatId,text,newCallbackData,withButton);
    }
}

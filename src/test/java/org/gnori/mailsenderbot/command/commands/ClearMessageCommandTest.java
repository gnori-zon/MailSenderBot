package org.gnori.mailsenderbot.command.commands;

import org.gnori.mailsenderbot.command.Command;
import org.gnori.mailsenderbot.model.Message;
import org.gnori.mailsenderbot.repository.MessageRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Collections;
import java.util.List;

import static org.gnori.mailsenderbot.utils.UtilsCommand.prepareCallbackDataForCreateMailingMessage;
import static org.gnori.mailsenderbot.utils.UtilsCommand.prepareTextForPreviewMessage;

public class ClearMessageCommandTest extends AbstractCommandTest {
    private MessageRepository messageRepository = Mockito.mock(MessageRepository.class);

    @Override
    public String getCommandMessage() {
        return prepareTextForPreviewMessage(new Message());
    }

    @Override
    public List<List<String>> getCallbackData() {
        return prepareCallbackDataForCreateMailingMessage();
    }

    @Override
    public boolean withButton() {
        return true;
    }

    @Override
    public Command getCommand() {
        return new ClearMessageCommand(getSendBotMessageService(),messageRepository);
    }
    @Test
    @Override
    public void shouldProperlyExecuteCommand() {
        var chatId = 12L;
        var messageId = 12;
        var message = new Message();
        var text = getCommandMessage();
        var newCallbackData = getCallbackData();
        var withButton = withButton();
        Update update = prepareUpdate(chatId, messageId);

        //when
        getCommand().execute(update);

        //then
        Mockito.verify(messageRepository).putMessage(chatId,message);
        Mockito.verify(sendBotMessageService).executeEditMessage(chatId,messageId,"✔Успешно", Collections.emptyList(),false);
        Mockito.verify(sendBotMessageService).createChangeableMessage(chatId,text,newCallbackData,withButton);
    }
}

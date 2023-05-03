package org.gnori.client.telegram.command.commands;

import static org.gnori.client.telegram.utils.preparers.CallbackDataPreparer.prepareCallbackDataForCreateMailingMessage;
import static org.gnori.client.telegram.utils.preparers.TextPreparer.prepareSuccessTextForChangingLastMessage;
import static org.gnori.client.telegram.utils.preparers.TextPreparer.prepareTextForPreviewMessage;

import java.util.Collections;
import java.util.List;
import org.gnori.client.telegram.command.Command;
import org.gnori.client.telegram.service.impl.MessageRepositoryService;
import org.gnori.data.model.Message;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.telegram.telegrambots.meta.api.objects.Update;

public class ClearMessageCommandTest extends AbstractCommandTest {
    private final MessageRepositoryService messageRepository = Mockito.mock(MessageRepositoryService.class);

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
        Mockito.verify(sendBotMessageService).executeEditMessage(chatId,messageId,prepareSuccessTextForChangingLastMessage(), Collections.emptyList(),false);
        Mockito.verify(sendBotMessageService).createChangeableMessage(chatId,text,newCallbackData,withButton);
    }
}

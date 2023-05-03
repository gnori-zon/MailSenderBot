package org.gnori.client.telegram.command.commands;

import static org.gnori.client.telegram.utils.preparers.CallbackDataPreparer.prepareCallbackDataForCreateMailingMessage;
import static org.gnori.client.telegram.utils.preparers.TextPreparer.prepareTextForPreviewMessage;

import java.util.List;
import org.gnori.client.telegram.command.Command;
import org.gnori.client.telegram.service.impl.MessageRepositoryService;
import org.gnori.data.model.Message;
import org.mockito.Mockito;

public class CreateMailingCommandTest extends AbstractCommandTest {
    private final MessageRepositoryService messageRepository = Mockito.mock(MessageRepositoryService.class);

    @Override
    public String getCommandMessage() {
        var id = 12L; // id from abstractTest
        var message = new Message();

        Mockito.when(messageRepository.getMessage(id)).thenReturn(message);
        return prepareTextForPreviewMessage(message);
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
        return new CreateMailingCommand(getSendBotMessageService(),messageRepository);
    }
}

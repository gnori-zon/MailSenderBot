package org.gnori.mailsenderbot.command.commands;

import org.gnori.mailsenderbot.command.Command;
import org.gnori.mailsenderbot.model.Message;
import org.gnori.mailsenderbot.repository.MessageRepository;
import org.mockito.Mockito;

import java.util.List;

import static org.gnori.mailsenderbot.utils.preparers.CallbackDataPreparer.prepareCallbackDataForCreateMailingMessage;
import static org.gnori.mailsenderbot.utils.preparers.TextPreparer.prepareTextForPreviewMessage;

public class CreateMailingCommandTest extends AbstractCommandTest {
    private final MessageRepository messageRepository = Mockito.mock(MessageRepository.class);

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

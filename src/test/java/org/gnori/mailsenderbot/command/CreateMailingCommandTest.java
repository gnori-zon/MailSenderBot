package org.gnori.mailsenderbot.command;

import org.gnori.mailsenderbot.command.commands.CreateMailingCommand;
import org.gnori.mailsenderbot.model.Message;
import org.gnori.mailsenderbot.repository.MessageRepository;
import org.mockito.Mockito;

import java.util.List;

import static org.gnori.mailsenderbot.utils.UtilsCommand.prepareCallbackDataForCreateMailingMessage;
import static org.gnori.mailsenderbot.utils.UtilsCommand.prepareTextForPreviewMessage;

public class CreateMailingCommandTest extends AbstractCommandTest {
    private MessageRepository messageRepository = Mockito.mock(MessageRepository.class);

    @Override
    String getCommandMessage() {
        var id = 12L; // id from abstractTest
        var message = new Message();

        Mockito.when(messageRepository.getMessage(id)).thenReturn(message);
        return prepareTextForPreviewMessage(message);
    }

    @Override
    List<List<String>> getCallbackData() {
        return prepareCallbackDataForCreateMailingMessage();
    }

    @Override
    boolean withButton() {
        return true;
    }

    @Override
    Command getCommand() {
        return new CreateMailingCommand(getSendBotMessageService(),messageRepository);
    }
}

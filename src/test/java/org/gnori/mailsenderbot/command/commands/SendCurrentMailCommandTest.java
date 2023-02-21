package org.gnori.mailsenderbot.command.commands;

import lombok.SneakyThrows;
import org.gnori.mailsenderbot.command.Command;
import org.gnori.mailsenderbot.entity.MessageSentRecord;
import org.gnori.mailsenderbot.model.Message;
import org.gnori.mailsenderbot.repository.MessageRepository;
import org.gnori.mailsenderbot.service.QueueManager;
import org.gnori.mailsenderbot.service.impl.ModifyDataBaseServiceImpl;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;

import javax.mail.AuthenticationFailedException;
import java.util.Collections;
import java.util.List;

import static org.gnori.mailsenderbot.utils.preparers.CallbackDataPreparer.prepareCallbackDataForBeginningMessage;
import static org.gnori.mailsenderbot.utils.preparers.TextPreparer.prepareTextForSendCurrentAndAnonymouslyMessage;
import static org.gnori.mailsenderbot.utils.preparers.TextPreparer.prepareTextForWaitingForConcreteSendingMessage;
public class SendCurrentMailCommandTest extends AbstractCommandTest {
    private final MessageRepository messageRepository = Mockito.mock(MessageRepository.class);
    private final QueueManager queueManager = Mockito.mock(QueueManager.class);
    private final ModifyDataBaseServiceImpl modifyDataBaseService = Mockito.mock(ModifyDataBaseServiceImpl.class);

    @SneakyThrows
    @Override
    public String getCommandMessage() {
        var id = 12L; // id from abstractTest

        var message = Mockito.mock(Message.class);
        Mockito.when(messageRepository.getMessage(id)).thenReturn(message);

        return prepareTextForSendCurrentAndAnonymouslyMessage();
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
        return new SendCurrentMailCommand(getSendBotMessageService(),
                                          modifyDataBaseService,
                                          messageRepository,
                                          queueManager);
    }

    @SneakyThrows
    @Test
    public void SuccessSend(){
        var id = 12L; // id from abstractTest
        int messageId = 12;
        var message = Mockito.mock(Message.class);
        var text = prepareTextForSendCurrentAndAnonymouslyMessage();
        var newCallbackData = prepareCallbackDataForBeginningMessage();
        var countMessages = message.getRecipients().size() * message.getCountForRecipient();
        Mockito.when(messageRepository.getMessage(id)).thenReturn(message);
        Update update = new Update();
        CallbackQuery callbackQuery = new CallbackQuery();
        org.telegram.telegrambots.meta.api.objects.Message messageTelegram = Mockito.mock(org.telegram.telegrambots.meta.api.objects.Message.class);
        Mockito.when(messageTelegram.getChatId()).thenReturn(id);
        Mockito.when(messageTelegram.getMessageId()).thenReturn(messageId);

        callbackQuery.setMessage(messageTelegram);
        update.setCallbackQuery(callbackQuery);
        var command = new SendCurrentMailCommand(getSendBotMessageService(),modifyDataBaseService,messageRepository,queueManager);

        command.execute(update);

        Mockito.verify(sendBotMessageService).executeEditMessage(id, messageId, prepareTextForWaitingForConcreteSendingMessage(), Collections.emptyList(), false);
        Mockito.verify(sendBotMessageService).executeEditMessage(id, messageId, text, newCallbackData, false);
        Mockito.verify(messageRepository).removeMessage(id);
        Mockito.verify(queueManager).addInQueue(message);
    }

}

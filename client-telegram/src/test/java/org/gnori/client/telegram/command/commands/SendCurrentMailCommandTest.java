package org.gnori.client.telegram.command.commands;

import static org.gnori.client.telegram.utils.preparers.CallbackDataPreparer.prepareCallbackDataForBeginningMessage;
import static org.gnori.client.telegram.utils.preparers.TextPreparer.prepareTextForSendCurrentAndAnonymouslyMessage;
import static org.gnori.client.telegram.utils.preparers.TextPreparer.prepareTextForWaitingForConcreteSendingMessage;

import java.util.Collections;
import java.util.List;
import lombok.SneakyThrows;
import org.gnori.client.telegram.command.Command;
import org.gnori.client.telegram.service.impl.MessageRepositoryService;
import org.gnori.data.model.Message;
import org.gnori.store.dao.ModifyDataBaseServiceImpl;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
public class SendCurrentMailCommandTest extends AbstractCommandTest {
    private final MessageRepositoryService messageRepository = Mockito.mock(MessageRepositoryService.class);
    private final RabbitTemplate rabbitTemplate = Mockito.mock(RabbitTemplate.class);
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
                                          rabbitTemplate,
                                          "ex-name");
    }

    @SneakyThrows
    @Test
    public void SuccessSend(){
        var id = 12L; // id from abstractTest
        int messageId = 12;
        var message = Mockito.mock(Message.class);
        var text = prepareTextForSendCurrentAndAnonymouslyMessage();
        var newCallbackData = prepareCallbackDataForBeginningMessage();
        Mockito.when(messageRepository.getMessage(id)).thenReturn(message);
        Update update = new Update();
        CallbackQuery callbackQuery = new CallbackQuery();
        org.telegram.telegrambots.meta.api.objects.Message messageTelegram = Mockito.mock(org.telegram.telegrambots.meta.api.objects.Message.class);
        Mockito.when(messageTelegram.getChatId()).thenReturn(id);
        Mockito.when(messageTelegram.getMessageId()).thenReturn(messageId);

        callbackQuery.setMessage(messageTelegram);
        update.setCallbackQuery(callbackQuery);
        var command = new SendCurrentMailCommand(getSendBotMessageService(),modifyDataBaseService,messageRepository,rabbitTemplate, "ex-name");

        command.execute(update);

        Mockito.verify(sendBotMessageService).executeEditMessage(id, messageId, prepareTextForWaitingForConcreteSendingMessage(), Collections.emptyList(), false);
        Mockito.verify(sendBotMessageService).executeEditMessage(id, messageId, text, newCallbackData, false);
        Mockito.verify(messageRepository).removeMessage(id);
        Mockito.verify(rabbitTemplate).convertAndSend("ex-name", null, message);
    }

}

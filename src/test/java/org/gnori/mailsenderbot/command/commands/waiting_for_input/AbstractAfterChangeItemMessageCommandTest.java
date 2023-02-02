package org.gnori.mailsenderbot.command.commands.waiting_for_input;

import org.gnori.mailsenderbot.command.commands.AbstractCommandTest;
import org.gnori.mailsenderbot.entity.enums.State;
import org.gnori.mailsenderbot.repository.MessageRepository;
import org.gnori.mailsenderbot.service.ModifyDataBaseService;
import org.gnori.mailsenderbot.service.impl.ModifyDataBaseServiceImpl;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Collections;
import java.util.List;

import static org.gnori.mailsenderbot.utils.CallbackDataPreparer.prepareCallbackDataForCreateMailingMessage;
import static org.gnori.mailsenderbot.utils.TextPreparer.prepareTextForPreviewMessage;

public abstract class AbstractAfterChangeItemMessageCommandTest extends AbstractCommandTest {
    public final ModifyDataBaseService modifyDataBaseService = Mockito.mock(ModifyDataBaseServiceImpl.class);
    public final MessageRepository messageRepository = Mockito.mock(MessageRepository.class);

    public MessageRepository getMessageRepository() {
        return messageRepository;
    }
    public ModifyDataBaseService getModifyDataBaseService() {
        return modifyDataBaseService;
    }
    public abstract String getIncomingMessage();
    public abstract String getTextForOldMessage();

    @Override
    public boolean withButton() {
        return true;
    }
    public boolean isPositive(){
        return true;
    }
    @Override
    public List<List<String>> getCallbackData() {
        return prepareCallbackDataForCreateMailingMessage();
    }

    @Override
    public String getCommandMessage() {
        return null;
    }

    @Override
    @Test
    public void shouldProperlyExecuteCommand() {
        var incomingMessage = getIncomingMessage();
        var textForOldMessage = getTextForOldMessage();
        var chatId = 12L;
        var lastMessageId = 12;
        var newCallbackData = getCallbackData();
        var newState = State.NOTHING_PENDING;

        Update update = new Update();
        Message message = Mockito.mock(Message.class);
        Mockito.when(message.getChatId()).thenReturn(chatId);
        Mockito.when(message.getMessageId()).thenReturn(lastMessageId+1);
        Mockito.when(message.getText()).thenReturn(incomingMessage);
        update.setMessage(message);
        var messageFromRepo = new org.gnori.mailsenderbot.model.Message();
        Mockito.when(messageRepository.getMessage(chatId)).thenReturn(messageFromRepo);


        getCommand().execute(update);

        var text = prepareTextForPreviewMessage(messageFromRepo);

        Mockito.verify(modifyDataBaseService).updateStateById(chatId,newState);
        if(isPositive()) {
            Mockito.verify(messageRepository).putMessage(chatId, messageFromRepo);
        }
        Mockito.verify(sendBotMessageService).executeEditMessage(chatId,lastMessageId,textForOldMessage, Collections.emptyList(),false);
        Mockito.verify(sendBotMessageService).createChangeableMessage(chatId,text,newCallbackData,withButton());


    }
}

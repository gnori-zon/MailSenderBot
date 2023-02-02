package org.gnori.mailsenderbot.command.commands.waiting_for_input;

import org.gnori.mailsenderbot.command.Command;
import org.gnori.mailsenderbot.entity.enums.State;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.telegram.telegrambots.meta.api.objects.Document;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Collections;
import java.util.List;

import static org.gnori.mailsenderbot.utils.TextPreparer.prepareSuccessTextForChangingLastMessage;
import static org.gnori.mailsenderbot.utils.TextPreparer.prepareTextForPreviewMessage;

public class AfterChangeAnnexCommandTest extends AbstractAfterChangeItemMessageCommandTest{
    @Override
    public Command getCommand() {
        return new AfterChangeAnnexCommand(getModifyDataBaseService(),getSendBotMessageService(),getMessageRepository());
    }

    @Override
    public String getIncomingMessage() {
        return null;
    }

    @Override
    public String getTextForOldMessage() {
        return prepareSuccessTextForChangingLastMessage();
    }
    @Override
    public void shouldProperlyExecuteCommand() {
        var incomingMessage = new Document();
        test(null,incomingMessage);

    }
    @Test
    public void shouldProperlyExecuteCommandForPhoto() {
        var incomingMessage = List.of(new PhotoSize());
        test(incomingMessage,null);

    }
    private void test(List<PhotoSize> photo, Document doc){
        Update update = new Update();
        Message message = Mockito.mock(Message.class);
        if(photo!=null) {
            Mockito.when(message.getPhoto()).thenReturn(photo);
        } else if (doc!=null) {
            Mockito.when(message.getDocument()).thenReturn(doc);
        }
        var textForOldMessage = getTextForOldMessage();
        var chatId = 12L;
        var lastMessageId = 12;
        var newCallbackData = getCallbackData();
        var newState = State.NOTHING_PENDING;
        Mockito.when(message.getChatId()).thenReturn(chatId);
        Mockito.when(message.getMessageId()).thenReturn(lastMessageId+1);
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

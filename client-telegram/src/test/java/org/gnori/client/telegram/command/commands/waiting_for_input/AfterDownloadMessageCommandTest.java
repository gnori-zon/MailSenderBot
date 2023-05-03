package org.gnori.client.telegram.command.commands.waiting_for_input;

import static org.gnori.client.telegram.utils.preparers.CallbackDataPreparer.prepareCallbackDataForCreateMailingMessage;
import static org.gnori.client.telegram.utils.preparers.TextPreparer.prepareSuccessTextForChangingLastMessage;
import static org.gnori.client.telegram.utils.preparers.TextPreparer.prepareTextForAfterBadDownloadMessage;
import static org.gnori.client.telegram.utils.preparers.TextPreparer.prepareTextForPreviewMessage;

import java.io.File;
import java.util.Collections;
import org.gnori.client.telegram.service.FileService;
import org.gnori.client.telegram.service.SendBotMessageService;
import org.gnori.client.telegram.service.impl.FileServiceImpl;
import org.gnori.client.telegram.service.impl.MessageRepositoryService;
import org.gnori.client.telegram.service.impl.SendBotMessageServiceImpl;
import org.gnori.data.model.Message;
import org.gnori.store.dao.ModifyDataBaseService;
import org.gnori.store.dao.ModifyDataBaseServiceImpl;
import org.gnori.store.entity.enums.State;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.core.io.FileSystemResource;
import org.telegram.telegrambots.meta.api.objects.Document;
import org.telegram.telegrambots.meta.api.objects.Update;

public class AfterDownloadMessageCommandTest {
    private final SendBotMessageService sendBotMessageService = Mockito.mock(
        SendBotMessageServiceImpl.class);
    private final ModifyDataBaseService modifyDataBaseService = Mockito.mock(
        ModifyDataBaseServiceImpl.class);
    private final MessageRepositoryService messageRepository = Mockito.mock(MessageRepositoryService.class);
    private final FileService fileService = Mockito.mock(FileServiceImpl.class);

    @Test
    public void positiveTest(){
        test(prepareSuccessTextForChangingLastMessage(),true);
    }

    @Test
    public void docIsAbsentTest(){
        test(prepareTextForAfterBadDownloadMessage(),false);
    }

    private void test(String textForOld, boolean isPositive){
        var chatId = 12L;
        var lastMessageId = 12;
        var newCallbackData = prepareCallbackDataForCreateMailingMessage();
        var messageFromRepo = new Message();
        Update update = new Update();
        org.telegram.telegrambots.meta.api.objects.Message message = Mockito.mock(org.telegram.telegrambots.meta.api.objects.Message.class);
        Mockito.when(message.getChatId()).thenReturn(chatId);
        Mockito.when(message.getMessageId()).thenReturn(lastMessageId+1);
        if(isPositive) {
            var doc = new Document();
            var file = Mockito.mock(File.class);
            var FSR = Mockito.mock(FileSystemResource.class);
            Mockito.when(fileService.processMail(chatId, doc)).thenReturn(FSR);
            Mockito.when(FSR.getFile()).thenReturn(file);
            Mockito.when(message.getDocument()).thenReturn(doc);
            Mockito.when(message.hasDocument()).thenReturn(true);
        }else {
            Mockito.when(message.getDocument()).thenReturn(null);
        }
        update.setMessage(message);

        Mockito.when(messageRepository.getMessage(chatId)).thenReturn(messageFromRepo);

        new AfterDownloadMessageCommand(sendBotMessageService,modifyDataBaseService,messageRepository,fileService).execute(update);

        var text = prepareTextForPreviewMessage(messageFromRepo);
        if(isPositive) {
            Mockito.verify(messageRepository).putMessage(chatId, messageFromRepo);
        }
        Mockito.verify(modifyDataBaseService).updateStateById(chatId, State.NOTHING_PENDING);
        Mockito.verify(sendBotMessageService).executeEditMessage(chatId,lastMessageId,textForOld, Collections.emptyList(),false);
        Mockito.verify(sendBotMessageService).createChangeableMessage(chatId,text,newCallbackData,true);
    }
}

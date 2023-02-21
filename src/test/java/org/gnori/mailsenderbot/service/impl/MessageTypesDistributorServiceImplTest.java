package org.gnori.mailsenderbot.service.impl;

import org.gnori.mailsenderbot.repository.MessageRepository;
import org.gnori.mailsenderbot.service.QueueManager;
import org.gnori.mailsenderbot.utils.CryptoTool;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
@DisplayName("Unit-level testing for MessageTypesDistributorServiceImpl")
class MessageTypesDistributorServiceImplTest {
    SendBotMessageServiceImpl  sendBotMessageService = Mockito.mock(SendBotMessageServiceImpl.class);
    ModifyDataBaseServiceImpl  modifyDataBaseService = Mockito.mock(ModifyDataBaseServiceImpl.class);
    MessageRepository messageRepository = Mockito.mock(MessageRepository.class);
    QueueManager queueManager = Mockito.mock(QueueManager.class);
    FileServiceImpl  fileService = Mockito.mock(FileServiceImpl.class);
    CryptoTool cryptoTool = Mockito.mock(CryptoTool.class);

    MessageTypesDistributorServiceImpl messageTypesDistributorService = new MessageTypesDistributorServiceImpl(sendBotMessageService,
            modifyDataBaseService,
            messageRepository,
            queueManager,
            fileService,
            cryptoTool);
    static Update update;
    static Message message;
    @BeforeEach
    public void clearData(){
        message = Mockito.mock(Message.class);
        update = Mockito.mock(Update.class);
    }
    @Test
    void distributeMessageByType() {
        Mockito.when(update.getMessage()).thenReturn(message);

        messageTypesDistributorService.distributeMessageByType(update);

        Mockito.verify(update).hasCallbackQuery();
        Mockito.verify(message).hasDocument();
        Mockito.verify(message).hasPhoto();
        Mockito.verify(message).hasText();
    }

    @Test
    void distributeMessageByTypeHasCallbackQuery() {
        var callback = Mockito.mock(CallbackQuery.class);
        var data = "data";
        Mockito.when(update.getMessage()).thenReturn(message);
        Mockito.when(update.getCallbackQuery()).thenReturn(callback);
        Mockito.when(callback.getData()).thenReturn(data);
        Mockito.when(update.hasCallbackQuery()).thenReturn(true);

        messageTypesDistributorService.distributeMessageByType(update);

        Mockito.verify(update).hasCallbackQuery();
        Mockito.verify(message,Mockito.never()).hasDocument();
        Mockito.verify(message,Mockito.never()).hasPhoto();
        Mockito.verify(message,Mockito.never()).hasText();
        Mockito.verify(callback).getData();
    }

    @Test
    void distributeMessageByTypeHasDocument() {
        var chatId = 12L;
        Mockito.when(update.getMessage()).thenReturn(message);
        Mockito.when(message.hasDocument()).thenReturn(true);
        Mockito.when(message.getChatId()).thenReturn(chatId);

        messageTypesDistributorService.distributeMessageByType(update);

        Mockito.verify(update).hasCallbackQuery();
        Mockito.verify(update).hasCallbackQuery();
        Mockito.verify(message).hasDocument();
        Mockito.verify(message,Mockito.never()).hasPhoto();
        Mockito.verify(message,Mockito.never()).hasText();
        Mockito.verify(modifyDataBaseService).findAccountDTOById(chatId);
    }

    @Test
    void distributeMessageByTypeHasPhoto() {
        var chatId = 12L;
        Mockito.when(update.getMessage()).thenReturn(message);
        Mockito.when(message.hasPhoto()).thenReturn(true);
        Mockito.when(message.getChatId()).thenReturn(chatId);

        messageTypesDistributorService.distributeMessageByType(update);

        Mockito.verify(update).hasCallbackQuery();
        Mockito.verify(update).hasCallbackQuery();
        Mockito.verify(message).hasDocument();
        Mockito.verify(message).hasPhoto();
        Mockito.verify(message,Mockito.never()).hasText();
        Mockito.verify(modifyDataBaseService).findAccountDTOById(chatId);
    }

    @Test
    void distributeMessageByTypeHasText() {
        var chatId = 12L;
        Mockito.when(update.getMessage()).thenReturn(message);
        Mockito.when(message.hasText()).thenReturn(true);
        Mockito.when(message.getChatId()).thenReturn(chatId);

        messageTypesDistributorService.distributeMessageByType(update);

        Mockito.verify(update).hasCallbackQuery();
        Mockito.verify(update).hasCallbackQuery();
        Mockito.verify(message).hasDocument();
        Mockito.verify(message).hasPhoto();
        Mockito.verify(message).hasText();
        Mockito.verify(modifyDataBaseService).findAccountDTOById(chatId);
    }

}
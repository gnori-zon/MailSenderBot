package org.gnori.client.telegram.impl;

import org.gnori.client.telegram.service.impl.FileServiceImpl;
import org.gnori.client.telegram.service.impl.MessageRepositoryService;
import org.gnori.client.telegram.service.impl.MessageTypesDistributorServiceImpl;
import org.gnori.client.telegram.service.impl.SendBotMessageServiceImpl;
import org.gnori.shared.utils.CryptoTool;
import org.gnori.store.dao.ModifyDataBaseServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
@DisplayName("Unit-level testing for MessageTypesDistributorServiceImpl")
class MessageTypesDistributorServiceImplTest {
    SendBotMessageServiceImpl  sendBotMessageService = Mockito.mock(SendBotMessageServiceImpl.class);
    ModifyDataBaseServiceImpl  modifyDataBaseService = Mockito.mock(ModifyDataBaseServiceImpl.class);
    MessageRepositoryService messageRepository = Mockito.mock(MessageRepositoryService.class);
    RabbitTemplate rabbitTemplate = Mockito.mock(RabbitTemplate.class);
    FileServiceImpl  fileService = Mockito.mock(FileServiceImpl.class);
    CryptoTool cryptoTool = Mockito.mock(CryptoTool.class);

    MessageTypesDistributorServiceImpl messageTypesDistributorService = new MessageTypesDistributorServiceImpl(sendBotMessageService,
            modifyDataBaseService,
            messageRepository,
            rabbitTemplate,
            fileService,
            cryptoTool,
            "ex-name");
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
        Mockito.verify(modifyDataBaseService).findAccountById(chatId);
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
        Mockito.verify(modifyDataBaseService).findAccountById(chatId);
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
        Mockito.verify(modifyDataBaseService).findAccountById(chatId);
    }

}
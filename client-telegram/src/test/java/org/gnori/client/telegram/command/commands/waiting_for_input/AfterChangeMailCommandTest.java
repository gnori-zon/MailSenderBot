package org.gnori.client.telegram.command.commands.waiting_for_input;

import static org.gnori.client.telegram.utils.preparers.CallbackDataPreparer.prepareCallbackDataForProfileMessage;
import static org.gnori.client.telegram.utils.preparers.TextPreparer.prepareSuccessTextForChangingLastMessage;
import static org.gnori.client.telegram.utils.preparers.TextPreparer.prepareTextForAfterInvalidMailChangeMailMessage;
import static org.gnori.client.telegram.utils.preparers.TextPreparer.prepareTextForProfileMessage;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.gnori.client.telegram.service.SendBotMessageService;
import org.gnori.client.telegram.service.impl.SendBotMessageServiceImpl;
import org.gnori.data.dto.AccountDto;
import org.gnori.store.dao.ModifyDataBaseService;
import org.gnori.store.dao.ModifyDataBaseServiceImpl;
import org.gnori.store.entity.Account;
import org.gnori.store.entity.enums.State;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

public class AfterChangeMailCommandTest {
    private final ModifyDataBaseService modifyDataBaseService = Mockito.mock(
        ModifyDataBaseServiceImpl.class);
    private final SendBotMessageService sendBotMessageService = Mockito.mock(
        SendBotMessageServiceImpl.class);
    @Test
    public void positiveTest(){
        var incomingMessage = "asdf@gmail.com";
        var textForOldMessage = prepareSuccessTextForChangingLastMessage();
        var chatId = 12L;
        var lastMessageId = 12;
        var newCallbackData = prepareCallbackDataForProfileMessage();
        var newState = State.NOTHING_PENDING;

        test(chatId,lastMessageId,incomingMessage,newState,textForOldMessage,newCallbackData,true);
    }

    @Test
    public void invalidMailTest(){
        var incomingMessage = "fal[skfa";
        var textForOldMessage = prepareTextForAfterInvalidMailChangeMailMessage();
        var chatId = 12L;
        var lastMessageId = 12;
        var newCallbackData = prepareCallbackDataForProfileMessage();
        var newState = State.NOTHING_PENDING;

        test(chatId,lastMessageId,incomingMessage,newState,textForOldMessage,newCallbackData,false);

    }
    private void test(Long chatId, Integer lastMessageId, String incomingMessage, State newState, String textForOldMessage, List<List<String>> newCallbackData, boolean positive){
        var update = prepareUpdateWithMessage(chatId,lastMessageId,incomingMessage);
        var account = new Account();
        Mockito.when(modifyDataBaseService.findAccountById(chatId)).thenReturn(Optional.of(account));

        new AfterChangeMailCommand(modifyDataBaseService,sendBotMessageService).execute(update);

        var text = prepareTextForProfileMessage(new AccountDto(account));

        if(positive){
            Mockito.verify(modifyDataBaseService).updateMailById(chatId, incomingMessage.trim());
        }
        Mockito.verify(modifyDataBaseService).updateStateById(chatId,newState);
        Mockito.verify(sendBotMessageService).executeEditMessage(chatId,lastMessageId,textForOldMessage, Collections.emptyList(),false);
        Mockito.verify(sendBotMessageService).createChangeableMessage(chatId,text,newCallbackData,true);
    }
    static Update prepareUpdateWithMessage(Long chatId, Integer lastMessageId, String incomingMessage){
        var update = new Update();
        Message message = Mockito.mock(Message.class);
        Mockito.when(message.getChatId()).thenReturn(chatId);
        Mockito.when(message.getMessageId()).thenReturn(lastMessageId+1);
        Mockito.when(message.getText()).thenReturn(incomingMessage);
        update.setMessage(message);
        return update;
    }
}

package org.gnori.mailsenderbot.command.commands.waiting_for_input;

import org.gnori.mailsenderbot.dto.AccountDto;
import org.gnori.mailsenderbot.entity.Account;
import org.gnori.mailsenderbot.entity.enums.State;
import org.gnori.mailsenderbot.service.ModifyDataBaseService;
import org.gnori.mailsenderbot.service.SendBotMessageService;
import org.gnori.mailsenderbot.service.impl.ModifyDataBaseServiceImpl;
import org.gnori.mailsenderbot.service.impl.SendBotMessageServiceImpl;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Collections;
import java.util.List;

import static org.gnori.mailsenderbot.utils.preparers.CallbackDataPreparer.prepareCallbackDataForProfileMessage;
import static org.gnori.mailsenderbot.utils.preparers.TextPreparer.*;

public class AfterChangeMailCommandTest {
    private final ModifyDataBaseService modifyDataBaseService = Mockito.mock(ModifyDataBaseServiceImpl.class);
    private final SendBotMessageService sendBotMessageService = Mockito.mock(SendBotMessageServiceImpl.class);
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
        var accountDTO = new AccountDto(new Account());
        Mockito.when(modifyDataBaseService.findAccountDTOById(chatId)).thenReturn(accountDTO);

        new AfterChangeMailCommand(modifyDataBaseService,sendBotMessageService).execute(update);

        var text = prepareTextForProfileMessage(accountDTO);

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

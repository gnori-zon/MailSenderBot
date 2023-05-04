package org.gnori.client.telegram.command.commands.waiting_for_input;

import static org.gnori.client.telegram.command.commands.waiting_for_input.AfterChangeMailCommandTest.prepareUpdateWithMessage;
import static org.gnori.client.telegram.utils.preparers.CallbackDataPreparer.prepareCallbackDataForProfileMessage;
import static org.gnori.client.telegram.utils.preparers.TextPreparer.prepareSuccessTextForChangingLastMessage;
import static org.gnori.client.telegram.utils.preparers.TextPreparer.prepareTextForAfterEmptyKeyChangeKeyForMailMessage;
import static org.gnori.client.telegram.utils.preparers.TextPreparer.prepareTextForProfileMessage;

import java.util.Collections;
import java.util.Optional;
import org.gnori.client.telegram.service.SendBotMessageService;
import org.gnori.client.telegram.service.impl.SendBotMessageServiceImpl;
import org.gnori.data.dto.AccountDto;
import org.gnori.shared.utils.CryptoTool;
import org.gnori.store.dao.ModifyDataBaseService;
import org.gnori.store.dao.ModifyDataBaseServiceImpl;
import org.gnori.store.entity.Account;
import org.gnori.store.entity.enums.State;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class AfterChangeKeyForMailCommandTest {
    private final ModifyDataBaseService modifyDataBaseService = Mockito.mock(
        ModifyDataBaseServiceImpl.class);
    private final SendBotMessageService sendBotMessageService = Mockito.mock(
        SendBotMessageServiceImpl.class);
    private final CryptoTool cryptoTool = Mockito.mock(CryptoTool.class);

    @Test
    public void positiveTest(){
        var key = "aksjdasikjd123";
        var textForOldMessage = prepareSuccessTextForChangingLastMessage();
        var chatId = 12L;
        var lastMessageId = 12;
        test(chatId,textForOldMessage,lastMessageId,key);

    }
    @Test
    public void emptyKeyTest(){
        var textForOldMessage = prepareTextForAfterEmptyKeyChangeKeyForMailMessage();
        var chatId = 12L;
        var lastMessageId = 12;
        test(chatId,textForOldMessage,lastMessageId,null);

    }

    private void test(Long chatId, String textForOldMessage, Integer lastMessageId, String key){
        var newState = State.NOTHING_PENDING;
        var newCallbackData = prepareCallbackDataForProfileMessage();
        var update = prepareUpdateWithMessage(chatId, lastMessageId, key);
        var account = new Account();
        if(key!=null) {
            Mockito.when(cryptoTool.encrypt(key)).thenReturn(key);
        }
        Mockito.when(modifyDataBaseService.findAccountById(chatId)).thenReturn(Optional.of(account));
        var text = prepareTextForProfileMessage(new AccountDto(account));

        new AfterChangeKeyForMailCommand(modifyDataBaseService,sendBotMessageService,cryptoTool).execute(update);

        if(key!=null){
            modifyDataBaseService.updateKeyForMailById(chatId, key);
        }
        Mockito.verify(modifyDataBaseService).updateStateById(chatId, newState);
        Mockito.verify(sendBotMessageService).executeEditMessage(chatId,lastMessageId,textForOldMessage, Collections.emptyList(),false);
        Mockito.verify(sendBotMessageService).createChangeableMessage(chatId,text,newCallbackData,true);
    }

}

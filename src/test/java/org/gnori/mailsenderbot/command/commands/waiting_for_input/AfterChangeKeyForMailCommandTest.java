package org.gnori.mailsenderbot.command.commands.waiting_for_input;

import org.gnori.mailsenderbot.dto.AccountDto;
import org.gnori.mailsenderbot.entity.Account;
import org.gnori.mailsenderbot.entity.enums.State;
import org.gnori.mailsenderbot.service.ModifyDataBaseService;
import org.gnori.mailsenderbot.service.SendBotMessageService;
import org.gnori.mailsenderbot.service.impl.ModifyDataBaseServiceImpl;
import org.gnori.mailsenderbot.service.impl.SendBotMessageServiceImpl;
import org.gnori.mailsenderbot.utils.CryptoTool;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Collections;

import static org.gnori.mailsenderbot.command.commands.waiting_for_input.AfterChangeMailCommandTest.prepareUpdateWithMessage;
import static org.gnori.mailsenderbot.utils.CallbackDataPreparer.prepareCallbackDataForProfileMessage;
import static org.gnori.mailsenderbot.utils.TextPreparer.*;

public class AfterChangeKeyForMailCommandTest {
    private final ModifyDataBaseService modifyDataBaseService = Mockito.mock(ModifyDataBaseServiceImpl.class);
    private final SendBotMessageService sendBotMessageService = Mockito.mock(SendBotMessageServiceImpl.class);
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
        var account = new AccountDto(new Account());
        if(key!=null) {
            Mockito.when(cryptoTool.encrypt(key)).thenReturn(key);
        }
        Mockito.when(modifyDataBaseService.findAccountById(chatId)).thenReturn(account);
        var text = prepareTextForProfileMessage(account);

        new AfterChangeKeyForMailCommand(modifyDataBaseService,sendBotMessageService,cryptoTool).execute(update);

        if(key!=null){
            modifyDataBaseService.updateKeyForMailById(chatId, key);
        }
        Mockito.verify(modifyDataBaseService).updateStateById(chatId, newState);
        Mockito.verify(sendBotMessageService).executeEditMessage(chatId,lastMessageId,textForOldMessage, Collections.emptyList(),false);
        Mockito.verify(sendBotMessageService).createChangeableMessage(chatId,text,newCallbackData,true);
    }

}

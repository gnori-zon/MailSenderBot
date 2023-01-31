package org.gnori.mailsenderbot.command.commands;

import org.gnori.mailsenderbot.command.Command;
import org.gnori.mailsenderbot.dto.AccountDto;
import org.gnori.mailsenderbot.entity.Account;
import org.gnori.mailsenderbot.entity.enums.State;
import org.gnori.mailsenderbot.service.MessageTypesDistributorService;
import org.gnori.mailsenderbot.service.ModifyDataBaseService;
import org.gnori.mailsenderbot.service.impl.MessageTypesDistributorServiceImpl;
import org.gnori.mailsenderbot.service.impl.ModifyDataBaseServiceImpl;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.List;

import static org.gnori.mailsenderbot.command.CommandName.*;


public class BackCommandTest {
    private final ModifyDataBaseService modifyDataBaseService = Mockito.mock(ModifyDataBaseServiceImpl.class);
    private final MessageTypesDistributorService messageTypesDistributorService = Mockito.mock(MessageTypesDistributorServiceImpl.class);

    private Command getCommand(){
        return new BackCommand(modifyDataBaseService,messageTypesDistributorService);
    }
    @Test
    public void first_CHANGE_MAIL__TO__BEGINNING(){
        shouldProperlyExecuteCommand(CHANGE_MAIL.getCommandName(), BEGINNING.getCommandName(), null);
    }
    @Test
    public void first_BACK__TO__BEGINNING(){
        shouldProperlyExecuteCommand(BACK.getCommandName(), BEGINNING.getCommandName(), State.NOTHING_PENDING);
    }

    @Test
    public void first_BACK__TO__PROFILE(){
        shouldProperlyExecuteCommand(BACK.getCommandName(), PROFILE.getCommandName(), State.MAIL_PENDING);
    }
    @Test
    public void first_BACK__TO__CREATE_MAILING(){
        shouldProperlyExecuteCommand(BACK.getCommandName(), CREATE_MAILING.getCommandName(), State.TITLE_PENDING);
    }
    @Test
    public void first_CLEAR_MESSAGE__BEGINNING(){
        shouldProperlyExecuteCommand(CLEAR_MESSAGE.getCommandName(), BEGINNING.getCommandName(), null);
    }
    @Test
    public void first_CHANGE_ITEM_TITLE__CREATE_MAILING(){
        shouldProperlyExecuteCommand(CHANGE_ITEM_TITLE.getCommandName(), CREATE_MAILING.getCommandName(), null);
    }



    private void shouldProperlyExecuteCommand(String firstCallBack,String expectedChangedCallBack,State state ) {
        var chatId = 12L;
        var update = prepareUpdate(chatId, firstCallBack);
        Mockito.when(modifyDataBaseService.findAccountById(chatId)).thenReturn(new AccountDto(Account.builder().state(state).build()));

        getCommand().execute(update);

        update.getCallbackQuery().setData(expectedChangedCallBack);
        if(state!=null&&state!=State.NOTHING_PENDING){
            Mockito.verify(modifyDataBaseService).updateStateById(chatId,State.NOTHING_PENDING);
        }
        Mockito.verify(messageTypesDistributorService).distributeMessageByType(update);


    }
    private Update prepareUpdate(Long chatId, String expectedFirstCallback){

        var prepareMarkup = new InlineKeyboardMarkup();
        var prepareButton = Mockito.mock(InlineKeyboardButton.class);
        var listButton = List.of(prepareButton);
        var listOfListsButtons = List.of(listButton);
        prepareMarkup.setKeyboard(listOfListsButtons);
        Update update = new Update();
        CallbackQuery callbackQuery = new CallbackQuery();
        Message message = Mockito.mock(Message.class);
        Mockito.when(message.getChatId()).thenReturn(chatId);
        Mockito.when(message.getReplyMarkup()).thenReturn(prepareMarkup);
        Mockito.when(prepareButton.getCallbackData()).thenReturn(expectedFirstCallback);
        callbackQuery.setMessage(message);
        update.setCallbackQuery(callbackQuery);
        return update;
    }

}

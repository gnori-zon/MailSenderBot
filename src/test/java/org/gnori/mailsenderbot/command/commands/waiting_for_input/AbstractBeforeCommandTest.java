package org.gnori.mailsenderbot.command.commands.waiting_for_input;

import org.gnori.mailsenderbot.command.commands.AbstractCommandTest;
import org.gnori.mailsenderbot.entity.enums.State;
import org.gnori.mailsenderbot.service.impl.ModifyDataBaseServiceImpl;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Collections;
import java.util.List;

public abstract class AbstractBeforeCommandTest extends AbstractCommandTest {
    private final ModifyDataBaseServiceImpl modifyDataBaseService = Mockito.mock(ModifyDataBaseServiceImpl.class);
    public abstract State getState();
    public ModifyDataBaseServiceImpl getModifyDataBaseService() {
        return modifyDataBaseService;
    }

    @Override
    public List<List<String>> getCallbackData() {
        return Collections.emptyList();
    }

    @Override
    @Test
    public void shouldProperlyExecuteCommand() {
        //given
        var chatId = 12L;
        var messageId = 12;
        var text = getCommandMessage();
        var newCallbackData = getCallbackData();
        var withButton = withButton();
        Update update = prepareUpdate(chatId, messageId);
        var newState = getState();

        //when
        getCommand().execute(update);

        //then
        Mockito.verify(modifyDataBaseService).updateStateById(chatId,newState);
        Mockito.verify(sendBotMessageService).executeEditMessage(chatId,messageId,text,newCallbackData,withButton);
    }
}

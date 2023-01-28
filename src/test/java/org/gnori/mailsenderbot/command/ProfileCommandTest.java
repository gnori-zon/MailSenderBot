package org.gnori.mailsenderbot.command;

import org.gnori.mailsenderbot.command.commands.ProfileCommand;
import org.gnori.mailsenderbot.dto.AccountDto;
import org.gnori.mailsenderbot.entity.enums.State;
import org.gnori.mailsenderbot.service.impl.ModifyDataBaseServiceImpl;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;

import static org.gnori.mailsenderbot.utils.UtilsCommand.prepareCallbackDataForProfileMessage;
import static org.gnori.mailsenderbot.utils.UtilsCommand.prepareTextForProfileMessage;

public class ProfileCommandTest extends AbstractCommandTest{
    private ModifyDataBaseServiceImpl modifyDataBaseService = Mockito.mock(ModifyDataBaseServiceImpl.class);
    private final AccountDto account = Mockito.mock(AccountDto.class);

    @Override
    String getCommandMessage() {
        var id = 12L; // id from abstractTest

        Mockito.when(modifyDataBaseService.findAccountById(id)).thenReturn(account);
        Mockito.when(account.getEmail()).thenReturn("email@mail.ru");
        Mockito.when(account.hasKey()).thenReturn(true);

        return prepareTextForProfileMessage(account);
    }

    @Override
    List<List<String>> getCallbackData() {
        return prepareCallbackDataForProfileMessage();
    }

    @Override
    boolean withButton() {
        return true;
    }

    @Override
    Command getCommand() {
        return new ProfileCommand(modifyDataBaseService,getSendBotMessageService());
    }

    @Test
    public void changeAccountState(){
        var id = 12L; // id from abstractTest
        shouldProperlyExecuteCommand();
        Mockito.verify(modifyDataBaseService).updateStateById(id, State.NOTHING_PENDING);
    }
}

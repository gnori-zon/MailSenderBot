package org.gnori.mailsenderbot.command.commands;

import org.gnori.mailsenderbot.command.Command;
import org.gnori.mailsenderbot.dto.AccountDto;
import org.gnori.mailsenderbot.entity.enums.State;
import org.gnori.mailsenderbot.service.impl.ModifyDataBaseServiceImpl;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;

import static org.gnori.mailsenderbot.utils.CallbackDataPreparer.prepareCallbackDataForProfileMessage;
import static org.gnori.mailsenderbot.utils.TextPreparer.prepareTextForProfileMessage;

public class ProfileCommandTest extends AbstractCommandTest {
    private final ModifyDataBaseServiceImpl modifyDataBaseService = Mockito.mock(ModifyDataBaseServiceImpl.class);
    private final AccountDto account = Mockito.mock(AccountDto.class);

    @Override
    public String getCommandMessage() {
        var id = 12L; // id from abstractTest

        Mockito.when(modifyDataBaseService.findAccountById(id)).thenReturn(account);
        Mockito.when(account.getEmail()).thenReturn("email@mail.ru");
        Mockito.when(account.hasKey()).thenReturn(true);

        return prepareTextForProfileMessage(account);
    }

    @Override
    public List<List<String>> getCallbackData() {
        return prepareCallbackDataForProfileMessage();
    }

    @Override
    public boolean withButton() {
        return true;
    }

    @Override
    public Command getCommand() {
        return new ProfileCommand(modifyDataBaseService,getSendBotMessageService());
    }

    @Test
    public void changeAccountState(){
        var id = 12L; // id from abstractTest
        shouldProperlyExecuteCommand();
        Mockito.verify(modifyDataBaseService).updateStateById(id, State.NOTHING_PENDING);
    }
}

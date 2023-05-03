package org.gnori.client.telegram.command.commands;

import static org.gnori.client.telegram.utils.preparers.CallbackDataPreparer.prepareCallbackDataForProfileMessage;
import static org.gnori.client.telegram.utils.preparers.TextPreparer.prepareTextForProfileMessage;

import java.util.List;
import java.util.Optional;
import org.gnori.client.telegram.command.Command;
import org.gnori.data.dto.AccountDto;
import org.gnori.store.dao.ModifyDataBaseServiceImpl;
import org.gnori.store.entity.Account;
import org.gnori.store.entity.enums.State;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class ProfileCommandTest extends AbstractCommandTest {
    private final ModifyDataBaseServiceImpl modifyDataBaseService = Mockito.mock(
        ModifyDataBaseServiceImpl.class);
    private final Account account = Account.builder().email("email@mail.ru").keyForMail("asjkdn").build();

    @Override
    public String getCommandMessage() {
        var id = 12L; // id from abstractTest

        Mockito.when(modifyDataBaseService.findAccountById(id)).thenReturn(Optional.of(account));

        return prepareTextForProfileMessage(new AccountDto(account));
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

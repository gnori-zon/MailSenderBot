package org.gnori.client.telegram.command.commands;

import static org.gnori.client.telegram.utils.preparers.CallbackDataPreparer.prepareCallbackDataForSendMessage;
import static org.gnori.client.telegram.utils.preparers.TextPreparer.prepareTextForSendMessage;

import java.util.List;
import java.util.Optional;
import org.gnori.client.telegram.command.Command;
import org.gnori.data.dto.AccountDto;
import org.gnori.store.dao.ModifyDataBaseServiceImpl;
import org.gnori.store.entity.Account;
import org.mockito.Mockito;

public class SendCommandTest extends AbstractCommandTest {
    private final ModifyDataBaseServiceImpl modifyDataBaseService = Mockito.mock(ModifyDataBaseServiceImpl.class);
    private final Account account = Account.builder().email("email@mail.ru").keyForMail("asjkdn").build();

    @Override
    public String getCommandMessage() {
        var id = 12L; // id from abstractTest
        Mockito.when(modifyDataBaseService.findAccountById(id)).thenReturn(Optional.of(account));

        return prepareTextForSendMessage(new AccountDto(account));
    }

    @Override
    public List<List<String>> getCallbackData() {
        return prepareCallbackDataForSendMessage(new AccountDto(account));
    }

    @Override
    public boolean withButton() {
        return true;
    }

    @Override
    public Command getCommand() {
        return new SendCommand(getSendBotMessageService(),modifyDataBaseService);
    }
}

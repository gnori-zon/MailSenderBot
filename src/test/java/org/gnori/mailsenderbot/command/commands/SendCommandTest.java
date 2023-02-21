package org.gnori.mailsenderbot.command.commands;

import org.gnori.mailsenderbot.command.Command;
import org.gnori.mailsenderbot.dto.AccountDto;
import org.gnori.mailsenderbot.service.impl.ModifyDataBaseServiceImpl;
import org.mockito.Mockito;

import java.util.List;

import static org.gnori.mailsenderbot.utils.preparers.CallbackDataPreparer.prepareCallbackDataForSendMessage;
import static org.gnori.mailsenderbot.utils.preparers.TextPreparer.prepareTextForSendMessage;

public class SendCommandTest extends AbstractCommandTest {
    private final ModifyDataBaseServiceImpl modifyDataBaseService = Mockito.mock(ModifyDataBaseServiceImpl.class);
    private final AccountDto account = Mockito.mock(AccountDto.class);

    @Override
    public String getCommandMessage() {
        var id = 12L; // id from abstractTest
        Mockito.when(modifyDataBaseService.findAccountDTOById(id)).thenReturn(account);
        Mockito.when(account.getEmail()).thenReturn("email@mail.ru");
        Mockito.when(account.hasKey()).thenReturn(true);
        return prepareTextForSendMessage(account);
    }

    @Override
    public List<List<String>> getCallbackData() {
        return prepareCallbackDataForSendMessage(account);
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

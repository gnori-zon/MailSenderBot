package org.gnori.mailsenderbot.command;

import org.gnori.mailsenderbot.command.commands.SendCommand;
import org.gnori.mailsenderbot.dto.AccountDto;
import org.gnori.mailsenderbot.service.impl.ModifyDataBaseServiceImpl;
import org.mockito.Mockito;

import java.util.List;

public class SendCommandTest extends AbstractCommandTest{
    private ModifyDataBaseServiceImpl modifyDataBaseService = Mockito.mock(ModifyDataBaseServiceImpl.class);
    private final AccountDto account = Mockito.mock(AccountDto.class);

    private String text = "*Выберите способ отправки:*";
    @Override
    String getCommandMessage() {
        return text;
    }

    @Override
    List<List<String>> getCallbackData() {
        var id = 12L; // id from abstractTest

        Mockito.when(modifyDataBaseService.findAccountById(id)).thenReturn(account);
        Mockito.when(account.getEmail()).thenReturn("email@mail.ru");
        Mockito.when(account.hasKey()).thenReturn(true);

        List<String> callbackData = List.of("SEND_ANONYMOUSLY", "SEND_CURRENT_MAIL");
        List<String> callbackDataText = List.of("Отправить анонимно", "Отпрвить почтой аккаунта");
        return List.of(callbackData, callbackDataText);
    }

    @Override
    boolean withButton() {
        return true;
    }

    @Override
    Command getCommand() {
        return new SendCommand(getSendBotMessageService(),modifyDataBaseService);
    }
}

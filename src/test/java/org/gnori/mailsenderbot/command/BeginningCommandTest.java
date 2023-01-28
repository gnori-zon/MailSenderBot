package org.gnori.mailsenderbot.command;

import org.gnori.mailsenderbot.command.commands.BeginningCommand;
import org.gnori.mailsenderbot.entity.enums.State;
import org.gnori.mailsenderbot.service.impl.ModifyDataBaseServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;

import static org.gnori.mailsenderbot.utils.UtilsCommand.prepareCallbackDataForBeginningMessage;

@DisplayName("Unit-level testing for BeginningCommandTest")
public class BeginningCommandTest extends AbstractCommandTest{
    private ModifyDataBaseServiceImpl modifyDataBaseService = Mockito.mock(ModifyDataBaseServiceImpl.class);

    @Override
    String getCommandMessage() {
        return "Выберите необходимый пункт👇🏿";
    }

    @Override
    List<List<String>> getCallbackData() {
        return prepareCallbackDataForBeginningMessage();
    }

    @Override
    boolean withButton() {
        return false;
    }

    @Override
    Command getCommand() {
        return new BeginningCommand(getSendBotMessageService(),modifyDataBaseService);
    }

    @Test
    public void isNotRegistrationCommand(){
        var id = 12L; // id from abstractTest
        shouldProperlyExecuteCommand();
        Mockito.verify(modifyDataBaseService).updateStateById(id, State.NOTHING_PENDING);
    }
}

package org.gnori.mailsenderbot.command.commands;

import org.gnori.mailsenderbot.command.Command;
import org.gnori.mailsenderbot.entity.enums.State;
import org.gnori.mailsenderbot.service.impl.ModifyDataBaseServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;

import static org.gnori.mailsenderbot.utils.CallbackDataPreparer.prepareCallbackDataForBeginningMessage;
import static org.gnori.mailsenderbot.utils.TextPreparer.prepareTextForBeginningMessage;

@DisplayName("Unit-level testing for BeginningCommandTest")
public class BeginningCommandTest extends AbstractCommandTest {
    private final ModifyDataBaseServiceImpl modifyDataBaseService = Mockito.mock(ModifyDataBaseServiceImpl.class);

    @Override
    public String getCommandMessage() {
        return prepareTextForBeginningMessage();
    }

    @Override
    public List<List<String>> getCallbackData() {
        return prepareCallbackDataForBeginningMessage();
    }

    @Override
    public boolean withButton() {
        return false;
    }

    @Override
    public Command getCommand() {
        return new BeginningCommand(getSendBotMessageService(),modifyDataBaseService);
    }

    @Test
    public void isNotRegistrationCommand(){
        var id = 12L; // id from abstractTest
        shouldProperlyExecuteCommand();
        Mockito.verify(modifyDataBaseService).updateStateById(id, State.NOTHING_PENDING);
    }
}

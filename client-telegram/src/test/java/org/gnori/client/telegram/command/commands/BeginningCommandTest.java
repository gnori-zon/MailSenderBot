package org.gnori.client.telegram.command.commands;

import static org.gnori.client.telegram.utils.preparers.CallbackDataPreparer.prepareCallbackDataForBeginningMessage;
import static org.gnori.client.telegram.utils.preparers.TextPreparer.prepareTextForBeginningMessage;

import java.util.List;
import org.gnori.client.telegram.command.Command;
import org.gnori.store.dao.ModifyDataBaseServiceImpl;
import org.gnori.store.entity.enums.State;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

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

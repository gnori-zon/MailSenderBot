package org.gnori.client.telegram.command.commands;

import static org.gnori.client.telegram.utils.preparers.CallbackDataPreparer.prepareCallbackDataForChangeItemMessage;
import static org.gnori.client.telegram.utils.preparers.TextPreparer.prepareTextForChangeItemMessage;

import java.util.List;
import org.gnori.client.telegram.command.Command;

public class ChangeItemCommandTest extends AbstractCommandTest {
    @Override
    public String getCommandMessage() {
        return prepareTextForChangeItemMessage();
    }

    @Override
    public List<List<String>> getCallbackData() {
        return prepareCallbackDataForChangeItemMessage();
    }

    @Override
    public boolean withButton() {
        return true;
    }

    @Override
    public Command getCommand() {
        return new ChangeItemCommand(getSendBotMessageService());
    }
}

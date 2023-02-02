package org.gnori.mailsenderbot.command.commands;

import org.gnori.mailsenderbot.command.Command;

import java.util.List;

import static org.gnori.mailsenderbot.utils.CallbackDataPreparer.prepareCallbackDataForChangeItemMessage;
import static org.gnori.mailsenderbot.utils.TextPreparer.prepareTextForChangeItemMessage;

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

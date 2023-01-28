package org.gnori.mailsenderbot.command;

import org.gnori.mailsenderbot.command.commands.ChangeItemCommand;

import java.util.List;

import static org.gnori.mailsenderbot.utils.UtilsCommand.prepareCallbackDataForChangeItemMessage;

public class ChangeItemCommandTest extends AbstractCommandTest{
    @Override
    String getCommandMessage() {
        return "*Выберите пункт, для изменения:*";
    }

    @Override
    List<List<String>> getCallbackData() {
        return prepareCallbackDataForChangeItemMessage();
    }

    @Override
    boolean withButton() {
        return true;
    }

    @Override
    Command getCommand() {
        return new ChangeItemCommand(getSendBotMessageService());
    }
}

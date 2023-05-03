package org.gnori.client.telegram.command.commands.waiting_for_input;

import static org.gnori.client.telegram.utils.preparers.TextPreparer.prepareSuccessTextForChangingLastMessage;

import org.gnori.client.telegram.command.Command;

public class AfterChangeTitleCommandTest extends AbstractAfterChangeItemMessageCommandTest{

    @Override
    public Command getCommand() {
        return new AfterChangeTitleCommand(getModifyDataBaseService(),getSendBotMessageService(),getMessageRepository());
    }

    @Override
    public String getIncomingMessage() {
        return "title";
    }

    @Override
    public String getTextForOldMessage() {
        return prepareSuccessTextForChangingLastMessage();
    }
}

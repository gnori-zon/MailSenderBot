package org.gnori.client.telegram.command.commands.waiting_for_input;

import static org.gnori.client.telegram.utils.preparers.TextPreparer.prepareSuccessTextForChangingLastMessage;

import org.gnori.client.telegram.command.Command;


public class AfterChangeSentDateCommandPositiveTest extends AbstractAfterChangeItemMessageCommandTest{
    @Override
    public Command getCommand() {
        return new AfterChangeSentDateCommand(getModifyDataBaseService(),getSendBotMessageService(),getMessageRepository());
    }

    @Override
    public String getIncomingMessage() {
        return "2023-11-01 12:00:00";
    }

    @Override
    public String getTextForOldMessage() {
        return prepareSuccessTextForChangingLastMessage();
    }
}

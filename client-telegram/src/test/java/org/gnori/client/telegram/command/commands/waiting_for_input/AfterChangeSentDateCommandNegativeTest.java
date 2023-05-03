package org.gnori.client.telegram.command.commands.waiting_for_input;

import static org.gnori.client.telegram.utils.preparers.TextPreparer.prepareTextInvalidDateForAfterChangeSentDateMessage;

import org.gnori.client.telegram.command.Command;

public class AfterChangeSentDateCommandNegativeTest extends AbstractAfterChangeItemMessageCommandTest{
    @Override
    public Command getCommand() {
        return new AfterChangeSentDateCommand(getModifyDataBaseService(),getSendBotMessageService(),getMessageRepository());
    }

    @Override
    public boolean isPositive() {
        return false;
    }

    @Override
    public String getIncomingMessage() {
        return "2020-11-01 12:00:00";
    }

    @Override
    public String getTextForOldMessage() {
        return prepareTextInvalidDateForAfterChangeSentDateMessage();
    }
}
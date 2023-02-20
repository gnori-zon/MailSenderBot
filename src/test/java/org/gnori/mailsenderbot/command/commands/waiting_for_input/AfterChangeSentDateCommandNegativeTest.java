package org.gnori.mailsenderbot.command.commands.waiting_for_input;

import org.gnori.mailsenderbot.command.Command;

import static org.gnori.mailsenderbot.utils.preparers.TextPreparer.prepareTextInvalidDateForAfterChangeSentDateMessage;

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
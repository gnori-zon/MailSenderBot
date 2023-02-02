package org.gnori.mailsenderbot.command.commands.waiting_for_input;

import org.gnori.mailsenderbot.command.Command;

import static org.gnori.mailsenderbot.utils.TextPreparer.prepareTextForAfterNotNumberChangeCountForRecipientsMessage;

public class AfterChangeCountForRecipientCommandNegativeTest extends AbstractAfterChangeItemMessageCommandTest{
    @Override
    public Command getCommand() {
        return new AfterChangeCountForRecipientCommand(getModifyDataBaseService(),getSendBotMessageService(),getMessageRepository());
    }

    @Override
    public String getIncomingMessage() {
        return "asd";
    }

    @Override
    public boolean isPositive() {
        return false;
    }

    @Override
    public String getTextForOldMessage() {
        return prepareTextForAfterNotNumberChangeCountForRecipientsMessage();
    }
}


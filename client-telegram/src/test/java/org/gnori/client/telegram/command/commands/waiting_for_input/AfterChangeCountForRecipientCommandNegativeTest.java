package org.gnori.client.telegram.command.commands.waiting_for_input;

import static org.gnori.client.telegram.utils.preparers.TextPreparer.prepareTextForAfterNotNumberChangeCountForRecipientsMessage;

import org.gnori.client.telegram.command.Command;

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

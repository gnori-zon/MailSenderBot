package org.gnori.client.telegram.command.commands.waiting_for_input;

import static org.gnori.client.telegram.utils.preparers.TextPreparer.prepareSuccessTextForChangingLastMessage;

import org.gnori.client.telegram.command.Command;

public class AfterChangeRecipientsCommandTest extends AbstractAfterChangeItemMessageCommandTest{
    @Override
    public Command getCommand() {
        return new AfterChangeRecipientsCommand(getModifyDataBaseService(),getSendBotMessageService(),getMessageRepository());
    }

    @Override
    public String getIncomingMessage() {
        return "email@gmail.com, gmail@mail.com";
    }

    @Override
    public String getTextForOldMessage() {
        return prepareSuccessTextForChangingLastMessage();
    }
}

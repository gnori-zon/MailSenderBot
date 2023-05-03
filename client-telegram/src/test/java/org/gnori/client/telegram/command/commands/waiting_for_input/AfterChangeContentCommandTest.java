package org.gnori.client.telegram.command.commands.waiting_for_input;

import static org.gnori.client.telegram.utils.preparers.TextPreparer.prepareSuccessTextForChangingLastMessage;

import org.gnori.client.telegram.command.Command;

public class AfterChangeContentCommandTest extends AbstractAfterChangeItemMessageCommandTest{
    @Override
    public Command getCommand() {
        return new AfterChangeContentCommand(getModifyDataBaseService(),getSendBotMessageService(),getMessageRepository());
    }

    @Override
    public String getIncomingMessage() {
        return "Text for content mail is very very very very very very very very very very very very very " +
                "very very very very very very very very very very very very very LONG";
    }

    @Override
    public String getTextForOldMessage() {
        return prepareSuccessTextForChangingLastMessage();
    }
}

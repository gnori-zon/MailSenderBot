package org.gnori.mailsenderbot.command.commands.waiting_for_input;

import org.gnori.mailsenderbot.command.Command;

import static org.gnori.mailsenderbot.utils.TextPreparer.prepareSuccessTextForChangingLastMessage;

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

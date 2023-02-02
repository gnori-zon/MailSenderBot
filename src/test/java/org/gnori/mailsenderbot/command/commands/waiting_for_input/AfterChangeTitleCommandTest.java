package org.gnori.mailsenderbot.command.commands.waiting_for_input;

import org.gnori.mailsenderbot.command.Command;

import static org.gnori.mailsenderbot.utils.TextPreparer.prepareSuccessTextForChangingLastMessage;

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

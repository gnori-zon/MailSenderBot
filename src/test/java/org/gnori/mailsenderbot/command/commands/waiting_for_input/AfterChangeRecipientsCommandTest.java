package org.gnori.mailsenderbot.command.commands.waiting_for_input;

import org.gnori.mailsenderbot.command.Command;

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
        return "✔Успешно";
    }
}

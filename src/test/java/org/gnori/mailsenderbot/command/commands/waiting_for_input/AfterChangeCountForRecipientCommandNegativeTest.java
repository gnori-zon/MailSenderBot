package org.gnori.mailsenderbot.command.commands.waiting_for_input;

import org.gnori.mailsenderbot.command.Command;

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
        return "❌Необходимо ввести число, попробуйте снова";
    }
}


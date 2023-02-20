package org.gnori.mailsenderbot.command.commands.waiting_for_input;

import org.gnori.mailsenderbot.command.Command;
import org.gnori.mailsenderbot.entity.enums.State;

import static org.gnori.mailsenderbot.entity.enums.State.MAIL_PENDING;
import static org.gnori.mailsenderbot.utils.preparers.TextPreparer.prepareTextForBeforeChangeMailMessage;

public class BeforeChangeMailCommandTest extends AbstractBeforeCommandTest{
    @Override
    public String getCommandMessage() {
        return prepareTextForBeforeChangeMailMessage();
    }

    @Override
    public boolean withButton() {
        return true;
    }

    @Override
    public Command getCommand() {
        return new BeforeChangeMailCommand(getModifyDataBaseService(),getSendBotMessageService());
    }

    @Override
    public State getState() {
        return MAIL_PENDING;
    }
}

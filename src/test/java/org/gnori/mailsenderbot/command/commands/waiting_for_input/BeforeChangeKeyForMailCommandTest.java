package org.gnori.mailsenderbot.command.commands.waiting_for_input;

import org.gnori.mailsenderbot.command.Command;
import org.gnori.mailsenderbot.entity.enums.State;

import static org.gnori.mailsenderbot.entity.enums.State.KEY_FOR_MAIL_PENDING;
import static org.gnori.mailsenderbot.utils.preparers.TextPreparer.prepareTextForBeforeChangeKeyForMailMessage;

public class BeforeChangeKeyForMailCommandTest extends AbstractBeforeCommandTest{
    @Override
    public String getCommandMessage() {
        return prepareTextForBeforeChangeKeyForMailMessage();
    }

    @Override
    public boolean withButton() {
        return true;
    }

    @Override
    public Command getCommand() {
        return new BeforeChangeKeyForMailCommand(getModifyDataBaseService(),getSendBotMessageService());
    }

    @Override
    public State getState() {
        return KEY_FOR_MAIL_PENDING;
    }
}

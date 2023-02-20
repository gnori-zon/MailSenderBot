package org.gnori.mailsenderbot.command.commands.waiting_for_input;

import org.gnori.mailsenderbot.command.Command;
import org.gnori.mailsenderbot.entity.enums.State;

import static org.gnori.mailsenderbot.entity.enums.State.RECIPIENTS_PENDING;
import static org.gnori.mailsenderbot.utils.preparers.TextPreparer.prepareTextForBeforeChangeRecipientsMessage;

public class BeforeChangeRecipientsCommandTest extends AbstractBeforeCommandTest{
    @Override
    public String getCommandMessage() {
        return prepareTextForBeforeChangeRecipientsMessage();
    }

    @Override
    public boolean withButton() {
        return true;
    }

    @Override
    public Command getCommand() {
        return new BeforeChangeRecipientsCommand(getSendBotMessageService(),getModifyDataBaseService());
    }

    @Override
    public State getState() {
        return RECIPIENTS_PENDING;
    }
}

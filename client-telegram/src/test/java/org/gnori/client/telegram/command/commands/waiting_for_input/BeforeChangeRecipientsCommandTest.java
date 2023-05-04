package org.gnori.client.telegram.command.commands.waiting_for_input;

import static org.gnori.client.telegram.utils.preparers.TextPreparer.prepareTextForBeforeChangeRecipientsMessage;
import static org.gnori.store.entity.enums.State.RECIPIENTS_PENDING;

import org.gnori.client.telegram.command.Command;
import org.gnori.store.entity.enums.State;

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

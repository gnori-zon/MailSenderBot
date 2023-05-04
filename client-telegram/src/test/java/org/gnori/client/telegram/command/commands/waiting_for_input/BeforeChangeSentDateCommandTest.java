package org.gnori.client.telegram.command.commands.waiting_for_input;

import static org.gnori.client.telegram.utils.preparers.TextPreparer.prepareTextForBeforeChangeSentDateMessage;

import org.gnori.client.telegram.command.Command;
import org.gnori.store.entity.enums.State;

public class BeforeChangeSentDateCommandTest extends AbstractBeforeCommandTest{
    @Override
    public String getCommandMessage() {
        return prepareTextForBeforeChangeSentDateMessage();
    }

    @Override
    public boolean withButton() {
        return true;
    }

    @Override
    public Command getCommand() {
        return new BeforeChangeSentDateCommand(getSendBotMessageService(),getModifyDataBaseService());
    }

    @Override
    public State getState() {
        return State.SENT_DATE_PENDING;
    }
}

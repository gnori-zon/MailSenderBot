package org.gnori.client.telegram.command.commands.waiting_for_input;

import static org.gnori.client.telegram.utils.preparers.TextPreparer.prepareTextForBeforeChangeTitleMessage;
import static org.gnori.store.entity.enums.State.TITLE_PENDING;

import org.gnori.client.telegram.command.Command;
import org.gnori.store.entity.enums.State;

public class BeforeChangeTitleCommandTest extends AbstractBeforeCommandTest{
    @Override
    public String getCommandMessage() {
        return prepareTextForBeforeChangeTitleMessage();
    }

    @Override
    public boolean withButton() {
        return true;
    }

    @Override
    public Command getCommand() {
        return new BeforeChangeTitleCommand(getSendBotMessageService(),getModifyDataBaseService());
    }

    @Override
    public State getState() {
        return TITLE_PENDING;
    }
}

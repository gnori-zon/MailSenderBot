package org.gnori.client.telegram.command.commands.waiting_for_input;

import static org.gnori.client.telegram.utils.preparers.TextPreparer.prepareTextForBeforeChangeAnnexMessage;
import static org.gnori.store.entity.enums.State.ANNEX_PENDING;

import org.gnori.client.telegram.command.Command;
import org.gnori.store.entity.enums.State;

public class BeforeChangeAnnexCommandTest extends AbstractBeforeCommandTest {
    @Override
    public String getCommandMessage() {
        return prepareTextForBeforeChangeAnnexMessage();
    }

    @Override
    public boolean withButton() {
        return true;
    }

    @Override
    public Command getCommand() {
        return new BeforeChangeAnnexCommand(getSendBotMessageService(),getModifyDataBaseService());
    }

    @Override
    public State getState() {
        return ANNEX_PENDING;
    }
}

package org.gnori.mailsenderbot.command.commands.waiting_for_input;

import org.gnori.mailsenderbot.command.Command;
import org.gnori.mailsenderbot.entity.enums.State;

import java.util.Collections;
import java.util.List;

import static org.gnori.mailsenderbot.entity.enums.State.ANNEX_PENDING;
import static org.gnori.mailsenderbot.utils.TextPreparer.prepareTextForBeforeChangeAnnexMessage;

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

package org.gnori.client.telegram.command.commands.waiting_for_input;

import static org.gnori.client.telegram.utils.preparers.TextPreparer.prepareTextForBeforeChangeKeyForMailMessage;
import static org.gnori.store.entity.enums.State.KEY_FOR_MAIL_PENDING;

import org.gnori.client.telegram.command.Command;
import org.gnori.store.entity.enums.State;

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

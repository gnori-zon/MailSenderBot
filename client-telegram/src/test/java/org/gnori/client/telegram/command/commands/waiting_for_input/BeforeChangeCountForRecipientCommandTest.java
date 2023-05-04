package org.gnori.client.telegram.command.commands.waiting_for_input;

import static org.gnori.client.telegram.utils.preparers.TextPreparer.prepareTextForBeforeChangeCountForRecipientsMessage;
import static org.gnori.store.entity.enums.State.COUNT_FOR_RECIPIENT_PENDING;

import org.gnori.client.telegram.command.Command;
import org.gnori.store.entity.enums.State;

public class BeforeChangeCountForRecipientCommandTest extends AbstractBeforeCommandTest{
    @Override
    public String getCommandMessage() {
        return prepareTextForBeforeChangeCountForRecipientsMessage();
    }

    @Override
    public boolean withButton() {
        return true;
    }

    @Override
    public Command getCommand() {
        return new BeforeChangeCountForRecipientCommand(getSendBotMessageService(),getModifyDataBaseService());
    }

    @Override
    public State getState() {
        return COUNT_FOR_RECIPIENT_PENDING;
    }
}

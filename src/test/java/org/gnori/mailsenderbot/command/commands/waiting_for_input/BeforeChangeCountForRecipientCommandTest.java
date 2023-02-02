package org.gnori.mailsenderbot.command.commands.waiting_for_input;

import org.gnori.mailsenderbot.command.Command;
import org.gnori.mailsenderbot.entity.enums.State;

import static org.gnori.mailsenderbot.entity.enums.State.COUNT_FOR_RECIPIENT_PENDING;
import static org.gnori.mailsenderbot.utils.TextPreparer.prepareTextForBeforeChangeCountForRecipientsMessage;

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

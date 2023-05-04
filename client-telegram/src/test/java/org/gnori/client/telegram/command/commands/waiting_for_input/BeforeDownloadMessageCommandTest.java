package org.gnori.client.telegram.command.commands.waiting_for_input;

import static org.gnori.client.telegram.utils.preparers.TextPreparer.prepareTextForBeforeDownloadMessage;
import static org.gnori.store.entity.enums.State.DOWNLOAD_MESSAGE_PENDING;

import org.gnori.client.telegram.command.Command;
import org.gnori.store.entity.enums.State;

public class BeforeDownloadMessageCommandTest extends AbstractBeforeCommandTest{
    @Override
    public String getCommandMessage() {
        return prepareTextForBeforeDownloadMessage();
    }

    @Override
    public boolean withButton() {
        return true;
    }

    @Override
    public Command getCommand() {
        return new BeforeDownloadMessageCommand(getSendBotMessageService(),getModifyDataBaseService());
    }

    @Override
    public State getState() {
        return DOWNLOAD_MESSAGE_PENDING;
    }
}

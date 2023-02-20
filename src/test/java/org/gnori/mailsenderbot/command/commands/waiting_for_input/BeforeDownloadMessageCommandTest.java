package org.gnori.mailsenderbot.command.commands.waiting_for_input;

import org.gnori.mailsenderbot.command.Command;
import org.gnori.mailsenderbot.entity.enums.State;

import static org.gnori.mailsenderbot.entity.enums.State.DOWNLOAD_MESSAGE_PENDING;
import static org.gnori.mailsenderbot.utils.preparers.TextPreparer.prepareTextForBeforeDownloadMessage;

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

package org.gnori.mailsenderbot.command.commands.waiting_for_input;

import org.gnori.mailsenderbot.command.Command;
import org.gnori.mailsenderbot.entity.enums.State;

import static org.gnori.mailsenderbot.entity.enums.State.TITLE_PENDING;

public class BeforeChangeTitleCommandTest extends AbstractBeforeCommandTest{
    @Override
    public String getCommandMessage() {
        return "*Введите новый заголовок: *";
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

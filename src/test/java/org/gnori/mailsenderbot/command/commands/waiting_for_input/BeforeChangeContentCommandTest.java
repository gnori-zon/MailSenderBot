package org.gnori.mailsenderbot.command.commands.waiting_for_input;

import org.gnori.mailsenderbot.command.Command;
import org.gnori.mailsenderbot.entity.enums.State;

import static org.gnori.mailsenderbot.entity.enums.State.CONTENT_PENDING;

public class BeforeChangeContentCommandTest extends AbstractBeforeCommandTest{
    @Override
    public String getCommandMessage() {
        return "*Введите новый основной текст: *";
    }

    @Override
    public boolean withButton() {
        return true;
    }

    @Override
    public Command getCommand() {
        return new BeforeChangeContentCommand(getSendBotMessageService(),getModifyDataBaseService());
    }

    @Override
    public State getState() {
        return CONTENT_PENDING;
    }
}

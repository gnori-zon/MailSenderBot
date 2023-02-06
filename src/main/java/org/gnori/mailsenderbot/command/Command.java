package org.gnori.mailsenderbot.command;

import org.telegram.telegrambots.meta.api.objects.Update;

/**
 *  Command interface for describing the implementation of telegram bot commands.
 */
public interface Command {
    /**
     * Main method, which is executing command logic.
     * @param update provided {@link Update} object with all the needed data for doing command.
     */
    void execute(Update update);
}

package org.gnori.client.telegram.command.commands.state;

import org.telegram.telegrambots.meta.api.objects.Update;

public enum StateCommandType {

    UNDEFINED;

    public static StateCommandType of(Update update) {

        return UNDEFINED;
    }
}

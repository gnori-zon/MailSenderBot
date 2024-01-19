package org.gnori.client.telegram.command.commands.callback;

import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Optional;

public enum CallbackCommandType {

    MAILING_HISTORY,
    CREATE_MAILING,
    PROFILE_INFO,
    UNDEFINED;

    public static CallbackCommandType of(Update update) {

        return Optional.of(update.getCallbackQuery())
                .map(CallbackQuery::getData)
                .map(CallbackCommandType::valueOf)
                .orElse(UNDEFINED);
    }
}

package org.gnori.client.telegram.command.commands.callback;

import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Optional;

public enum CallbackCommandType {

    MAILING_HISTORY,
    CREATE_MAILING,
    PROFILE_INFO,
    CHANGE_MAIL,
    CHANGE_KEY,
    HELP,
    SEND,
    UPLOAD_MESSAGE,
    CLEAR_MESSAGE,
    CHANGE_MESSAGE_ITEM,
    CHANGE_MESSAGE_ITEM_TITLE,
    CHANGE_MESSAGE_ITEM_TEXT,
    CHANGE_MESSAGE_ITEM_ANNEX,
    CHANGE_MESSAGE_ITEM_RECIPIENTS,
    CHANGE_MESSAGE_ITEM_COUNT_FOR_RECIPIENTS,
    CHANGE_MESSAGE_ITEM_SENT_DATE,
    SEND_ANONYMOUSLY,
    SEND_CURRENT_MAIL,
    UNDEFINED;

    public static CallbackCommandType of(Update update) {

        return Optional.of(update.getCallbackQuery())
                .map(CallbackQuery::getData)
                .map(CallbackCommandType::valueOf)
                .orElse(UNDEFINED);
    }
}

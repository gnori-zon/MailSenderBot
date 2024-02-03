package org.gnori.client.telegram.service.command.commands.callback;

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
    BACK,
    UNDEFINED;

    public static final  String DATA_DELIMITER = "#";

    public static CallbackCommandType of(CallbackQuery callbackQuery) {

        return Optional.of(callbackQuery.getData())
                .map(CallbackCommandType::valueOf)
                .orElse(UNDEFINED);
    }

    public static boolean isBackCommand(Update update) {

        return Optional.ofNullable(update.getCallbackQuery())
                .map(CallbackQuery::getData)
                .map(callbackData -> BACK.name().equals(callbackData))
                .orElse(false);
    }
}

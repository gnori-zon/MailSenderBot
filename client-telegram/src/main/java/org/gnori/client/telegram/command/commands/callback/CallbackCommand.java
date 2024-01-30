package org.gnori.client.telegram.command.commands.callback;

import org.gnori.client.telegram.command.Command;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public interface CallbackCommand extends Command<CallbackCommandType> {

    default List<String> getCallbackParams(Update update) {

        return Optional.ofNullable(update)
                .map(Update::getCallbackQuery)
                .map(CallbackQuery::getData)
                .map(callbackData -> callbackData.split(CallbackCommandType.DATA_DELIMITER))
                .map(callbackData -> Arrays.stream(callbackData).skip(1).toList())
                .orElse(Collections.emptyList());
    }
}

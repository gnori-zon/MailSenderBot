package org.gnori.client.telegram.command.commands.text;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Getter(AccessLevel.PRIVATE)
@RequiredArgsConstructor
public enum TextCommandType {
    START("/start"),
    UNDEFINED(null);

    private final String raw;

    public static TextCommandType of(Update update) {

        return Optional.ofNullable(update.getMessage())
                .map(Message::getText)
                .map(text -> typeMap().getOrDefault(text, UNDEFINED))
                .orElse(UNDEFINED);
    }

    private static Map<String, TextCommandType> typeMap() {

        return Arrays.stream(TextCommandType.values())
                .collect(Collectors.toUnmodifiableMap(TextCommandType::getRaw, Function.identity()));
    }
}

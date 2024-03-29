package org.gnori.client.telegram.service.command.commands.text;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Getter(AccessLevel.PRIVATE)
@RequiredArgsConstructor
public enum TextCommandType {

    START("/start"),
    UNDEFINED(null);

    private final String raw;

    public static TextCommandType of(Message message) {

        return typeMap().getOrDefault(message.getText(), UNDEFINED);
    }

    private static Map<String, TextCommandType> typeMap() {

        return Arrays.stream(TextCommandType.values())
                .filter(type -> type.getRaw() != null)
                .collect(Collectors.toUnmodifiableMap(TextCommandType::getRaw, Function.identity()));
    }
}

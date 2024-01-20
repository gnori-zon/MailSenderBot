package org.gnori.client.telegram.command.commands.state;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.gnori.store.entity.enums.State;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Getter(AccessLevel.PRIVATE)
@AllArgsConstructor
public enum StateCommandType {

    CHANGING_KEY(State.KEY_FOR_MAIL_PENDING),
    CHANGING_MAIL(State.MAIL_PENDING),
    CHANGING_MESSAGE_ITEM_ANNEX(State.MESSAGE_ANNEX_PENDING),
    CHANGING_MESSAGE_ITEM_CONT_FOR_RECIPIENTS(State.MESSAGE_COUNT_FOR_RECIPIENT_PENDING),
    CHANGING_MESSAGE_ITEM_RECIPIENTS(State.MESSAGE_RECIPIENTS_PENDING),
    CHANGING_MESSAGE_ITEM_SENT_DATE(State.MESSAGE_SENT_DATE_PENDING),
    CHANGING_MESSAGE_ITEM_TEXT(State.MESSAGE_TEXT_PENDING),
    CHANGING_MESSAGE_ITEM_TITLE(State.MESSAGE_TITLE_PENDING),
    UPLOADING_MESSAGE(State.UPLOAD_MESSAGE_PENDING),
    UNDEFINED(null);


    private final State state;

    public static StateCommandType of(State state) {

        return typeMap().getOrDefault(state, UNDEFINED);
    }

    private static Map<State, StateCommandType> typeMap() {

        return Arrays.stream(StateCommandType.values())
                .collect(Collectors.toUnmodifiableMap(StateCommandType::getState, Function.identity()));
    }
}

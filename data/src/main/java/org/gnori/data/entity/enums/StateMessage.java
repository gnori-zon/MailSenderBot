package org.gnori.data.entity.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum StateMessage {

    QUEUE("⏳in the queue"),
    SUCCESS("✔sent"),
    FAIL("❌not sent");

    private final String text;
}

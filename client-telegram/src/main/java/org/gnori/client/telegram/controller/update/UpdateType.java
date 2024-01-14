package org.gnori.client.telegram.controller.update;

import org.telegram.telegrambots.meta.api.objects.Update;

public enum UpdateType {
    EMPTY,
    VALID,
    UNSUPPORTED;

    static UpdateType of(Update update) {

        if (update == null) {
            return EMPTY;
        } else if (update.hasMessage() || update.hasCallbackQuery()) {
            return VALID;
        } else {
            return UNSUPPORTED;
        }
    }
}
